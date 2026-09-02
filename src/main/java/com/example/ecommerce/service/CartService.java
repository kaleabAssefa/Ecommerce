package com.example.ecommerce.service;

import com.example.ecommerce.dto.*;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import com.example.ecommerce.exception.InsufficientStockException;
import com.example.ecommerce.exception.ResourceNotFoundException;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CartService(CartItemRepository cartItemRepository,
                       CustomerRepository customerRepository,
                       ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    public CartResponse addToCart(AddToCartRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + request.getCustomerId()));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));

        var existingItem = cartItemRepository.findByCustomerIdAndProductId(
                request.getCustomerId(), request.getProductId());

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();
            validateStock(product, newQuantity);
            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        } else {
            validateStock(product, request.getQuantity());
            CartItem newItem = new CartItem(customer, product, request.getQuantity());
            cartItemRepository.save(newItem);
        }

        return getCart(request.getCustomerId());
    }

    public CartResponse getCart(Long customerId) {
        List<CartItem> items = cartItemRepository.findByCustomerId(customerId);

        List<CartItemResponse> itemResponses = items.stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());

        BigDecimal total = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(itemResponses, total);
    }

    public CartResponse updateQuantity(Long cartItemId, UpdateCartItemRequest request) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with id: " + cartItemId));

        validateStock(item.getProduct(), request.getQuantity());
        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        return getCart(item.getCustomer().getId());
    }

    public CartResponse removeItem(Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with id: " + cartItemId));

        Long customerId = item.getCustomer().getId();
        cartItemRepository.deleteById(cartItemId);

        return getCart(customerId);
    }

    private void validateStock(Product product, int requestedQuantity) {
        if (requestedQuantity > product.getStockQuantity()) {
            throw new InsufficientStockException("Requested quantity (" + requestedQuantity +
                    ") exceeds available stock (" + product.getStockQuantity() + ") for product: " + product.getName());
        }
    }

    private CartItemResponse toItemResponse(CartItem item) {
        BigDecimal subtotal = item.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        return new CartItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getPrice(),
                item.getQuantity(),
                subtotal
        );
    }
}