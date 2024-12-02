package com.pollub.cookie.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@NoArgsConstructor
@Data
public class CategoryDTO {

    @Setter
    private Long id;

    @NotBlank(message = "Nazwa kategorii jest wymagana")
    private String name;

    @NotBlank(message = "Opis kategorii jest wymagany")
    private String description;

    private List<Long> productIds;

}
