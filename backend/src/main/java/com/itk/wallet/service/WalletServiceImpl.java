package com.itk.wallet.service;

import com.itk.wallet.api.request.WalletUpdateRequest;
import com.itk.wallet.dto.WalletDTO;
import com.itk.wallet.model.Wallet;
import com.itk.wallet.repository.WalletRepository;
import com.itk.wallet.service.base.WalletService;
import com.itk.wallet.utils.OperationType;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Validated
@Component
@Service
@Transactional
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    @Override
    public WalletDTO findById(UUID id) {

        if(id == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "wallet id must not be null"
            );
        }

        Optional<Wallet> wallet = this.walletRepository.findById(id);

        if(wallet.isPresent()) {
            return this.modelMapper.map(wallet, WalletDTO.class);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "wallet not found"
            );
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public synchronized void save(WalletUpdateRequest walletUpdateRequest) {

        if(walletUpdateRequest.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "wallet id must not be null"
            );
        }

        Optional<Wallet> wallet = this.walletRepository.findById(walletUpdateRequest.getId());

        if(wallet.isPresent()) {

            WalletDTO walletDTO = this.modelMapper.map(wallet, WalletDTO.class);

            OperationType operationType = walletUpdateRequest.getOperationType();

            switch (operationType) {
                case DEPOSIT:
                    walletDTO.deposit(walletUpdateRequest.getAmount());
                    break;
                case WITHDRAW:
                    walletDTO.withdraw(walletUpdateRequest.getAmount());
                    break;
            }

            this.walletRepository.save(this.modelMapper.map(walletDTO, Wallet.class));

        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "wallet not found"
            );
        }
    }

}
