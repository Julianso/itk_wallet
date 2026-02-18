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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class WalletControllerTest {

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
    void testWalletGet() throws Exception {

        UUID uuid = UUID.randomUUID();
        WalletDTO stub = new WalletDTO();
        stub.setId(uuid);
        stub.setAmount(100L);

        walletRepository.save(modelMapper.map(stub, Wallet.class));

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/wallets/" + stub.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(stub.getId().toString()))
            .andExpect(jsonPath("$.amount").value(stub.getAmount()))
            .andExpect(status().isOk());
    }

    @Test
    void testWalletGetNotFound() throws Exception {

        UUID uuid = UUID.randomUUID();
        WalletDTO stub = new WalletDTO();
        stub.setId(uuid);
        stub.setAmount(100L);

        walletRepository.save(modelMapper.map(stub, Wallet.class));

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/wallets/" +
                            UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testWalletDeposit() throws Exception {

        UUID uuid = UUID.randomUUID();
        WalletDTO stub = new WalletDTO();
        stub.setId(uuid);
        stub.setAmount(100L);

        walletRepository.save(modelMapper.map(stub, Wallet.class));

        WalletUpdateRequest updateRequest = new WalletUpdateRequest();
        updateRequest.setId(stub.getId());
        updateRequest.setOperationType(OperationType.DEPOSIT);
        updateRequest.setAmount(1L);

        Gson gson = new Gson();

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/wallet")
            .contentType(MediaType.APPLICATION_JSON)
            .content(gson.toJson(updateRequest))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/wallets/" + stub.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(stub.getId().toString()))
            .andExpect(jsonPath("$.amount").value(101))
            .andExpect(status().isOk());
    }

    @Test
    void testWalletWithdraw() throws Exception {

        UUID uuid = UUID.randomUUID();
        WalletDTO stub = new WalletDTO();
        stub.setId(uuid);
        stub.setAmount(100L);

        walletRepository.save(modelMapper.map(stub, Wallet.class));

        WalletUpdateRequest updateRequest = new WalletUpdateRequest();
        updateRequest.setId(stub.getId());
        updateRequest.setOperationType(OperationType.WITHDRAW);
        updateRequest.setAmount(100L);

        Gson gson = new Gson();

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/wallet")
            .contentType(MediaType.APPLICATION_JSON)
            .content(gson.toJson(updateRequest))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/wallets/" +
                            stub.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(stub.getId().toString()))
            .andExpect(jsonPath("$.amount").value(0))
            .andExpect(status().isOk());
    }

    @Test
    void testWalletInsufficientFunds() throws Exception {

        UUID uuid = UUID.randomUUID();
        WalletDTO stub = new WalletDTO();
        stub.setId(uuid);
        stub.setAmount(100L);

        walletRepository.save(modelMapper.map(stub, Wallet.class));

        WalletUpdateRequest updateRequest = new WalletUpdateRequest();
        updateRequest.setId(stub.getId());
        updateRequest.setOperationType(OperationType.WITHDRAW);
        updateRequest.setAmount(101L);

        Gson gson = new Gson();

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/wallet")
            .contentType(MediaType.APPLICATION_JSON)
            .content(gson.toJson(updateRequest))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testWalletFundsLimitHasReached() throws Exception {

        UUID uuid = UUID.randomUUID();
        WalletDTO stub = new WalletDTO();
        stub.setId(uuid);
        stub.setAmount(Long.MAX_VALUE);

        walletRepository.save(modelMapper.map(stub, Wallet.class));

        WalletUpdateRequest updateRequest = new WalletUpdateRequest();
        updateRequest.setId(stub.getId());
        updateRequest.setOperationType(OperationType.DEPOSIT);
        updateRequest.setAmount(1L);

        Gson gson = new Gson();

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/wallet")
            .contentType(MediaType.APPLICATION_JSON)
            .content(gson.toJson(updateRequest))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testWalletUpdateNotFound() throws Exception {

        UUID uuid = UUID.randomUUID();
        WalletDTO stub = new WalletDTO();
        stub.setId(uuid);
        stub.setAmount(Long.MAX_VALUE);

        walletRepository.save(modelMapper.map(stub, Wallet.class));

        WalletUpdateRequest updateRequest = new WalletUpdateRequest();
        updateRequest.setId(UUID.randomUUID());
        updateRequest.setOperationType(OperationType.DEPOSIT);
        updateRequest.setAmount(1L);

        Gson gson = new Gson();

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/wallet")
            .contentType(MediaType.APPLICATION_JSON)
            .content(gson.toJson(updateRequest))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testWalletUpdateIncorrectId() throws Exception {

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/wallet")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"id\": \"\",\"operationType\": \"DEPOSIT\"," +
                    "\"amount\": \"100\"}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/wallet")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"id\": \"227c4a47-08a3-4b6d-86b8-\"," +
                    "\"operationType\": \"DEPOSIT\",\"amount\": \"100\"}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/wallet")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"uuid\": \"227c4a47-08a3-4b6d-86b8-460904c71845\"," +
                    "\"operationType\": \"DEPOSIT\",\"amount\": \"100\"}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testWalletUpdateIncorrectAmount() throws Exception {

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/wallet")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"id\": \"227c4a47-08a3-4b6d-86b8-460904c71845\"," +
                    "\"operationType\": \"DEPOSIT\",\"amount\": \"-100\"}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/wallet")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"id\": \"227c4a47-08a3-4b6d-86b8-460904c71845\"," +
                    "\"operationType\": \"DEPOSIT\",\"amount\": \"ONE_HUNDRED\"}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testWalletUpdateIncorrectOperationType() throws Exception {

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/wallet")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"id\": \"227c4a47-08a3-4b6d-86b8-460904c71845\"," +
                    "\"operationType\": \"DELETE\",\"amount\": \"100\"}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testWalletUpdateIncorrectJson() throws Exception {

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/wallet")
            .contentType(MediaType.APPLICATION_JSON)
            .content("")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/wallet")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/wallet")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testWalletUpdateIncorrectPath() throws Exception {

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/wallet/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"id\": \"227c4a47-08a3-4b6d-86b8-460904c71845\"," +
                    "\"operationType\": \"DEPOSIT\",\"amount\": \"100\"}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void testWalletUpdateIncorrectMethod() throws Exception {

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/wallet")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"id\": \"227c4a47-08a3-4b6d-86b8-460904c71845\"}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isMethodNotAllowed());
    }

}