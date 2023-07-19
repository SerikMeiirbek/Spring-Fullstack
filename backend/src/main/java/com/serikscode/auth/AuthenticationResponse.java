package com.serikscode.auth;

import com.serikscode.dto.CustomerDTO;

public record AuthenticationResponse(
        String token,
        CustomerDTO customerDTO

) {
}
