package com.pollub.cookie.service;

import com.pollub.cookie.dto.ProductCreateDTO;
import com.pollub.cookie.dto.ProductDTO;
import com.pollub.cookie.dto.ProductImportJsonDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    ProductDTO createProduct(ProductCreateDTO productCreateDTO, MultipartFile zdjecieFile);

    ProductDTO getProductById(Long id);

    List<ProductDTO> getAllProducts();

    ProductDTO updateProduct(Long id, ProductCreateDTO productCreateDTO, MultipartFile zdjecieFile);

    void deleteProduct(Long id);

    List<ProductDTO> importProductsFromJson(List<ProductImportJsonDTO> productImportJsonDTOs);
}
