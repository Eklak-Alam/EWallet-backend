package com.Ewallet.repos;

import com.Ewallet.entities.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankRepo extends JpaRepository<Bank, Long> {
    void deleteByUserId(Long userId);
}