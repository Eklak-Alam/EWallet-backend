package com.Ewallet.repos;

import com.Ewallet.entities.User;
import com.Ewallet.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepo extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUser(User user);
    void deleteByUserId(Long userId);
}