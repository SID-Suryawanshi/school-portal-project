package com.college.dbmsproject;

// This DTO will hold the nested user info
public class UserDTO {
    private String email;
    private String role;

    public UserDTO(User user) {
        this.email = user.getEmail();
        this.role = user.getRole().name();
    }

    // Getters
    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}