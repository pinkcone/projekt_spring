package com.pollub.cookie.controller;

import com.pollub.cookie.dto.CategoryDTO;
import com.pollub.cookie.service.CategoryService;
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
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Błędne dane wejściowe", content = @Content),
        @ApiResponse(responseCode = "403", description = "Brak dostępu", content = @Content),
        @ApiResponse(responseCode = "404", description = "Kategoria nie została znaleziona", content = @Content)
})
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Tworzy nową kategorię", description = "Endpoint do tworzenia nowej kategorii w systemie.")
    @ApiResponse(responseCode = "201", description = "Kategoria została pomyślnie utworzona",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CategoryDTO.class),
                    examples = @ExampleObject(value = "{ \"id\": 1, \"nazwa\": \"Torty\", \"opis\": \"Torty urodzinowe i okolicznościowe.\", \"produktyIds\": [1, 2, 3] }")))
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO createdCategory = categoryService.createCategory(categoryDTO);
        return ResponseEntity.status(201).body(createdCategory);
    }

    @Operation(summary = "Pobiera kategorię po ID", description = "Endpoint do pobierania szczegółów kategorii na podstawie jej ID.")
    @ApiResponse(responseCode = "200", description = "Pomyślnie pobrano kategorię",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CategoryDTO.class),
                    examples = @ExampleObject(value = "{ \"id\": 1, \"nazwa\": \"Torty\", \"opis\": \"Torty urodzinowe i okolicznościowe.\", \"produktyIds\": [1, 2, 3] }")))
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        CategoryDTO categoryDTO = categoryService.getCategoryById(id);
        return ResponseEntity.ok(categoryDTO);
    }

    @Operation(summary = "Pobiera wszystkie kategorie", description = "Endpoint do pobierania listy wszystkich kategorii w systemie.")
    @ApiResponse(responseCode = "200", description = "Pomyślnie pobrano listę kategorii",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CategoryDTO.class),
                    examples = @ExampleObject(value = "[ { \"id\": 1, \"nazwa\": \"Torty\", \"opis\": \"Torty urodzinowe i okolicznościowe.\", \"produktyIds\": [1, 2, 3] }, { \"id\": 2, \"nazwa\": \"Ciasta\", \"opis\": \"Domowe wypieki.\", \"produktyIds\": [4, 5] } ]")))
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categoryDTOs = categoryService.getAllCategories();
        return ResponseEntity.ok(categoryDTOs);
    }

    @Operation(summary = "Aktualizuje istniejącą kategorię", description = "Endpoint do aktualizacji danych istniejącej kategorii na podstawie jej ID.")
    @ApiResponse(responseCode = "200", description = "Kategoria została pomyślnie zaktualizowana",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CategoryDTO.class),
                    examples = @ExampleObject(value = "{ \"id\": 1, \"nazwa\": \"Torty\", \"opis\": \"Torty urodzinowe i okolicznościowe.\", \"produktyIds\": [1, 2, 4] }")))
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO updatedCategory = categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    @Operation(summary = "Usuwa kategorię po ID", description = "Endpoint do usuwania istniejącej kategorii na podstawie jej ID.")
    @ApiResponse(responseCode = "204", description = "Kategoria została pomyślnie usunięta",
            content = @Content)

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Importuje listę kategorii z JSON", description = "Endpoint do importowania wielu kategorii jednocześnie z danych w formacie JSON.")
    @ApiResponse(responseCode = "201", description = "Kategorie zostały pomyślnie zaimportowane",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CategoryDTO.class),
                    examples = @ExampleObject(value = "[ { \"id\": 1, \"nazwa\": \"Torty\", \"opis\": \"Torty urodzinowe i okolicznościowe.\", \"produktyIds\": [1, 2, 3] }, { \"id\": 2, \"nazwa\": \"Ciasta\", \"opis\": \"Domowe wypieki.\", \"produktyIds\": [4, 5] } ]")))
    @PostMapping("/import/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CategoryDTO>> importCategoriesFromJson(
            @Valid @RequestBody List<CategoryDTO> categoryDTOs) {
        List<CategoryDTO> createdCategories = categoryService.importCategoriesFromJson(categoryDTOs);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategories);
    }
}
