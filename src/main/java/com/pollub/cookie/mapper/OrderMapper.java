package com.pollub.cookie.mapper;

import com.pollub.cookie.dto.OrderDTO;
import com.pollub.cookie.dto.OrderItemDTO;
import com.pollub.cookie.dto.ProductDTO;
import com.pollub.cookie.exception.ResourceNotFoundException;
import com.pollub.cookie.model.*;
import com.pollub.cookie.repository.OrderItemRepository;
import com.pollub.cookie.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderMapper {
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderMapper(UserRepository userRepository, OrderItemRepository orderItemRepository) {
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public Order mapToEntity(OrderDTO orderDTO) {
        Order order = new Order();
        order.setOrderDate(orderDTO.getOrderDate());
        order.setOrderStatus(mapStatusStringToEnum(orderDTO.getStatus()));
        order.setTotalPrice(orderDTO.getTotalPrice());
        order.setOrderAddress(orderDTO.getAddress());
        order.setPhoneNumber(orderDTO.getPhoneNumber());
        return order;
    }


    public OrderDTO mapToDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setOrderDate(order.getOrderDate());
        orderDTO.setStatus(order.getOrderStatus().name());
        orderDTO.setTotalPrice(order.getTotalPrice());
        orderDTO.setUserId(order.getUser().getId());
        orderDTO.setAddress(order.getOrderAddress());
        orderDTO.setPhoneNumber(order.getPhoneNumber());

        List<Long> orderItemIds = order.getOrderItems() != null
                ? order.getOrderItems().stream()
                .map(OrderItem::getId)
                .collect(Collectors.toList())
                : new ArrayList<>();
        orderDTO.setOrderItemIds(orderItemIds);
        return orderDTO;
    }


    public OrderDTO mapToDTOWithItems(Order order) {
        OrderDTO orderDTO = mapToDTO(order);
        List<OrderItemDTO> orderItems = order.getOrderItems().stream()
                .map(this::mapOrderItemToDTO)
                .collect(Collectors.toList());
        orderDTO.setOrderItems(orderItems);
        return orderDTO;
    }


    public OrderItemDTO mapOrderItemToDTO(OrderItem orderItem) {
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setId(orderItem.getId());
        orderItemDTO.setQuantity(orderItem.getQuantity());
        orderItemDTO.setPrice(orderItem.getPrice());
        orderItemDTO.setProductId(orderItem.getProduct().getId());

        ProductDTO productDTO = mapProductToDTO(orderItem.getProduct());
        orderItemDTO.setProduct(productDTO);
        return orderItemDTO;
    }


    public ProductDTO mapProductToDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setPrice(product.getPrice());
        productDTO.setDescription(product.getDescription());
        productDTO.setImage(product.getImage());

        return productDTO;
    }

    public OrderStatus mapStatusStringToEnum(String statusString) {
        try {
            return OrderStatus.valueOf(statusString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + statusString);
        }
    }


    public List<OrderItem> mapOrderItemIdsToEntities(List<Long> orderItemIds, Order order) {
        if (orderItemIds == null || orderItemIds.isEmpty()) {
            throw new IllegalArgumentException("Order item ID list cannot be empty");
        }

        List<OrderItem> orderItems = orderItemRepository.findAllById(orderItemIds);
        if (orderItems.size() != orderItemIds.size()) {
            throw new ResourceNotFoundException("Some order items were not found for the provided IDs.");
        }

        orderItems.forEach(item -> item.setOrder(order));

        return orderItems;
    }

    public User mapUserIdToEntity(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }
}
