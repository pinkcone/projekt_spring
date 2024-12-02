package com.pollub.cookie.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "discount_codes", uniqueConstraints = {
        @UniqueConstraint(columnNames = "code")
})
public class DiscountCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Kod rabatowy jest wymagany")
    @Column(nullable = false, unique = true)
    private String code;

    @NotNull(message = "Typ rabatu jest wymagany")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType type;

    @NotNull(message = "Wartość rabatu jest wymagana")
    @Positive(message = "Wartość rabatu musi być dodatnia")
    @Column(nullable = false)
    private Double value;

    @NotNull(message = "Data ważności rabatu jest wymagana")
    @Future(message = "Data ważności rabatu musi być przyszła")
    @Column(nullable = false)
    private LocalDate expirationDate;
}
