package com.pollub.cookie.model;

import lombok.Data;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    @NotNull(message = "Data zamówienia jest wymagana")
    @Column(nullable = false)
    private LocalDateTime orderDate;

    @NotNull(message = "Status zamówienia jest wymagany")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @NotNull(message = "Całkowita cena jest wymagana")
    @Column(nullable = false)
    private BigDecimal totalPrice;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;
    @NotNull(message = "Adres jest wymagany")
    @Column(nullable = false)
    private String orderAddress;

    @NotNull(message = "Numer telefonu jest wymagany")
    @Column(nullable = false)
    private String phoneNumber;
}
