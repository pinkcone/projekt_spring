package com.pollub.cookie.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Data
public class CartItemDTO {

    private Long id;

    @NotNull(message = "Ilość jest wymagana")
    @Positive(message = "Ilość musi być dodatnia")
    private Integer quantity;

    @NotNull(message = "Cena jest wymagana")
    @Positive(message = "Cena musi być dodatnia")
    private BigDecimal price;

    @NotNull(message = "ID produktu jest wymagane")
    private Long productId;
    private ProductDTO product;
}
