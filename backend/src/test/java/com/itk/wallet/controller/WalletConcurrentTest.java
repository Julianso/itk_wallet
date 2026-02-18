package com.itk.wallet.controller;

import com.google.gson.Gson;
import com.itk.wallet.api.request.WalletUpdateRequest;
import com.itk.wallet.dto.WalletDTO;
import com.itk.wallet.model.Wallet;
import com.itk.wallet.repository.WalletRepository;
import com.itk.wallet.utils.OperationType;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class WalletConcurrentTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Test
    public void testWalletDepositConcurrent() throws InterruptedException {

        int numberOfRequests = 1000;
        long amountPerRequest = 1L;

        UUID uuid = UUID.randomUUID();
        WalletDTO stub = new WalletDTO();
        stub.setId(uuid);
        stub.setAmount(100L);

        walletRepository.save(modelMapper.map(stub, Wallet.class));

        WalletUpdateRequest updateRequest = new WalletUpdateRequest();
        updateRequest.setId(stub.getId());
        updateRequest.setOperationType(OperationType.DEPOSIT);
        updateRequest.setAmount(amountPerRequest);

        Gson gson = new Gson();
        String json = gson.toJson(updateRequest);

        ExecutorService executor = Executors.newFixedThreadPool(numberOfRequests);

        for (int i = 0; i < numberOfRequests; i++) {
            executor.submit(() -> {
                try {
                    mockMvc.perform(
                                    MockMvcRequestBuilders.post("/api/v1/wallet")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(json)
                                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }
}