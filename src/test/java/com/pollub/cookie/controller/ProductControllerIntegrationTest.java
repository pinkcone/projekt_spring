package com.pollub.cookie.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pollub.cookie.dto.AuthRequestDTO;
import com.pollub.cookie.dto.ProductImportJsonDTO;
import com.pollub.cookie.model.Category;
import com.pollub.cookie.model.Product;
import com.pollub.cookie.model.Role;
import com.pollub.cookie.model.User;
import com.pollub.cookie.repository.CategoryRepository;
import com.pollub.cookie.repository.ProductRepository;
import com.pollub.cookie.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private String adminToken;

    private Long categoryId;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Category testCategory;

    @BeforeEach
    void setUp() throws Exception {

        String categoryName = "Test Category";
        testCategory = categoryRepository.findByName(categoryName);
        if (testCategory == null) {
            Category category = new Category();
            category.setName(categoryName);
            category.setDescription("Test Category Description");
            testCategory = categoryRepository.save(category);
        }
        categoryId = testCategory.getId();


        Optional<User> adminUser = userRepository.findByEmail("admin@example.com");
        if (adminUser.isEmpty()) {
            User adminUserCreate = new User();
            adminUserCreate.setEmail("admin@example.com");
            adminUserCreate.setPassword(passwordEncoder.encode("adminpassword"));
            adminUserCreate.setRole(Role.ADMIN);
            userRepository.save(adminUserCreate);
        }

        AuthRequestDTO authRequest = new AuthRequestDTO();
        authRequest.setEmail("admin@example.com");
        authRequest.setPassword("adminpassword");

        String authResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        adminToken = objectMapper.readTree(authResponse).get("token").asText();
    }

    @Test
    void givenValidProductData_whenCreateProduct_thenReturnCreatedProduct() throws Exception {

        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "image.jpg", "image/jpeg", "image content".getBytes());


        String uniqueProductName = "Test Product " + UUID.randomUUID();

        mockMvc.perform(multipart("/api/products")
                        .file(imageFile)
                        .param("name", uniqueProductName)
                        .param("description", "Test Description")
                        .param("weight", "500")
                        .param("quantityInStock", "10")
                        .param("price", "100.0")
                        .param("categoryIds", categoryId.toString())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(uniqueProductName))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    void givenProductExists_whenGetProductById_thenReturnProduct() throws Exception {

        String uniqueProductName = "Test Product " + UUID.randomUUID();
        Product product = new Product();
        product.setName(uniqueProductName);
        product.setDescription("Test Description");
        product.setWeight(BigDecimal.valueOf(500));
        product.setQuantityInStock(10);
        product.setPrice(BigDecimal.valueOf(100.0));
        product.setCategories(Collections.singletonList(testCategory));
        product.setImage("test_image.jpg");

        Product savedProduct = productRepository.save(product);


        mockMvc.perform(get("/api/products/" + savedProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(uniqueProductName))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    void whenGetAllProducts_thenReturnProductList() throws Exception {

        String uniqueProductName1 = "Product 1 " + UUID.randomUUID();
        Product product1 = new Product();
        product1.setName(uniqueProductName1);
        product1.setDescription("Description 1");
        product1.setWeight(BigDecimal.valueOf(500));
        product1.setQuantityInStock(10);
        product1.setPrice(BigDecimal.valueOf(100.0));
        product1.setCategories(Collections.singletonList(testCategory));
        product1.setImage("image1.jpg");

        String uniqueProductName2 = "Product 2 " + UUID.randomUUID();
        Product product2 = new Product();
        product2.setName(uniqueProductName2);
        product2.setDescription("Description 2");
        product2.setWeight(BigDecimal.valueOf(600));
        product2.setQuantityInStock(20);
        product2.setPrice(BigDecimal.valueOf(200.0));
        product2.setCategories(Collections.singletonList(testCategory));
        product2.setImage("image2.jpg");

        productRepository.saveAll(Arrays.asList(product1, product2));


        mockMvc.perform(get("/api/products/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name").value(hasItem(uniqueProductName1)))
                .andExpect(jsonPath("$[*].name").value(hasItem(uniqueProductName2)));
    }

    @Test
    void givenProducts_whenGetProductsWithFilter_thenReturnFilteredProducts() throws Exception {

        String anotherCategoryName = "Another Category";
        Category category2 = categoryRepository.findByName(anotherCategoryName);
        if (category2 == null) {
            category2 = new Category();
            category2.setName(anotherCategoryName);
            category2.setDescription("Another Category Description");
            category2 = categoryRepository.save(category2);
        }

        String uniqueProductName1 = "Apple Pie " + UUID.randomUUID();
        Product product1 = new Product();
        product1.setName(uniqueProductName1);
        product1.setDescription("Delicious apple pie");
        product1.setWeight(BigDecimal.valueOf(500));
        product1.setQuantityInStock(10);
        product1.setPrice(BigDecimal.valueOf(100.0));
        product1.setCategories(Collections.singletonList(testCategory));
        product1.setImage("apple_pie.jpg");

        String uniqueProductName2 = "Banana Bread " + UUID.randomUUID();
        Product product2 = new Product();
        product2.setName(uniqueProductName2);
        product2.setDescription("Yummy banana bread");
        product2.setWeight(BigDecimal.valueOf(600));
        product2.setQuantityInStock(20);
        product2.setPrice(BigDecimal.valueOf(200.0));
        product2.setCategories(Collections.singletonList(category2));
        product2.setImage("banana_bread.jpg");

        productRepository.saveAll(Arrays.asList(product1, product2));


        mockMvc.perform(get("/api/products")
                        .param("category", categoryId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name").value(hasItem(uniqueProductName1)));


        mockMvc.perform(get("/api/products")
                        .param("search", "Banana Bread")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name").value(hasItem(uniqueProductName2)));
    }

    @Test
    void givenValidUpdateData_whenUpdateProduct_thenReturnUpdatedProduct() throws Exception {

        String uniqueProductName = "Old Product " + UUID.randomUUID();
        Product product = new Product();
        product.setName(uniqueProductName);
        product.setDescription("Old Description");
        product.setWeight(BigDecimal.valueOf(500));
        product.setQuantityInStock(10);
        product.setPrice(BigDecimal.valueOf(100.0));
        product.setCategories(Collections.singletonList(testCategory));
        product.setImage("old_image.jpg");

        Product savedProduct = productRepository.save(product);


        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "new_image.jpg", "image/jpeg", "new image content".getBytes());


        String updatedProductName = "Updated Product " + UUID.randomUUID();


        mockMvc.perform(multipart("/api/products/" + savedProduct.getId())
                        .file(imageFile)
                        .param("name", updatedProductName)
                        .param("description", "Updated Description")
                        .param("weight", "700")
                        .param("quantityInStock", "15")
                        .param("price", "150.0")
                        .param("categoryIds", categoryId.toString())
                        .with(request -> {
                            request.setMethod("PUT"); // Nadpisujemy metodę na PUT
                            return request;
                        })
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedProductName))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    void givenProductExists_whenDeleteProduct_thenReturnNoContent() throws Exception {

        String uniqueProductName = "Product to Delete " + UUID.randomUUID();
        Product product = new Product();
        product.setName(uniqueProductName);
        product.setDescription("Description");
        product.setWeight(BigDecimal.valueOf(500));
        product.setQuantityInStock(10);
        product.setPrice(BigDecimal.valueOf(100.0));
        product.setCategories(Collections.singletonList(testCategory));
        product.setImage("image.jpg");

        Product savedProduct = productRepository.save(product);


        mockMvc.perform(delete("/api/products/" + savedProduct.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());


        Optional<Product> deletedProduct = productRepository.findById(savedProduct.getId());
        assert(deletedProduct.isEmpty());
    }

    @Test
    void givenValidProductList_whenImportProductsFromJson_thenReturnCreatedProducts() throws Exception {

        String uniqueProductName1 = "Imported Product 1 " + UUID.randomUUID();
        ProductImportJsonDTO product1 = new ProductImportJsonDTO();
        product1.setName(uniqueProductName1);
        product1.setDescription("Description 1");
        product1.setWeight(BigDecimal.valueOf(500));
        product1.setQuantityInStock(10);
        product1.setPrice(BigDecimal.valueOf(100.0));
        product1.setCategoryIds(Collections.singletonList(categoryId));

        String uniqueProductName2 = "Imported Product 2 " + UUID.randomUUID();
        ProductImportJsonDTO product2 = new ProductImportJsonDTO();
        product2.setName(uniqueProductName2);
        product2.setDescription("Description 2");
        product2.setWeight(BigDecimal.valueOf(600));
        product2.setQuantityInStock(20);
        product2.setPrice(BigDecimal.valueOf(200.0));
        product2.setCategoryIds(Collections.singletonList(categoryId));

        List<ProductImportJsonDTO> importList = Arrays.asList(product1, product2);


        mockMvc.perform(post("/api/products/import/json")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(importList))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].name").value(uniqueProductName1))
                .andExpect(jsonPath("$[1].name").value(uniqueProductName2));
    }

    @Test
    void whenExportProductsToJson_thenReturnProductList() throws Exception {

        String uniqueProductName1 = "Product 1 " + UUID.randomUUID();
        Product product1 = new Product();
        product1.setName(uniqueProductName1);
        product1.setDescription("Description 1");
        product1.setWeight(BigDecimal.valueOf(500));
        product1.setQuantityInStock(10);
        product1.setPrice(BigDecimal.valueOf(100.0));
        product1.setCategories(Collections.singletonList(testCategory));
        product1.setImage("image1.jpg");

        String uniqueProductName2 = "Product 2 " + UUID.randomUUID();
        Product product2 = new Product();
        product2.setName(uniqueProductName2);
        product2.setDescription("Description 2");
        product2.setWeight(BigDecimal.valueOf(600));
        product2.setQuantityInStock(20);
        product2.setPrice(BigDecimal.valueOf(200.0));
        product2.setCategories(Collections.singletonList(testCategory));
        product2.setImage("image2.jpg");

        productRepository.saveAll(Arrays.asList(product1, product2));

        mockMvc.perform(get("/api/products/export/json")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name").value(hasItem(uniqueProductName1)))
                .andExpect(jsonPath("$[*].name").value(hasItem(uniqueProductName2)));
    }
}
