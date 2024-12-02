package com.pollub.cookie.service.impl;

import com.pollub.cookie.dto.AuthRequestDTO;
import com.pollub.cookie.dto.AuthResponseDTO;
import com.pollub.cookie.security.JwtTokenProvider;
import com.pollub.cookie.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public AuthResponseDTO authenticate(AuthRequestDTO authRequest) {
        logger.info("Próba autentykacji użytkownika: {}", authRequest.getEmail());
        try {
            Authentication authentication = authenticateUser(authRequest);
            String token = jwtTokenProvider.generateToken(authentication);
            logger.info("Autentykacja udana dla użytkownika: {}", authRequest.getEmail());
            return new AuthResponseDTO(token);
        } catch (BadCredentialsException e) {
            logger.warn("Błędne dane logowania dla użytkownika: {}", authRequest.getEmail());
            throw e;
        } catch (AuthenticationException e) {
            logger.error("Błąd autentykacji dla użytkownika: {}. Szczegóły: {}", authRequest.getEmail(), e.getMessage());
            throw e;
        }
    }

    private Authentication authenticateUser(AuthRequestDTO authRequest) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );
    }
}
