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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private UserRepo userRepository;
    private BankRepo bankRepository;
    private WalletRepo walletRepository;
    private PasswordEncoder passwordEncoder;


    public UserService(UserRepo userRepository, BankRepo bankRepository, WalletRepo walletRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.bankRepository = bankRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse createUserWithAccounts(UserRequest userRequest) {
        // Validate user doesn't exist
        validateUserDoesNotExist(userRequest);
        userRequest.validatePasswordsMatch();

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
        User user = new User();
        user.setName(userRequest.getName());
        user.setUserName(userRequest.getUserName());
        user.setEmail(userRequest.getEmail());
        user.setPhoneNumber(userRequest.getCountryCode() + userRequest.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setRole(User.Role.USER);
        return user;
    }


    private Bank createBankAccount(User user) {
        Bank bank = new Bank();
        bank.setAccountNumber("ACCT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12));
        bank.setPhoneNumber(user.getPhoneNumber());
        bank.setBalance(getDefaultBalanceForCountry(user.getPhoneNumber().substring(0, 3))); // First 3 chars are country code
        bank.setCurrency(getCurrencyForCountry(user.getPhoneNumber().substring(0, 3)));
        bank.setUser(user);
        return bank;
    }


    private Wallet createWallet(User user) {
        Wallet wallet = new Wallet();
        wallet.setPhoneNumber(user.getPhoneNumber());
        wallet.setUser(user);
        wallet.setBalance(0.0);
        wallet.setCurrency(getCurrencyForCountry(user.getPhoneNumber().substring(0, 3)));
        return wallet;
    }


    private UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setUserName(user.getUserName());
        response.setEmail(user.getEmail());
        response.setCountryCode(user.getPhoneNumber().substring(0, 3));
        response.setPhoneNumber(user.getPhoneNumber().substring(3));
        response.setRole(user.getRole());
        response.setCreatedOn(user.getCreatedOn());
        response.setUpdatedOn(user.getUpdatedOn());
        return response;
    }


    private double getDefaultBalanceForCountry(String countryCode) {
        return switch (countryCode) {
            case "+91" -> 100.0;  // India
            case "+1" -> 1.0;     // USA/Canada
            case "+44" -> 5.0;    // UK
            case "+81" -> 100.0;  // Japan
            default -> 1.0;       // Default
        };
    }

    private String getCurrencyForCountry(String countryCode) {
        return switch (countryCode) {
            case "+91" -> "INR";  // India
            case "+1" -> "USD";   // USA/Canada
            case "+44" -> "GBP";  // UK
            case "+81" -> "JPY";  // Japan
            case "+49" -> "EUR";  // Germany
            case "+33" -> "FR";  // France
            case "+86" -> "CN";  //China
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

        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
            userRequest.validatePasswordsMatch();
            existingUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }


        // Update user fields
        existingUser.setName(userRequest.getName());
        existingUser.setUserName(userRequest.getUserName());
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setPhoneNumber(newPhone);
        existingUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));

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


    public UserResponse getUserByUsernameAndPhone(String userName, String phoneNumber) {
        // You decide the default country code (or make it dynamic)
        String PhoneNumber = phoneNumber;

        User user = userRepository.findByUserNameAndPhoneNumber(userName, phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username and phone number"));

        return convertToUserResponse(user);
    }


}