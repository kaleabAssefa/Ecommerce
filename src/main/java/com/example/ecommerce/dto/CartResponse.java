package com.example.ecommerce.dto;
import java.math.BigDecimal;
import java.util.List;

public class CartResponse {

    private List<CartItemResponse> items;
    private BigDecimal cartTotal;

    public CartResponse() {
    }

    public CartResponse(List<CartItemResponse> items, BigDecimal cartTotal) {
        this.items = items;
        this.cartTotal = cartTotal;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public void setItems(List<CartItemResponse> items) {
        this.items = items;
    }

    public BigDecimal getCartTotal() {
        return cartTotal;
    }

    public void setCartTotal(BigDecimal cartTotal) {
        this.cartTotal = cartTotal;
    }
}