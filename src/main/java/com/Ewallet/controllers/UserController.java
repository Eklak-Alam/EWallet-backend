package com.Ewallet.controllers;

import com.Ewallet.request.UserRequest;
import com.Ewallet.response.UserResponse;
import com.Ewallet.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Create User
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest) {
        UserResponse response = userService.createUserWithAccounts(userRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get User by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    // Get All Users
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Update User
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        UserResponse response = userService.updateUser(id, userRequest);
        return ResponseEntity.ok(response);
    }

    // Delete User
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Get User by Email
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        UserResponse response = userService.getUserByEmail(email);
        return ResponseEntity.ok(response);
    }

    // Get User by Username
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        UserResponse response = userService.getUserByUsername(username);
        return ResponseEntity.ok(response);
    }

    // Get User by Phone
    @GetMapping("/phone/{phone}")
    public ResponseEntity<UserResponse> getUserByPhone(@PathVariable String phone) {
        UserResponse response = userService.getUserByPhone(phone);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/login")
    public ResponseEntity<UserResponse> loginWithUsernameAndPhone(
            @RequestParam String userName,
            @RequestParam String phoneNumber
    ) {
        return ResponseEntity.ok(userService.getUserByUsernameAndPhone(userName, phoneNumber));
    }

}
