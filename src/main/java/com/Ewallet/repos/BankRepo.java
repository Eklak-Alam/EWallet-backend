package com.Ewallet.repos;

import com.Ewallet.entities.Bank;
import com.Ewallet.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankRepo extends JpaRepository<Bank, Long> {
    Optional<Bank> findByUser(User user);
    void deleteByUserId(Long userId);
}