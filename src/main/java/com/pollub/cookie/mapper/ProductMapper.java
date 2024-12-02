package com.pollub.cookie.mapper;

import com.pollub.cookie.dto.CategoryDTO;
import com.pollub.cookie.dto.ProductCreateDTO;
import com.pollub.cookie.dto.ProductDTO;
import com.pollub.cookie.dto.ProductImportJsonDTO;
import com.pollub.cookie.model.Category;
import com.pollub.cookie.model.Product;
import com.pollub.cookie.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductMapper {
    private static final Logger logger = LoggerFactory.getLogger(ProductMapper.class);
    private final CategoryRepository categoryRepository;

    public ProductMapper(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Product mapToEntity(ProductCreateDTO productCreateDTO, String imageFileName) {
        Product product = new Product();
        product.setName(productCreateDTO.getName());
        product.setDescription(productCreateDTO.getDescription());
        product.setWeight((productCreateDTO.getWeight()));
        product.setQuantityInStock(productCreateDTO.getQuantityInStock());
        product.setPrice(productCreateDTO.getPrice());
        product.setImage(imageFileName);
        return product;
    }
    public Product mapToEntity(ProductImportJsonDTO productImportJsonDTO) {
        logger.debug("Mapowanie ProductImportJsonDTO do Product: {}", productImportJsonDTO);

        Product product = new Product();
        product.setName(productImportJsonDTO.getName());
        product.setDescription(productImportJsonDTO.getDescription());
        product.setPrice(productImportJsonDTO.getPrice());
        product.setWeight(productImportJsonDTO.getWeight());
        product.setQuantityInStock(productImportJsonDTO.getQuantityInStock());
        product.setImage(productImportJsonDTO.getImageUrl());

        List<Category> categories = mapCategoryIdsToEntities(productImportJsonDTO.getCategoryIds());
        product.setCategories(categories);

        logger.debug("Zmapowany produkt: {}", product);
        return product;
    }

    /**
     * Mapuje listę ID kategorii na encje Category.
     *
     * @param categoryIds Lista ID kategorii
     * @return Lista encji Category
     */
    public List<Category> mapCategoryIdsToEntities(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            logger.debug("Lista ID kategorii jest pusta lub null. Zwracanie pustej listy kategorii.");
            return new ArrayList<>();
        }

        logger.debug("Mapowanie listy ID kategorii: {}", categoryIds);

        List<Category> categories = categoryRepository.findAllById(categoryIds);

        if (categories.isEmpty()) {
            logger.warn("Nie znaleziono kategorii dla podanych ID: {}", categoryIds);
        } else {
            logger.debug("Znalezione kategorie: {}", categories);
        }

        return categories;
    }

    /**
     * Mapuje encję Product na ProductDTO.
     *
     * @param product Encja Product
     * @return ProductDTO
     */
    public ProductDTO mapToDTO(Product product) {
        logger.debug("Mapowanie Product do ProductDTO: {}", product);

        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setWeight(product.getWeight());
        productDTO.setImage(product.getImage());
        productDTO.setQuantityInStock(product.getQuantityInStock());
        productDTO.setPrice(product.getPrice());

        List<CategoryDTO> categoryDTOs = product.getCategories() != null
                ? product.getCategories().stream()
                .map(this::mapCategoryToDTO)
                .collect(Collectors.toList())
                : new ArrayList<>();

        productDTO.setCategories(categoryDTOs);
        logger.debug("Zmapowany ProductDTO: {}", productDTO);
        return productDTO;
    }

    public CategoryDTO mapCategoryToDTO(Category category) {
        logger.debug("Mapowanie Category do CategoryDTO: {}", category);

        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());

        logger.debug("Zmapowany CategoryDTO: {}", dto);
        return dto;
    }
}
