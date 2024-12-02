package com.pollub.cookie.service.impl;

import com.pollub.cookie.dto.CategoryDTO;
import com.pollub.cookie.exception.ResourceNotFoundException;
import com.pollub.cookie.mapper.CategoryMapper;
import com.pollub.cookie.model.Category;
import com.pollub.cookie.model.Product;
import com.pollub.cookie.repository.CategoryRepository;
import com.pollub.cookie.repository.ProductRepository;
import com.pollub.cookie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = categoryMapper.mapToEntity(categoryDTO);
        List<Product> products = categoryMapper.mapProductIdsToEntities(categoryDTO.getProductIds());
        category.setProducts(products);

        products.forEach(product -> product.getCategories().add(category));

        Category savedCategory = categoryRepository.save(category);

        productRepository.saveAll(products);

        return categoryMapper.mapToDTO(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kategoria nie znaleziona o ID: " + id));
        return categoryMapper.mapToDTO(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(categoryMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kategoria nie znaleziona o ID: " + id));

        existingCategory.setName(categoryDTO.getName());
        existingCategory.setDescription(categoryDTO.getDescription());

        existingCategory.getProducts().forEach(product -> product.getCategories().remove(existingCategory));
        existingCategory.getProducts().clear();

        List<Product> newProducts = categoryMapper.mapProductIdsToEntities(categoryDTO.getProductIds());
        existingCategory.setProducts(newProducts);

        newProducts.forEach(product -> product.getCategories().add(existingCategory));

        Category updatedCategory = categoryRepository.save(existingCategory);

        productRepository.saveAll(newProducts);

        return categoryMapper.mapToDTO(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kategoria nie znaleziona o ID: " + id));

        category.getProducts().forEach(product -> product.getCategories().remove(category));

        categoryRepository.deleteById(id);

        productRepository.saveAll(category.getProducts());
    }

    @Override
    @Transactional
    public List<CategoryDTO> importCategoriesFromJson(List<CategoryDTO> categoryDTOs) {
        List<Category> categories = categoryDTOs.stream()
                .map(categoryMapper::mapToEntityWithProducts)
                .collect(Collectors.toList());

        List<Category> savedCategories = categoryRepository.saveAll(categories);

        for (Category category : savedCategories) {
            List<Product> products = category.getProducts();
            products.forEach(product -> product.getCategories().add(category));
        }

        List<Product> allProducts = savedCategories.stream()
                .flatMap(category -> category.getProducts().stream())
                .collect(Collectors.toList());
        productRepository.saveAll(allProducts);

        return savedCategories.stream()
                .map(categoryMapper::mapToDTO)
                .collect(Collectors.toList());
    }
}
