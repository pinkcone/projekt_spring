package com.pollub.cookie.controller;

import com.pollub.cookie.dto.AuthRequestDTO;
import com.pollub.cookie.dto.AuthResponseDTO;
import com.pollub.cookie.dto.UserDTO;
import com.pollub.cookie.service.AuthService;
import com.pollub.cookie.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @Operation(summary = "Logowanie użytkownika", description = "Endpoint do logowania użytkownika i uzyskania tokena uwierzytelniającego.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logowanie udane",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDTO.class),
                            examples = @ExampleObject(value = "{ \"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\" }"))),
            @ApiResponse(responseCode = "401", description = "Nieprawidłowe dane logowania",
                    content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO authRequest) {
        try {
            AuthResponseDTO authResponse = authService.authenticate(authRequest);
            logger.info("Logowanie udane dla użytkownika: {}", authRequest.getEmail());
            return ResponseEntity.ok(authResponse);
        } catch (AuthenticationException e) {
            logger.warn("Nieudane logowanie dla użytkownika: {}. Szczegóły: {}", authRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @Operation(summary = "Rejestracja nowego użytkownika", description = "Endpoint do rejestracji nowego użytkownika w systemie.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rejestracja udana",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class),
                            examples = @ExampleObject(value = "{ \"id\": 1, \"email\": \"newuser@example.com\", \"haslo\": \"password123\", \"imie\": \"Jan\", \"nazwisko\": \"Kowalski\", \"adres\": \"ul. Kwiatowa 1\", \"numerTelefonu\": \"123456789\", \"rola\": \"USER\", \"zamowieniaIds\": null, \"koszykId\": null }"))),
            @ApiResponse(responseCode = "400", description = "Błędne dane wejściowe",
                    content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody UserDTO userDTO) {
        UserDTO registeredUser = userService.createUser(userDTO);
        return ResponseEntity.ok(registeredUser);
    }
}
