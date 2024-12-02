package com.pollub.cookie.mapper;

import com.pollub.cookie.dto.CategoryDTO;
import com.pollub.cookie.model.Category;
import com.pollub.cookie.model.Product;
import com.pollub.cookie.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryMapper {

    private final ProductRepository productRepository;

    public CategoryMapper(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public CategoryDTO mapToDTO(Category category) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setName(category.getName());
        categoryDTO.setDescription(category.getDescription());

        List<Long> productIds = category.getProducts() != null
                ? category.getProducts().stream()
                .map(Product::getId)
                .collect(Collectors.toList())
                : new ArrayList<>();
        categoryDTO.setProductIds(productIds);

        return categoryDTO;
    }

    public Category mapToEntity(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        return category;
    }

    public Category mapToEntityWithProducts(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        List<Product> products = mapProductIdsToEntities(categoryDTO.getProductIds());
        category.setProducts(products);
        return category;
    }

    public List<Product> mapProductIdsToEntities(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return new ArrayList<>();
        }
        return productRepository.findAllById(productIds);
    }
}
