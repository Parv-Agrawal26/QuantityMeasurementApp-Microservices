package com.apps.authservice.service;

import com.apps.authservice.dto.AuthRequestDTO;
import com.apps.authservice.dto.AuthResponseDTO;
import com.apps.authservice.dto.RegisterRequestDTO;

public interface IAuthService {
    AuthResponseDTO login(AuthRequestDTO request);
    AuthResponseDTO register(RegisterRequestDTO request);
    void logout(String token);
}
