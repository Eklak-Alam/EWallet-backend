package com.Ewallet.controllers;

import com.Ewallet.entities.Bank;
import com.Ewallet.services.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/banks")
public class BankController {

    @Autowired
    private BankService bankService;

    // ✅ Get bank by user ID
    @GetMapping("/user/{userId}")
    public Bank getBankByUserId(@PathVariable Long userId) {
        return bankService.getBankByUserId(userId);
    }

    // ✅ Delete bank by user ID
    @DeleteMapping("/user/{userId}")
    public String deleteBankByUserId(@PathVariable Long userId) {
        bankService.deleteBankByUserId(userId);
        return "Bank account deleted for user ID: " + userId;
    }
}
