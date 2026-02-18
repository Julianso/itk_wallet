package com.itk.wallet.repository;

import com.itk.wallet.model.Wallet;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
@Transactional
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

}
