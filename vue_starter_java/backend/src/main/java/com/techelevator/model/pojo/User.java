package com.techelevator.model.pojo;

import javax.validation.constraints.AssertTrue;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * User
 */
public class User {
    @NotBlank(message = "Username is required")
    @Length(max=32, message="Username cannot be over 32 characters")
    private String username;

    @NotBlank(message="Email is required")
    @Email(message = "Email needs to be valid")
    @Length(max=255, message="Email cannot be over 255 characters")
    private String email;
    private long id;

    @NotBlank(message = "Password is required")
    @Length(max=32, message="Password cannot be over 32 characters")
    private String password;
    private String confirmPassword;
    
    private String role = "user";

    @JsonIgnore
    @AssertTrue(message = "Passwords must match")
    public boolean isPasswordMatching() {
        if (password != null) {
            return password.equals(confirmPassword);
        }
        return true;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }
    
    /**
     * @return the role
     */
    public String getRole() {
    	return role;
    }
    
    /**
     * @param role the role to set
     */
    public void setRole(String role) {
    	this.role = role;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}