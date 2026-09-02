package com.example.ecommerce.service;

import com.example.ecommerce.dto.OrderItemResponse;
import com.example.ecommerce.dto.OrderResponse;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import com.example.ecommerce.exception.InsufficientStockException;
import com.example.ecommerce.exception.ResourceNotFoundException;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        CartItemRepository cartItemRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderResponse placeOrder(Long customerId) {
        List<CartItem> cartItems = cartItemRepository.findByCustomerId(customerId);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cannot place an order with an empty cart for customer id: " + customerId);
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        Order order = new Order(cartItems.get(0).getCustomer(), BigDecimal.ZERO);

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            if (cartItem.getQuantity() > product.getStockQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
            }

            OrderItem orderItem = new OrderItem(product, cartItem.getQuantity(), product.getPrice());
            order.addOrderItem(orderItem);

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        cartItemRepository.deleteByCustomerId(customerId);

        return toResponse(savedOrder);
    }

    public List<OrderResponse> getOrderHistory(Long customerId) {
        return orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        return toResponse(order);
    }

    public OrderResponse updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        order.setStatus(newStatus);
        Order updated = orderRepository.save(order);
        return toResponse(updated);
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getOrderDate(),
                order.getStatus(),
                order.getTotalAmount(),
                itemResponses
        );
    }
}