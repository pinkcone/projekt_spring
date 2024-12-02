package com.pollub.cookie.repository;

import com.pollub.cookie.model.Product;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsById(@NotNull Long id);

    List<Product> findByCategories_Id(Long categoryId);


    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByCategories_IdAndNameContainingIgnoreCase(Long categoryId, String name);

}
