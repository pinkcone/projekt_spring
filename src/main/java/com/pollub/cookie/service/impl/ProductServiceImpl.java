package com.pollub.cookie.service.impl;

import com.pollub.cookie.dto.ProductCreateDTO;
import com.pollub.cookie.dto.ProductDTO;
import com.pollub.cookie.dto.ProductImportJsonDTO;
import com.pollub.cookie.exception.ResourceNotFoundException;
import com.pollub.cookie.mapper.ProductMapper;
import com.pollub.cookie.model.Product;
import com.pollub.cookie.repository.ProductRepository;
import com.pollub.cookie.service.FileStorageService;
import com.pollub.cookie.service.ProductService;
import com.pollub.cookie.validator.FileValidator;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository,
                              ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional
    public ProductDTO createProduct(ProductCreateDTO productCreateDTO, MultipartFile zdjecieFile) {
        logger.debug("Tworzenie produktu: {}", productCreateDTO);

        String zdjecieFileName = processImageFile(zdjecieFile);

        Product product = productMapper.mapToEntity(productCreateDTO, zdjecieFileName);
        product.setCategories(productMapper.mapCategoryIdsToEntities(productCreateDTO.getCategoryIds()));

        Product savedProduct = productRepository.save(product);
        return productMapper.mapToDTO(savedProduct);
    }

    private String processImageFile(MultipartFile zdjecieFile) {
        if (zdjecieFile != null && !zdjecieFile.isEmpty()) {
            FileValidator.validateImageFile(zdjecieFile);
            try {
                return fileStorageService.saveFile(zdjecieFile);
            } catch (IOException e) {
                throw new RuntimeException("Błąd podczas zapisywania pliku: " + e.getMessage());
            }
        }
        return null;
    }

    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produkt nie znaleziony o ID: " + id));
        return productMapper.mapToDTO(product);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(productMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductCreateDTO productCreateDTO, MultipartFile zdjecieFile) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produkt nie znaleziony o ID: " + id));

        updateProductFields(existingProduct, productCreateDTO);

        String zdjecieFileName = processImageFile(zdjecieFile);
        if (zdjecieFileName != null) {
            existingProduct.setImage(zdjecieFileName);
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.mapToDTO(updatedProduct);
    }

    private void updateProductFields(Product product, ProductCreateDTO dto) {
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setWeight(dto.getWeight());
        product.setQuantityInStock(dto.getQuantityInStock());
        product.setPrice(dto.getPrice());
        product.setCategories(productMapper.mapCategoryIdsToEntities(dto.getCategoryIds()));
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produkt nie znaleziony o ID: " + id);
        }
        productRepository.deleteById(id);
    }

    @Transactional
    public List<ProductDTO> importProductsFromJson(@Valid List<ProductImportJsonDTO> productImportJsonDTOs) {
        log.info("Rozpoczynanie importu {} produktów.", productImportJsonDTOs.size());

        if (productImportJsonDTOs == null || productImportJsonDTOs.isEmpty()) {
            log.warn("Przekazano pustą listę produktów do importu.");
            return Collections.emptyList();
        }

        productImportJsonDTOs.forEach(dto -> log.debug("Importowany produkt: {}", dto));

        List<Product> products = productImportJsonDTOs.stream()
                .map(productMapper::mapToEntity)
                .collect(Collectors.toList());

        log.info("Mapa produktów z DTO na encje zakończona. Zapis produktów do bazy danych.");

        try {
            products = productRepository.saveAll(products);
            log.info("Produkty zostały pomyślnie zapisane do bazy danych.");
        } catch (Exception e) {
            log.error("Błąd podczas zapisywania produktów do bazy danych: {}", e.getMessage(), e);
            throw e;
        }

        products.forEach(product -> log.debug("Zapisany produkt: {}", product));

        List<ProductDTO> createdProducts = products.stream()
                .map(productMapper::mapToDTO)
                .collect(Collectors.toList());

        log.info("Import produktów zakończony sukcesem. Zaimportowanych produktów: {}", createdProducts.size());

        return createdProducts;
    }
}
