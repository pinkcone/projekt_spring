package com.pollub.cookie.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Email(message = "Nieprawidłowy format email")
    @NotBlank(message = "Email jest wymagany")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Hasło jest wymagane")
    @Size(min = 6, message = "Hasło musi mieć przynajmniej 6 znaków")
    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;
}
