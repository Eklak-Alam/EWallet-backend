package com.Ewallet.services;

import com.Ewallet.entities.User;
import com.Ewallet.exceptions.AuthenticationFailedException;
import com.Ewallet.request.LoginRequest;
import com.Ewallet.response.LoginResponse;
import com.Ewallet.repos.UserRepo;
import com.Ewallet.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private AuthenticationManager authenticationManager;
    private CustomUserDetailsService userDetailsService;
    private JwtUtils jwtUtils;
    private UserRepo userRepository;

    public AuthService(AuthenticationManager authenticationManager, CustomUserDetailsService userDetailsService, JwtUtils jwtUtils, UserRepo userRepository) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        // Authenticate user
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUserName(),
                            loginRequest.getPassword()
                    )
            );

            // Rest of your login logic
        } catch (BadCredentialsException e) {
            throw new AuthenticationFailedException("Invalid username or password");
        }

        // Get user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUserName());

        // Generate token
        String token = jwtUtils.generateToken(userDetails);

        // Get user info
        User user = userRepository.findByUserName(loginRequest.getUserName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Build response
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setId(user.getId());
        response.setUserName(user.getUserName());
        response.setEmail(user.getEmail());

        return response;
    }
}