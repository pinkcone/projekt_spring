package com.pollub.cookie.controller;

import com.pollub.cookie.dto.DiscountCodeDTO;
import com.pollub.cookie.service.DiscountCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.xml.bind.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/discount-codes")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Błędne dane wejściowe", content = @Content),
        @ApiResponse(responseCode = "403", description = "Brak dostępu", content = @Content),
        @ApiResponse(responseCode = "404", description = "Kod rabatowy nie został znaleziony", content = @Content)
})
public class DiscountCodeController {

    private final DiscountCodeService discountCodeService;

    @Autowired
    public DiscountCodeController(DiscountCodeService discountCodeService) {
        this.discountCodeService = discountCodeService;
    }

    @Operation(summary = "Tworzy nowy kod rabatowy", description = "Endpoint do tworzenia nowego kodu rabatowego w systemie.")
    @ApiResponse(responseCode = "201", description = "Kod rabatowy został pomyślnie utworzony",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DiscountCodeDTO.class),
                    examples = @ExampleObject(value = "{ \"id\": 1, \"kod\": \"DISCOUNT10\", \"procent\": 10, \"dataWygasniecia\": \"2024-12-31\", \"uzywany\": false }")))
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DiscountCodeDTO> createDiscountCode(@Valid @RequestBody DiscountCodeDTO discountCodeDTO) throws ValidationException {
        DiscountCodeDTO createdDiscountCode = discountCodeService.createDiscountCode(discountCodeDTO);
        return ResponseEntity.status(201).body(createdDiscountCode);
    }


    @Operation(summary = "Pobiera kod rabatowy po ID", description = "Endpoint do pobierania szczegółów kodu rabatowego na podstawie jego ID.")
    @ApiResponse(responseCode = "200", description = "Pomyślnie pobrano kod rabatowy",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DiscountCodeDTO.class),
                    examples = @ExampleObject(value = "{ \"id\": 1, \"kod\": \"DISCOUNT10\", \"procent\": 10, \"dataWygasniecia\": \"2024-12-31\", \"uzywany\": false }")))
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DiscountCodeDTO> getDiscountCodeById(@PathVariable Long id) {
        DiscountCodeDTO discountCodeDTO = discountCodeService.getDiscountCodeById(id);
        return ResponseEntity.ok(discountCodeDTO);
    }

    @Operation(summary = "Pobiera wszystkie kody rabatowe", description = "Endpoint do pobierania listy wszystkich kodów rabatowych w systemie.")
    @ApiResponse(responseCode = "200", description = "Pomyślnie pobrano listę kodów rabatowych",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DiscountCodeDTO.class),
                    examples = @ExampleObject(value = "[ { \"id\": 1, \"kod\": \"DISCOUNT10\", \"procent\": 10, \"dataWygasniecia\": \"2024-12-31\", \"uzywany\": false }, { \"id\": 2, \"kod\": \"DISCOUNT20\", \"procent\": 20, \"dataWygasniecia\": \"2025-01-31\", \"uzywany\": false } ]")))
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<DiscountCodeDTO>> getAllDiscountCodes() {
        List<DiscountCodeDTO> discountCodes = discountCodeService.getAllDiscountCodes();
        return ResponseEntity.ok(discountCodes);
    }

    @Operation(summary = "Aktualizuje istniejący kod rabatowy", description = "Endpoint do aktualizacji danych istniejącego kodu rabatowego na podstawie jego ID.")
    @ApiResponse(responseCode = "200", description = "Kod rabatowy został pomyślnie zaktualizowany",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DiscountCodeDTO.class),
                    examples = @ExampleObject(value = "{ \"id\": 1, \"kod\": \"DISCOUNT15\", \"procent\": 15, \"dataWygasniecia\": \"2025-12-31\", \"uzywany\": false }")))
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DiscountCodeDTO> updateDiscountCode(@PathVariable Long id, @Valid @RequestBody DiscountCodeDTO discountCodeDTO) {
        DiscountCodeDTO updatedDiscountCode = discountCodeService.updateDiscountCode(id, discountCodeDTO);
        return ResponseEntity.ok(updatedDiscountCode);
    }

    @Operation(summary = "Usuwa kod rabatowy po ID", description = "Endpoint do usuwania istniejącego kodu rabatowego na podstawie jego ID.")
    @ApiResponse(responseCode = "204", description = "Kod rabatowy został pomyślnie usunięty", content = @Content)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDiscountCode(@PathVariable Long id) {
        discountCodeService.deleteDiscountCode(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Pobiera kod rabatowy po kodzie", description = "Endpoint do pobierania szczegółów kodu rabatowego na podstawie jego kodu.")
    @ApiResponse(responseCode = "200", description = "Pomyślnie pobrano kod rabatowy",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DiscountCodeDTO.class),
                    examples = @ExampleObject(value = "{ \"id\": 1, \"kod\": \"DISCOUNT10\", \"procent\": 10, \"dataWygasniecia\": \"2024-12-31\", \"uzywany\": false }")))
    @GetMapping("/by-code/{kod}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DiscountCodeDTO> getDiscountCodeByCode(@PathVariable String kod) {
        DiscountCodeDTO discountCodeDTO = discountCodeService.getDiscountCodeByCode(kod);
        return ResponseEntity.ok(discountCodeDTO);
    }

}
