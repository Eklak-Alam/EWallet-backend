package com.Ewallet.controllers;

import com.Ewallet.entities.Wallet;
import com.Ewallet.services.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {

    @Autowired
    private WalletService walletService;

    // ✅ Get wallet by user ID
    @GetMapping("/user/{userId}")
    public Wallet getWalletByUserId(@PathVariable Long userId) {
        return walletService.getWalletByUserId(userId);
    }

    // ✅ Delete wallet by user ID
    @DeleteMapping("/user/{userId}")
    public String deleteWalletByUserId(@PathVariable Long userId) {
        walletService.deleteWalletByUserId(userId);
        return "Wallet deleted for user ID: " + userId;
    }
}
