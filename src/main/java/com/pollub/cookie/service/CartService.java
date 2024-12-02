package com.pollub.cookie.service;

import com.pollub.cookie.dto.CartDTO;
import com.pollub.cookie.dto.CartItemRequestDTO;
import com.pollub.cookie.model.Cart;
import com.pollub.cookie.model.User;

public interface CartService {

    CartDTO getCartByUserEmail(String email);

    CartDTO addToCart(String email, CartItemRequestDTO cartItemRequestDTO);

    CartDTO updateCartItem(String email, CartItemRequestDTO cartItemRequestDTO);

    CartDTO removeFromCart(String email, Long productId);

    Cart getCartByUser(User user);

    void clearCart(Cart cart);
}
