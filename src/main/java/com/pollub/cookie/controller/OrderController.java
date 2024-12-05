package com.pollub.cookie.controller;

import com.pollub.cookie.dto.OrderDTO;
import com.pollub.cookie.dto.PlaceOrderDTO;
import com.pollub.cookie.exception.ResourceNotFoundException;
import com.pollub.cookie.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/orders")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Błędne dane wejściowe", content = @Content),
        @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp", content = @Content),
        @ApiResponse(responseCode = "403", description = "Brak dostępu", content = @Content),
        @ApiResponse(responseCode = "404", description = "Zamówienie nie zostało znalezione", content = @Content)
})
public class OrderController {

    private final OrderService orderService;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Składa nowe zamówienie", description = "Endpoint do składania nowego zamówienia przez zalogowanego użytkownika.")
    @ApiResponse(responseCode = "201", description = "Zamówienie zostało pomyślnie złożone",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OrderDTO.class),
                    examples = @ExampleObject(value = "{ \"id\": 1, \"userEmail\": \"user@example.com\", \"products\": [ { \"productId\": 1, \"quantity\": 2 }, { \"productId\": 3, \"quantity\": 1 } ], \"totalPrice\": 59.99, \"status\": \"PLACED\", \"orderDate\": \"2024-04-27T14:30:00\" }")))
    @PostMapping("/place")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> placeOrder(@Valid @RequestBody PlaceOrderDTO placeOrderDTO, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        logger.info("Otrzymano żądanie złożenia zamówienia od użytkownika: {}", email);
        try {
            OrderDTO createdOrder = orderService.placeOrder(email, placeOrderDTO);
            logger.info("Zamówienie zostało pomyślnie złożone dla użytkownika: {}", email);
            return ResponseEntity.status(201).body(createdOrder);
        } catch (Exception e) {
            logger.error("Błąd podczas składania zamówienia dla użytkownika: {}", email, e);
            throw e;
        }
    }

    @Operation(summary = "Pobiera zamówienie po ID", description = "Endpoint do pobierania szczegółów zamówienia na podstawie jego ID.")
    @ApiResponse(responseCode = "200", description = "Pomyślnie pobrano zamówienie",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OrderDTO.class),
                    examples = @ExampleObject(value = "{ \"id\": 1, \"userEmail\": \"user@example.com\", \"products\": [ { \"productId\": 1, \"quantity\": 2 }, { \"productId\": 3, \"quantity\": 1 } ], \"totalPrice\": 59.99, \"status\": \"PLACED\", \"orderDate\": \"2024-04-27T14:30:00\" }")))
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        OrderDTO orderDTO = orderService.getOrderById(id);
        return ResponseEntity.ok(orderDTO);
    }

    @Operation(summary = "Pobiera wszystkie zamówienia", description = "Endpoint do pobierania listy wszystkich zamówień w systemie. Dostępny tylko dla administratorów.")
    @ApiResponse(responseCode = "200", description = "Pomyślnie pobrano listę zamówień",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OrderDTO.class),
                    examples = @ExampleObject(value = "[ { \"id\": 1, \"userEmail\": \"user@example.com\", \"products\": [ { \"productId\": 1, \"quantity\": 2 }, { \"productId\": 3, \"quantity\": 1 } ], \"totalPrice\": 59.99, \"status\": \"PLACED\", \"orderDate\": \"2024-04-27T14:30:00\" }, { \"id\": 2, \"userEmail\": \"admin@example.com\", \"products\": [ { \"productId\": 2, \"quantity\": 1 } ], \"totalPrice\": 19.99, \"status\": \"SHIPPED\", \"orderDate\": \"2024-04-28T09:15:00\" } ]")))
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Aktualizuje istniejące zamówienie", description = "Endpoint do aktualizacji danych istniejącego zamówienia na podstawie jego ID.")
    @ApiResponse(responseCode = "200", description = "Zamówienie zostało pomyślnie zaktualizowane",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OrderDTO.class),
                    examples = @ExampleObject(value = "{ \"id\": 1, \"userEmail\": \"user@example.com\", \"products\": [ { \"productId\": 1, \"quantity\": 3 }, { \"productId\": 3, \"quantity\": 2 } ], \"totalPrice\": 89.97, \"status\": \"CONFIRMED\", \"orderDate\": \"2024-04-27T14:30:00\" }")))
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable Long id, @Valid @RequestBody OrderDTO orderDTO) {
        OrderDTO updatedOrder = orderService.updateOrder(id, orderDTO);
        return ResponseEntity.ok(updatedOrder);
    }

    @Operation(summary = "Usuwa zamówienie po ID", description = "Endpoint do usuwania istniejącego zamówienia na podstawie jego ID.")
    @ApiResponse(responseCode = "204", description = "Zamówienie zostało pomyślnie usunięte", content = @Content)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Pobiera zamówienia użytkownika", description = "Endpoint do pobierania listy zamówień zalogowanego użytkownika.")
    @ApiResponse(responseCode = "200", description = "Pomyślnie pobrano listę zamówień",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OrderDTO.class),
                    examples = @ExampleObject(value = "[ { \"id\": 1, \"userEmail\": \"user@example.com\", \"products\": [ { \"productId\": 1, \"quantity\": 2 }, { \"productId\": 3, \"quantity\": 1 } ], \"totalPrice\": 59.99, \"status\": \"PLACED\", \"orderDate\": \"2024-04-27T14:30:00\" }, { \"id\": 3, \"userEmail\": \"user@example.com\", \"products\": [ { \"productId\": 2, \"quantity\": 1 } ], \"totalPrice\": 19.99, \"status\": \"SHIPPED\", \"orderDate\": \"2024-04-28T09:15:00\" } ]")))
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<OrderDTO>> getMyOrders(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<OrderDTO> orders = orderService.getOrdersByUserEmail(email);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Aktualizuje status zamówienia", description = "Endpoint do aktualizacji statusu istniejącego zamówienia na podstawie jego ID.")
    @ApiResponse(responseCode = "200", description = "Status zamówienia został pomyślnie zaktualizowany",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OrderDTO.class),
                    examples = @ExampleObject(value = "{ \"id\": 1, \"userEmail\": \"user@example.com\", \"products\": [ { \"productId\": 1, \"quantity\": 2 }, { \"productId\": 3, \"quantity\": 1 } ], \"totalPrice\": 59.99, \"status\": \"CONFIRMED\", \"orderDate\": \"2024-04-27T14:30:00\" }")))
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> statusUpdate) {
        String newStatus = statusUpdate.get("status");
        System.out.println(newStatus);
        OrderDTO updatedOrder = orderService.updateOrderStatus(id, newStatus);
        return ResponseEntity.ok(updatedOrder);
    }

    @Operation(summary = "Anuluje zamówienie po ID", description = "Endpoint do anulowania istniejącego zamówienia na podstawie jego ID.")
    @ApiResponse(responseCode = "200", description = "Zamówienie zostało pomyślnie anulowane",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OrderDTO.class),
                    examples = @ExampleObject(value = "{ \"id\": 1, \"userEmail\": \"user@example.com\", \"products\": [ { \"productId\": 1, \"quantity\": 2 }, { \"productId\": 3, \"quantity\": 1 } ], \"totalPrice\": 59.99, \"status\": \"CANCELLED\", \"orderDate\": \"2024-04-27T14:30:00\" }")))
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable Long id) {
        OrderDTO updatedOrder = orderService.cancelOrder(id);
        return ResponseEntity.ok(updatedOrder);
    }
}
