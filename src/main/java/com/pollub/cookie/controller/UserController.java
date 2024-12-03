package com.pollub.cookie.controller;

import com.pollub.cookie.dto.OrderDTO;
import com.pollub.cookie.dto.UserDTO;
import com.pollub.cookie.dto.UserUpdateDTO;
import com.pollub.cookie.service.OrderService;
import com.pollub.cookie.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Błędne dane użytkownika", content = @Content),
        @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp", content = @Content),
        @ApiResponse(responseCode = "403", description = "Brak dostępu", content = @Content),
        @ApiResponse(responseCode = "404", description = "Użytkownik nie został znaleziony", content = @Content)
})
public class UserController {

    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public UserController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @Operation(summary = "Tworzy nowego użytkownika", description = "Endpoint do tworzenia nowego użytkownika w systemie.")
    @ApiResponse(responseCode = "201", description = "Użytkownik został pomyślnie utworzony",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDTO.class),
                    examples = @ExampleObject(value = "{ \"id\": 1, \"email\": \"newuser@example.com\", \"haslo\": \"password123\", \"imie\": \"Jan\", \"nazwisko\": \"Kowalski\", \"adres\": \"ul. Kwiatowa 1\", \"numerTelefonu\": \"123456789\", \"rola\": \"USER\" }")))
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.status(201).body(createdUser);
    }

    @Operation(summary = "Pobiera użytkownika po ID", description = "Endpoint do pobierania szczegółów użytkownika na podstawie jego ID.")
    @ApiResponse(responseCode = "200", description = "Pomyślnie pobrano użytkownika",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDTO.class),
                    examples = @ExampleObject(value = "{ \"id\": 1, \"email\": \"user@example.com\", \"haslo\": \"password123\", \"imie\": \"Jan\", \"nazwisko\": \"Kowalski\", \"adres\": \"ul. Kwiatowa 1\", \"numerTelefonu\": \"123456789\", \"rola\": \"USER\" }")))
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO userDTO = userService.getUserById(id);
        return ResponseEntity.ok(userDTO);
    }

    @Operation(summary = "Pobiera wszystkich użytkowników", description = "Endpoint do pobierania listy wszystkich użytkowników w systemie. Dostępny tylko dla administratorów.")
    @ApiResponse(responseCode = "200", description = "Pomyślnie pobrano listę użytkowników",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDTO.class),
                    examples = @ExampleObject(value = "[ { \"id\": 1, \"email\": \"user1@example.com\", \"haslo\": \"password123\", \"imie\": \"Jan\", \"nazwisko\": \"Kowalski\", \"adres\": \"ul. Kwiatowa 1\", \"numerTelefonu\": \"123456789\", \"rola\": \"USER\" }, { \"id\": 2, \"email\": \"admin@example.com\", \"haslo\": \"adminpass\", \"imie\": \"Anna\", \"nazwisko\": \"Nowak\", \"adres\": \"ul. Różana 2\", \"numerTelefonu\": \"987654321\", \"rola\": \"ADMIN\" } ]")))
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Aktualizuje istniejącego użytkownika", description = "Endpoint do aktualizacji danych istniejącego użytkownika na podstawie jego ID.")
    @ApiResponse(responseCode = "200", description = "Użytkownik został pomyślnie zaktualizowany",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDTO.class),
                    examples = @ExampleObject(value = "{ \"id\": 1, \"email\": \"updateduser@example.com\", \"haslo\": \"newpassword123\", \"imie\": \"Janusz\", \"nazwisko\": \"Kowalski\", \"adres\": \"ul. Kwiatowa 1A\", \"numerTelefonu\": \"123456780\", \"rola\": \"USER\" }")))
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        UserDTO updatedUser = userService.updateUser(id, userUpdateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Usuwa użytkownika po ID", description = "Endpoint do usuwania istniejącego użytkownika na podstawie jego ID. Dostępny tylko dla administratorów.")
    @ApiResponse(responseCode = "204", description = "Użytkownik został pomyślnie usunięty", content = @Content)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Pobiera aktualnie zalogowanego użytkownika", description = "Endpoint do pobierania danych aktualnie zalogowanego użytkownika.")
    @ApiResponse(responseCode = "200", description = "Pomyślnie pobrano dane użytkownika",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDTO.class),
                    examples = @ExampleObject(value = "{ \"id\": 1, \"email\": \"user@example.com\", \"haslo\": \"password123\", \"imie\": \"Jan\", \"nazwisko\": \"Kowalski\", \"adres\": \"ul. Kwiatowa 1\", \"numerTelefonu\": \"123456789\", \"rola\": \"USER\" }")))
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        UserDTO userDTO = userService.getUserByEmail(email);
        return ResponseEntity.ok(userDTO);
    }

    @Operation(summary = "Pobiera zamówienia użytkownika", description = "Endpoint do pobierania listy zamówień zalogowanego użytkownika.")
    @ApiResponse(responseCode = "200", description = "Pomyślnie pobrano listę zamówień",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OrderDTO.class),
                    examples = @ExampleObject(value = "[ { \"id\": 1, \"userEmail\": \"user@example.com\", \"products\": [ { \"productId\": 1, \"quantity\": 2 }, { \"productId\": 3, \"quantity\": 1 } ], \"totalPrice\": 59.99, \"status\": \"PLACED\", \"orderDate\": \"2024-04-27T14:30:00\" }, { \"id\": 3, \"userEmail\": \"user@example.com\", \"products\": [ { \"productId\": 2, \"quantity\": 1 } ], \"totalPrice\": 19.99, \"status\": \"SHIPPED\", \"orderDate\": \"2024-04-28T09:15:00\" } ]")))
    @GetMapping("/me/orders")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrderDTO>> getMyOrders(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<OrderDTO> orders = orderService.getOrdersByUserEmail(email);
        return ResponseEntity.ok(orders);
    }
}
