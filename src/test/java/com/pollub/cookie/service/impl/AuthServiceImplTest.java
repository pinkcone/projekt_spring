package com.pollub.cookie.service.impl;

import com.pollub.cookie.dto.AuthRequestDTO;
import com.pollub.cookie.dto.AuthResponseDTO;
import com.pollub.cookie.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenValidCredentials_whenAuthenticate_thenReturnAuthResponse() {
        AuthRequestDTO authRequest = new AuthRequestDTO();
        authRequest.setEmail("user@example.com");
        authRequest.setPassword("password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("mocked-jwt-token");

        AuthResponseDTO response = authService.authenticate(authRequest);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
    }

    @Test
    void givenInvalidCredentials_whenAuthenticate_thenThrowBadCredentialsException() {
        AuthRequestDTO authRequest = new AuthRequestDTO();
        authRequest.setEmail("user@example.com");
        authRequest.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.authenticate(authRequest));
    }
}