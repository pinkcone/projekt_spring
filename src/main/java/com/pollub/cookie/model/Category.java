package com.pollub.cookie.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nazwa kategorii jest wymagana")
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank(message = "Opis kategorii jest wymagany")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;


    @ManyToMany(mappedBy = "categories")
    private List<Product> products;
}
