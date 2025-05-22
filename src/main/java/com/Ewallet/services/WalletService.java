package com.Ewallet.services;


import com.Ewallet.entities.User;
import com.Ewallet.entities.Wallet;
import com.Ewallet.exceptions.ResourceNotFoundException;
import com.Ewallet.repos.UserRepo;
import com.Ewallet.repos.WalletRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WalletService {

    @Autowired
    private WalletRepo walletRepo;

    @Autowired
    private UserRepo userRepo;

    // ✅ Get wallet by user ID
    public Wallet getWalletByUserId(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        return walletRepo.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user ID: " + userId));
    }

    // ✅ Delete wallet by user ID
    public void deleteWalletByUserId(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
        walletRepo.deleteByUserId(userId);
    }
}
