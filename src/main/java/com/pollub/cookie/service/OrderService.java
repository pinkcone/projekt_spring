package com.pollub.cookie.service;

import com.pollub.cookie.dto.OrderDTO;
import com.pollub.cookie.dto.PlaceOrderDTO;

import java.util.List;

public interface OrderService {

    OrderDTO placeOrder(String email, PlaceOrderDTO orderDTO);

    List<OrderDTO> getOrdersByUserEmail(String email);

    OrderDTO createOrder(OrderDTO orderDTO);

    OrderDTO getOrderById(Long id);

    List<OrderDTO> getAllOrders();

    OrderDTO updateOrder(Long id, OrderDTO orderDTO);

    void deleteOrder(Long id);

    OrderDTO updateOrderStatus(Long id, String newStatus);

    OrderDTO cancelOrder(Long id);
}
