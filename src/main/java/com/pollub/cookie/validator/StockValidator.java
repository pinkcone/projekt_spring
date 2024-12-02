package com.pollub.cookie.validator;

import com.pollub.cookie.model.Cart;
import com.pollub.cookie.model.CartItem;
import com.pollub.cookie.model.Product;
import com.pollub.cookie.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class StockValidator {

    public void validateStock(Cart cart) {
        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();
            if (cartItem.getQuantity() > product.getQuantityInStock()) {
                throw new IllegalArgumentException("Product '" + product.getName() + "' has insufficient stock. Available: "
                        + product.getQuantityInStock() + ", requested: " + cartItem.getQuantity());
            }
        }
    }

    public void adjustStock(Cart cart, ProductRepository productRepository) {
        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();
            product.setQuantityInStock(product.getQuantityInStock() - cartItem.getQuantity());
            productRepository.save(product);
        }
    }
}

