package com.pollub.cookie.service.impl;

import com.pollub.cookie.dto.OrderDTO;
import com.pollub.cookie.dto.PlaceOrderDTO;
import com.pollub.cookie.exception.ResourceNotFoundException;
import com.pollub.cookie.mapper.OrderMapper;
import com.pollub.cookie.model.*;
import com.pollub.cookie.repository.*;
import com.pollub.cookie.service.CartService;
import com.pollub.cookie.service.NotificationService;
import com.pollub.cookie.service.OrderService;
import com.pollub.cookie.validator.StockValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final StockValidator stockValidator;
    private final NotificationService notificationService;
    private final CartService cartService;
    private final OrderMapper orderMapper;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            UserRepository userRepository,
                            OrderItemRepository orderItemRepository,
                            ProductRepository productRepository,
                            StockValidator stockValidator, NotificationService notificationService, CartService cartService, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.stockValidator = stockValidator;
        this.notificationService = notificationService;
        this.cartService = cartService;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public OrderDTO placeOrder(String email, @Valid PlaceOrderDTO orderDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Cart cart = cartService.getCartByUser(user);

        if (cart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty. Cannot place order.");
        }

        stockValidator.validateStock(cart);
        stockValidator.adjustStock(cart, productRepository);

        Order order = createOrderFromCart(user, orderDTO, cart);
        cartService.clearCart(cart);

        notificationService.createNotificationForAdmins("New order with ID " + order.getId() + " has been placed.");

        return orderMapper.mapToDTOWithItems(order);
    }

    private Order createOrderFromCart(User user, PlaceOrderDTO orderDTO, Cart cart) {
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.NEW);
        order.setTotalPrice(orderDTO.getTotalPrice());
        order.setUser(user);
        order.setOrderAddress(orderDTO.getAddress());
        order.setPhoneNumber(orderDTO.getPhoneNumber());

        List<OrderItem> orderItems = cart.getCartItems().stream()
                .map(cartItem -> new OrderItem(order, cartItem.getProduct(), cartItem.getQuantity(), cartItem.getPrice()))
                .collect(Collectors.toList());
        order.setOrderItems(orderItems);

        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        List<Order> orders = orderRepository.findByUser(user);

        return orders.stream()
                .map(orderMapper::mapToDTOWithItems)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {

        Order order = orderMapper.mapToEntity(orderDTO);

        User user = orderMapper.mapUserIdToEntity(orderDTO.getUserId());
        order.setUser(user);

        order.setOrderDate(orderDTO.getOrderDate());

        order.setOrderStatus(orderMapper.mapStatusStringToEnum(orderDTO.getStatus()));

        order.setTotalPrice(orderDTO.getTotalPrice());

        List<OrderItem> orderItems = orderMapper.mapOrderItemIdsToEntities(orderDTO.getOrderItemIds(), order);
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        return orderMapper.mapToDTO(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
        return orderMapper.mapToDTOWithItems(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(orderMapper::mapToDTOWithItems)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

        existingOrder.setOrderDate(orderDTO.getOrderDate());

        existingOrder.setOrderStatus(orderMapper.mapStatusStringToEnum(orderDTO.getStatus()));

        existingOrder.setTotalPrice(orderDTO.getTotalPrice());

        User user = orderMapper.mapUserIdToEntity(orderDTO.getUserId());
        existingOrder.setUser(user);

        existingOrder.setOrderAddress(orderDTO.getAddress());
        existingOrder.setPhoneNumber(orderDTO.getPhoneNumber());

        orderItemRepository.deleteAll(existingOrder.getOrderItems());
        existingOrder.getOrderItems().clear();

        List<OrderItem> updatedOrderItems = orderMapper.mapOrderItemIdsToEntities(orderDTO.getOrderItemIds(), existingOrder);
        existingOrder.setOrderItems(updatedOrderItems);

        Order updatedOrder = orderRepository.save(existingOrder);

        return orderMapper.mapToDTOWithItems(updatedOrder);
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found with ID: " + id);
        }
        orderRepository.deleteById(id);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long id, String newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zamówienie nie znalezione o ID: " + id));
        System.out.println("jebac ci bobra");
        try {
            OrderStatus status = OrderStatus.valueOf(newStatus);
            order.setOrderStatus(status);
            Order updatedOrder = orderRepository.save(order);
            notificationService.createNotificationForUser(order.getUser(), "Status Twojego zamówienia o ID " + order.getId() + " został zmieniony na: " + status.name());
            return orderMapper.mapToDTOWithItems(updatedOrder);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Nieprawidłowy status zamówienia: " + newStatus);
        }
    }

    @Transactional
    public OrderDTO cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zamówienie nie znalezione o ID: " + id));

        if (order.getOrderStatus() == OrderStatus.NEW || order.getOrderStatus() == OrderStatus.IN_PROCESSING) {
            order.setOrderStatus(OrderStatus.CANCELLED);
            Order updatedOrder = orderRepository.save(order);
            return orderMapper.mapToDTOWithItems(updatedOrder);
        } else {
            throw new IllegalStateException("Nie można anulować zamówienia o statusie: " + order.getOrderStatus());
        }
    }
}
