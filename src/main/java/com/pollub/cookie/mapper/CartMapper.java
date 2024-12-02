package com.pollub.cookie.mapper;

import com.pollub.cookie.dto.CartDTO;
import com.pollub.cookie.dto.CartItemDTO;
import com.pollub.cookie.dto.ProductDTO;
import com.pollub.cookie.model.Cart;
import com.pollub.cookie.model.CartItem;
import com.pollub.cookie.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartMapper {
    public CartDTO mapToDTO(Cart cart) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setTotalPrice(cart.getTotalPrice());
        cartDTO.setUserId(cart.getUser().getId());

        List<CartItemDTO> cartItems = cart.getCartItems() != null
                ? cart.getCartItems().stream()
                .map(this::mapCartItemToDTO)
                .collect(Collectors.toList())
                : new ArrayList<>();
        cartDTO.setCartItems(cartItems);

        return cartDTO;
    }

    private CartItemDTO mapCartItemToDTO(CartItem cartItem) {
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setId(cartItem.getId());
        cartItemDTO.setQuantity(cartItem.getQuantity());
        cartItemDTO.setPrice(cartItem.getPrice());
        cartItemDTO.setProductId(cartItem.getProduct().getId());
        cartItemDTO.setProduct(mapProductToDTO(cartItem.getProduct()));
        return cartItemDTO;
    }

    private ProductDTO mapProductToDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setPrice(product.getPrice());
        productDTO.setDescription(product.getDescription());
        productDTO.setImage(product.getImage());
        productDTO.setQuantityInStock(product.getQuantityInStock());

        return productDTO;
    }
}
