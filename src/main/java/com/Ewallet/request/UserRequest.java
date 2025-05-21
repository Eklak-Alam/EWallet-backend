package com.Ewallet.request;

import com.Ewallet.entities.User;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    private String userName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Country code is required")
    @Pattern(regexp = "^\\+[0-9]{1,3}$", message = "Country code must start with + followed by 1-3 digits")
    private String countryCode;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be 10-15 digits")
    private String phoneNumber;

    public @NotBlank(message = "Name is required") @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "Name is required") @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters") String name) {
        this.name = name;
    }

    public @NotBlank(message = "Username is required") @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters") @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores") String getUserName() {
        return userName;
    }

    public void setUserName(@NotBlank(message = "Username is required") @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters") @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores") String userName) {
        this.userName = userName;
    }

    public @NotBlank(message = "Email is required") @Email(message = "Email should be valid") String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "Email is required") @Email(message = "Email should be valid") String email) {
        this.email = email;
    }

    public @NotBlank(message = "Country code is required") @Pattern(regexp = "^\\+[0-9]{1,3}$", message = "Country code must start with + followed by 1-3 digits") String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(@NotBlank(message = "Country code is required") @Pattern(regexp = "^\\+[0-9]{1,3}$", message = "Country code must start with + followed by 1-3 digits") String countryCode) {
        this.countryCode = countryCode;
    }

    public @NotBlank(message = "Phone number is required") @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be 10-15 digits") String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@NotBlank(message = "Phone number is required") @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be 10-15 digits") String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}