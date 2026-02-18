package com.itk.wallet.controller;

import com.itk.wallet.api.request.WalletUpdateRequest;
import com.itk.wallet.dto.WalletDTO;
import com.itk.wallet.service.base.WalletService;
import com.itk.wallet.utils.OperationType;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RestController
public class WalletController {

    @Autowired
    private WalletService walletService;

    @GetMapping(value = "/api/v1/wallets/{uuid}", produces = "application/json")
    WalletDTO getWalletByUUID(@PathVariable UUID uuid) {
        return walletService.findById(uuid);
    }

    @PostMapping(value = "/api/v1/wallet", consumes = "application/json", produces = "application/json")
    ResponseEntity<HttpStatus> updateWallet(@Valid @RequestBody WalletUpdateRequest updateWalletRequestDTO) {

        WalletDTO walletDTO = walletService.findById(updateWalletRequestDTO.getId());

        OperationType operationType = updateWalletRequestDTO.getOperationType();

        switch (operationType) {
            case DEPOSIT:
                walletDTO.deposit(updateWalletRequestDTO.getAmount());
                walletService.save(walletDTO);
                break;
            case WITHDRAW:
                walletDTO.withdraw(updateWalletRequestDTO.getAmount());
                walletService.save(walletDTO);
                break;
        }

        return ResponseEntity.ok(HttpStatus.OK);
    }

}