package com.serikscode.customer;

public record CustomerRegistrationRequest(
        String name,
        String email,
        Integer age
) { }
