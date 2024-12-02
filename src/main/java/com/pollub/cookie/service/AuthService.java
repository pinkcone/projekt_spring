package com.pollub.cookie.service;

import com.pollub.cookie.dto.AuthRequestDTO;
import com.pollub.cookie.dto.AuthResponseDTO;

public interface AuthService {

    AuthResponseDTO authenticate(AuthRequestDTO authRequest);
}
