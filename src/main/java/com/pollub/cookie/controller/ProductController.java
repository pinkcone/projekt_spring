package com.pollub.cookie.controller;

import com.pollub.cookie.dto.ProductCreateDTO;
import com.pollub.cookie.dto.ProductDTO;
import com.pollub.cookie.dto.ProductImportJsonDTO;
import com.pollub.cookie.mapper.ProductMapper;
import com.pollub.cookie.model.Product;
import com.pollub.cookie.repository.ProductRepository;
import com.pollub.cookie.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/products")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Błędne dane wejściowe", content = @Content),
        @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp", content = @Content),
        @ApiResponse(responseCode = "403", description = "Brak dostępu", content = @Content),
        @ApiResponse(responseCode = "404", description = "Produkt nie został znaleziony", content = @Content)
})
@Tag(name = "Produkty", description = "Operacje związane z produktami")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    @Autowired
    public ProductController(ProductService productService, ProductRepository productRepository, ProductMapper productMapper) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Operation(summary = "Tworzy nowy produkt", description = "Endpoint do tworzenia nowego produktu w systemie.")
    @ApiResponse(responseCode = "201", description = "Produkt został pomyślnie utworzony",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductDTO.class),
                    examples = @ExampleObject(value = "{ \"id\": 1, \"nazwa\": \"Tort bezowy\", \"opis\": \"Beza z musem malinowym, mascarpone i owocami.\", \"gramatura\": 1500, \"zdjecie\": \"beza.jpg\", \"iloscNaStanie\": 10, \"cena\": 150, \"kategorie\": [ { \"id\": 1, \"nazwa\": \"Torty\", \"opis\": \"Torty urodzinowe i okolicznościowe.\" } ] }")))
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(
            @Valid @ModelAttribute ProductCreateDTO productCreateDTO,
            @RequestParam("imageFile") MultipartFile imageFile) {
        logger.info("ProductCreateDTO: {}", productCreateDTO);
        logger.info("ImageFile: {}", imageFile.getOriginalFilename());
        ProductDTO createdProduct = productService.createProduct(productCreateDTO, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @Operation(summary = "Pobiera produkt po ID", description = "Endpoint do pobierania szczegółów produktu na podstawie jego ID.")
    @ApiResponse(responseCode = "200", description = "Pomyślnie pobrano produkt",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductDTO.class),
                    examples = @ExampleObject(value = "{ \"id\": 1, \"nazwa\": \"Tort bezowy\", \"opis\": \"Beza z musem malinowym, mascarpone i owocami.\", \"gramatura\": 1500, \"zdjecie\": \"beza.jpg\", \"iloscNaStanie\": 10, \"cena\": 150, \"kategorie\": [ { \"id\": 1, \"nazwa\": \"Torty\", \"opis\": \"Torty urodzinowe i okolicznościowe.\" } ] }")))
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO productDTO = productService.getProductById(id);
        return ResponseEntity.ok(productDTO);
    }

    @Operation(summary = "Pobiera wszystkie produkty", description = "Endpoint do pobierania listy wszystkich produktów w systemie.")
    @ApiResponse(responseCode = "200", description = "Pomyślnie pobrano listę produktów",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductDTO.class),
                    examples = @ExampleObject(value = "[{ \"id\": 1, \"nazwa\": \"Tort bezowy\", \"opis\": \"Beza z musem malinowym, mascarpone i owocami.\", \"gramatura\": 1500, \"zdjecie\": \"beza.jpg\", \"iloscNaStanie\": 10, \"cena\": 150, \"kategorie\": [ { \"id\": 1, \"nazwa\": \"Torty\", \"opis\": \"Torty urodzinowe i okolicznościowe.\" } ] }, { \"id\": 2, \"nazwa\": \"Sernik\", \"opis\": \"Sernik tradycyjny\", \"gramatura\": 300, \"zdjecie\": \"sernik.jpg\", \"iloscNaStanie\": 25, \"cena\": 17, \"kategorie\": [ { \"id\": 2, \"nazwa\": \"Ciasta\", \"opis\": \"Domowe wypieki.\" } ] } ]")))
    @GetMapping("/all")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Pobiera produkty z filtrowaniem", description = "Endpoint do pobierania produktów z opcjonalnym filtrowaniem po kategorii i wyszukiwaniem nazwy.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pomyślnie pobrano listę produktów",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDTO.class),
                            examples = @ExampleObject(value = "[{ \"id\": 1, \"nazwa\": \"Tort bezowy\", \"opis\": \"Beza z musem malinowym, mascarpone i owocami.\", \"gramatura\": 1500, \"zdjecie\": \"beza.jpg\", \"iloscNaStanie\": 10, \"cena\": 150, \"kategorie\": [ { \"id\": 1, \"nazwa\": \"Torty\", \"opis\": \"Torty urodzinowe i okolicznościowe.\" } ] }, { \"id\": 2, \"nazwa\": \"Sernik\", \"opis\": \"Sernik tradycyjny\", \"gramatura\": 300, \"zdjecie\": \"sernik.jpg\", \"iloscNaStanie\": 25, \"cena\": 17, \"kategorie\": [ { \"id\": 2, \"nazwa\": \"Ciasta\", \"opis\": \"Domowe wypieki.\" } ] } ]")))
    })
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getProducts(
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) String search
    ) {
        List<Product> products;

        if (category != null && search != null) {
            products = productRepository.findByCategories_IdAndNameContainingIgnoreCase(category, search);
        } else if (category != null) {
            products = productRepository.findByCategories_Id(category);
        } else if (search != null) {
            products = productRepository.findByNameContainingIgnoreCase(search);
        } else {
            products = productRepository.findAll();
        }

        List<ProductDTO> productDTOs = products.stream()
                .map(productMapper::mapToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(productDTOs);
    }

    @Operation(summary = "Aktualizuje istniejący produkt", description = "Endpoint do aktualizacji danych istniejącego produktu na podstawie jego ID.")
    @ApiResponse(responseCode = "200", description = "Produkt został pomyślnie zaktualizowany",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductDTO.class),
                    examples = @ExampleObject(value = "{ \"id\": 1, \"nazwa\": \"Tort bezowy\", \"opis\": \"Beza z musem malinowym, mascarpone i owocami.\", \"gramatura\": 1500, \"zdjecie\": \"beza.jpg\", \"iloscNaStanie\": 10, \"cena\": 150, \"kategorie\": [ { \"id\": 1, \"nazwa\": \"Torty\", \"opis\": \"Torty urodzinowe i okolicznościowe.\" } ] }")))

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @Valid @ModelAttribute ProductCreateDTO productCreateDTO,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        ProductDTO updatedProduct = productService.updateProduct(id, productCreateDTO, imageFile);
        return ResponseEntity.ok(updatedProduct);
    }

    @Operation(summary = "Usuwa produkt po ID", description = "Endpoint do usuwania istniejącego produktu na podstawie jego ID.")
    @ApiResponse(responseCode = "204", description = "Produkt został pomyślnie usunięty",
            content = @Content)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Importuje listę produktów z JSON", description = "Endpoint do importowania wielu produktów jednocześnie z danych w formacie JSON.")
    @ApiResponse(responseCode = "201", description = "Produkty zostały pomyślnie zaimportowane",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductDTO.class),
                    examples = @ExampleObject(value = "[{ \"id\": 1, \"nazwa\": \"Tort bezowy\", \"opis\": \"Beza z musem malinowym, mascarpone i owocami.\", \"gramatura\": 1500, \"zdjecie\": \"beza.jpg\", \"iloscNaStanie\": 10, \"cena\": 150, \"kategorie\": [ { \"id\": 1, \"nazwa\": \"Torty\", \"opis\": \"Torty urodzinowe i okolicznościowe.\" } ] }, { \"id\": 2, \"nazwa\": \"Sernik\", \"opis\": \"Sernik tradycyjny\", \"gramatura\": 300, \"zdjecie\": \"sernik.jpg\", \"iloscNaStanie\": 25, \"cena\": 17, \"kategorie\": [ { \"id\": 2, \"nazwa\": \"Ciasta\", \"opis\": \"Domowe wypieki.\" } ] } ]")))
    @PostMapping("/import/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductDTO>> importProductsFromJson(
            @Valid @RequestBody List<ProductImportJsonDTO> productImportJsonDTOs) {
        List<ProductDTO> createdProducts = productService.importProductsFromJson(productImportJsonDTOs);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProducts);
    }

    @Operation(summary = "Eksportuje wszystkie produkty do JSON", description = "Endpoint do eksportowania wszystkich produktów w systemie do formatu JSON.")
    @ApiResponse(responseCode = "200", description = "Pomyślnie wyeksportowano produkty",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductDTO.class),
                    examples = @ExampleObject(value = "[{ \"id\": 1, \"nazwa\": \"Tort bezowy\", \"opis\": \"Beza z musem malinowym, mascarpone i owocami.\", \"gramatura\": 1500, \"zdjecie\": \"beza.jpg\", \"iloscNaStanie\": 10, \"cena\": 150, \"kategorie\": [ { \"id\": 1, \"nazwa\": \"Torty\", \"opis\": \"Torty urodzinowe i okolicznościowe.\" } ] }, { \"id\": 2, \"nazwa\": \"Sernik\", \"opis\": \"Sernik tradycyjny\", \"gramatura\": 300, \"zdjecie\": \"sernik.jpg\", \"iloscNaStanie\": 25, \"cena\": 17, \"kategorie\": [ { \"id\": 2, \"nazwa\": \"Ciasta\", \"opis\": \"Domowe wypieki.\" } ] } ]")))

    @GetMapping("/export/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductDTO>> exportProductsToJson() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
}
