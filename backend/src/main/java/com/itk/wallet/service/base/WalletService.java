package com.itk.wallet.service.base;

import com.itk.wallet.dto.WalletDTO;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@Component
public interface WalletService {

    WalletDTO findById(UUID id);

    void save(WalletDTO wallet);

}
