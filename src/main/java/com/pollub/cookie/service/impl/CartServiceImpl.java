package com.pollub.cookie.service.impl;

import com.pollub.cookie.dto.CartDTO;
import com.pollub.cookie.dto.CartItemRequestDTO;
import com.pollub.cookie.exception.ResourceNotFoundException;
import com.pollub.cookie.mapper.CartMapper;
import com.pollub.cookie.model.Cart;
import com.pollub.cookie.model.CartItem;
import com.pollub.cookie.model.Product;
import com.pollub.cookie.model.User;
import com.pollub.cookie.repository.CartItemRepository;
import com.pollub.cookie.repository.CartRepository;
import com.pollub.cookie.repository.ProductRepository;
import com.pollub.cookie.repository.UserRepository;
import com.pollub.cookie.service.CartService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;

    @Autowired
    public CartServiceImpl(
            CartRepository cartRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            CartItemRepository cartItemRepository, CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartMapper = cartMapper;
    }

    public Cart getCartByUser(User user) {
        return cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono koszyka dla użytkownika: " + user.getEmail()));
    }

    @Override
    @Transactional(readOnly = true)
    public CartDTO getCartByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono użytkownika o emailu: " + email));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setTotalPrice(BigDecimal.ZERO);
                    newCart.setCartItems(new ArrayList<>());
                    return cartRepository.save(newCart);
                });

        return cartMapper.mapToDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO addToCart(String email, CartItemRequestDTO cartItemRequestDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony o email: " + email));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setTotalPrice(BigDecimal.ZERO);
                    newCart.setCartItems(new ArrayList<>());
                    return cartRepository.save(newCart);
                });

        Product product = productRepository.findById(cartItemRequestDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Produkt nie znaleziony o ID: " + cartItemRequestDTO.getProductId()));

        Optional<CartItem> existingItemOpt = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + cartItemRequestDTO.getQuantity());
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(cartItemRequestDTO.getQuantity());
            cartItem.setPrice(product.getPrice());
            cart.getCartItems().add(cartItem);
        }

        cart.setTotalPrice(calculateTotalPrice(cart));

        Cart updatedCart = cartRepository.save(cart);
        return cartMapper.mapToDTO(updatedCart);
    }

    @Override
    @Transactional
    public CartDTO updateCartItem(String email, CartItemRequestDTO cartItemRequestDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony o email: " + email));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Koszyk nie znaleziony dla użytkownika: " + email));

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(cartItemRequestDTO.getProductId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Produkt nie znaleziony w koszyku: " + cartItemRequestDTO.getProductId()));

        cartItem.setQuantity(cartItemRequestDTO.getQuantity());

        cart.setTotalPrice(calculateTotalPrice(cart));

        Cart updatedCart = cartRepository.save(cart);
        return cartMapper.mapToDTO(updatedCart);
    }

    @Override
    @Transactional
    public CartDTO removeFromCart(String email, Long productId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony o email: " + email));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Koszyk nie znaleziony dla użytkownika: " + email));

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Produkt nie znaleziony w koszyku: " + productId));

        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        cart.setTotalPrice(calculateTotalPrice(cart));

        Cart updatedCart = cartRepository.save(cart);
        return cartMapper.mapToDTO(updatedCart);
    }

    private BigDecimal calculateTotalPrice(@NotNull Cart cart) {
        return cart.getCartItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void clearCart(Cart cart) {
        cart.getCartItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
    }
}
