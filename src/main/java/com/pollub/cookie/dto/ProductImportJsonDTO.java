package com.pollub.cookie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor

public class ProductImportJsonDTO {


    @NotBlank(message = "Nazwa produktu jest wymagana")
    private String name;

    private String description;

    @NotNull(message = "Cena jest wymagana")
    @DecimalMin(value = "0.0", inclusive = false, message = "Cena musi być większa od zera")
    private BigDecimal price;

    @DecimalMin(value = "0.0", inclusive = false, message = "Gramatura musi być większa od zera")
    private BigDecimal weight;

    @Min(value = 0, message = "Ilość na stanie nie może być ujemna")
    private Integer quantityInStock;
    @JsonDeserialize(as = ArrayList.class)
    @JsonProperty("kategorie")
    private List<Long> categoryIds = new ArrayList<>();
    @JsonProperty("zdjecie")
    private String imageUrl;

}


