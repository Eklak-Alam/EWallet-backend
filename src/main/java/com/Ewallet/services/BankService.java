package com.Ewallet.services;

import com.Ewallet.entities.Bank;
import com.Ewallet.entities.User;
import com.Ewallet.exceptions.ResourceNotFoundException;
import com.Ewallet.repos.BankRepo;
import com.Ewallet.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankService {

    @Autowired
    private BankRepo bankRepo;

    @Autowired
    private UserRepo userRepo;

    // ✅ Get bank by user ID
    public Bank getBankByUserId(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        return bankRepo.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Bank not found for user ID: " + userId));
    }

    // ✅ Delete bank by user ID
    public void deleteBankByUserId(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
        bankRepo.deleteByUserId(userId);
    }
}
