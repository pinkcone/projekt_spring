package com.pollub.cookie.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class ProductDTO {

    @Setter
    private Long id;

    @NotBlank(message = "Nazwa produktu jest wymagana")
    private String name;

    @NotBlank(message = "Opis produktu jest wymagany")
    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "Cena musi być większa od zera")
    private BigDecimal weight;


    private String image;

    @NotNull(message = "Ilość na stanie jest wymagana")
    @Positive(message = "Ilość na stanie musi być dodatnia")
    private Integer quantityInStock;

    @NotNull(message = "Cena produktu jest wymagana")
    @Positive(message = "Cena musi być dodatnia")
    private BigDecimal price;

    private List<CategoryDTO> categories = new ArrayList<>();

}
