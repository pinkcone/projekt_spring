package com.pollub.cookie.dto;

import lombok.Data;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@Data
public class DiscountCodeDTO {

    private Long id;

    @NotBlank(message = "Kod rabatowy jest wymagany")
    private String code;

    @NotNull(message = "Typ rabatu jest wymagany")
    private String type;

    @NotNull(message = "Wartość rabatu jest wymagana")
    @Positive(message = "Wartość rabatu musi być dodatnia")
    private Double value;

    @NotNull(message = "Data ważności rabatu jest wymagana")
    @Future(message = "Data ważności rabatu musi być przyszła")
    private LocalDate expirationDate;
}
