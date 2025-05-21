package com.Ewallet.services;
import com.Ewallet.entities.Bank;
import com.Ewallet.entities.User;
import com.Ewallet.entities.Wallet;
import com.Ewallet.exceptions.DuplicateResourceException;
import com.Ewallet.exceptions.ResourceNotFoundException;
import com.Ewallet.repos.BankRepo;
import com.Ewallet.repos.UserRepo;
import com.Ewallet.repos.WalletRepo;
import com.Ewallet.response.UserResponse;
import com.Ewallet.request.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepository;
    private final BankRepo bankRepository;
    private final WalletRepo walletRepository;

    @Transactional
    public UserResponse createUserWithAccounts(UserRequest userRequest) {
        // Validate user doesn't exist
        validateUserDoesNotExist(userRequest);

        // Convert UserRequest to User entity
        User user = convertToUserEntity(userRequest);

        // Save user
        User savedUser = userRepository.save(user);

        // Create and save bank account
        Bank bankAccount = createBankAccount(savedUser);
        bankRepository.save(bankAccount);

        // Create and save wallet
        Wallet wallet = createWallet(savedUser);
        walletRepository.save(wallet);

        // Convert to response
        return convertToUserResponse(savedUser);
    }

    private void validateUserDoesNotExist(UserRequest userRequest) {
        String fullPhoneNumber = userRequest.getCountryCode() + userRequest.getPhoneNumber();

        if (userRepository.findByUserName(userRequest.getUserName()).isPresent()) {
            throw new DuplicateResourceException("Username already taken");
        }
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already registered");
        }
        if (userRepository.findByPhoneNumber(fullPhoneNumber).isPresent()) {
            throw new DuplicateResourceException("Phone number already registered");
        }
    }

    private User convertToUserEntity(UserRequest userRequest) {
        return User.builder()
                .name(userRequest.getName())
                .userName(userRequest.getUserName())
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getCountryCode() + userRequest.getPhoneNumber())
                .role(User.Role.USER) // Default role
                .build();
    }

    private Bank createBankAccount(User user) {
        return Bank.builder()
                .accountNumber("ACCT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12))
                .phoneNumber(user.getPhoneNumber())
                .balance(getDefaultBalanceForCountry(user.getPhoneNumber().substring(0, 3))) // First 3 chars are country code
                .currency(getCurrencyForCountry(user.getPhoneNumber().substring(0, 3)))
                .user(user)
                .build();
    }

    private Wallet createWallet(User user) {
        return Wallet.builder()
                .phoneNumber(user.getPhoneNumber())
                .user(user)
                .balance(0.0)
                .currency(getCurrencyForCountry(user.getPhoneNumber().substring(0, 3)))
                .build();
    }

    private UserResponse convertToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .userName(user.getUserName())
                .email(user.getEmail())
                .countryCode(user.getPhoneNumber().substring(0, 3)) // Extract country code
                .phoneNumber(user.getPhoneNumber().substring(3)) // Extract phone number without country code
                .createdOn(user.getCreatedOn())
                .updatedOn(user.getUpdatedOn())
                .build();
    }

    private double getDefaultBalanceForCountry(String countryCode) {
        return switch (countryCode) {
            case "+91" -> 100.0;  // India
            case "+1" -> 10.0;     // USA/Canada
            case "+44" -> 50.0;    // UK
            case "+81" -> 1000.0;  // Japan
            default -> 50.0;       // Default
        };
    }

    private String getCurrencyForCountry(String countryCode) {
        return switch (countryCode) {
            case "+91" -> "INR";  // India
            case "+1" -> "USD";   // USA/Canada
            case "+44" -> "GBP";  // UK
            case "+81" -> "JPY";  // Japan
            case "+49" -> "EUR";  // Germany
            default -> "USD";     // Default
        };
    }




    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToUserResponse(user);
    }

    // Get All Users
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    // Update User
    @Transactional
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Check if new email/phone is already taken by another user
        if (!existingUser.getEmail().equals(userRequest.getEmail())) {
            if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
                throw new DuplicateResourceException("Email already registered");
            }
        }

        String newPhone = userRequest.getCountryCode() + userRequest.getPhoneNumber();
        if (!existingUser.getPhoneNumber().equals(newPhone)) {
            if (userRepository.findByPhoneNumber(newPhone).isPresent()) {
                throw new DuplicateResourceException("Phone number already registered");
            }
        }

        // Update user fields
        existingUser.setName(userRequest.getName());
        existingUser.setUserName(userRequest.getUserName());
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setPhoneNumber(newPhone);

        User updatedUser = userRepository.save(existingUser);
        return convertToUserResponse(updatedUser);
    }

    // Delete User
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Delete associated bank and wallet first
        bankRepository.deleteByUserId(id);
        walletRepository.deleteByUserId(id);
        userRepository.delete(user);
    }

    // Get User by Email
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return convertToUserResponse(user);
    }

    // Get User by Username
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return convertToUserResponse(user);
    }

    // Get User by Phone
    public UserResponse getUserByPhone(String phone) {
        User user = userRepository.findByPhoneNumber(phone)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with phone: " + phone));
        return convertToUserResponse(user);
    }
}