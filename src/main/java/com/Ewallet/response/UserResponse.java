package com.Ewallet.response;

import com.Ewallet.entities.User;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String userName;
    private String email;
    private String countryCode;
    private String phoneNumber;
    private User.Role role;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
}