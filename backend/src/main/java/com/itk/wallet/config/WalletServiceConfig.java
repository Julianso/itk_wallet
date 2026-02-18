package com.itk.wallet.config;

import com.itk.wallet.service.base.WalletService;
import com.itk.wallet.service.WalletServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.itk.wallet.service")
public class WalletServiceConfig {

    @Bean
    public WalletService walletService() {
        return new WalletServiceImpl();
    }
}