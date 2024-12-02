package com.pollub.cookie.model;

import lombok.Data;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Ilość jest wymagana")
    @Positive(message = "Ilość musi być dodatnia")
    @Column(nullable = false)
    private Integer quantity;

    @NotNull(message = "Cena jest wymagana")
    @Positive(message = "Cena musi być dodatnia")
    @Column(nullable = false)
    private BigDecimal price;

    @Setter
    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public OrderItem(Order order,
                     Product product,
                     @NotNull(message = "Ilość jest wymagana") @Positive(message = "Ilość musi być dodatnia") Integer quantity,
                     @NotNull(message = "Cena jest wymagana") @Positive(message = "Cena musi być dodatnia") BigDecimal price) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }
}
