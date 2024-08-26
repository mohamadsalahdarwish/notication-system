package com.example.notificationsystem.model;

public class AuthenticationResponse {

    private String jwt;

    // Default constructor
    public AuthenticationResponse() {
    }

    // Parameterized constructor
    public AuthenticationResponse(String jwt) {
        this.jwt = jwt;
    }

    // Getter and Setter
    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}

