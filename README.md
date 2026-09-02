# Ecommerce
An e-commerce backend built with Java and Spring Boot, created as a learning project to understand REST APIs, JPA/Hibernate, layered architecture, and SQL databases from the ground up.
src/main/java/com/example/ecommerce/
├── EcommerceApplication.java   # Main entry point
├── entity/                     # Database entities (JPA/Hibernate models)
│   ├── Customer.java
│   ├── Product.java
│   ├── CartItem.java
│   ├── Order.java
│   ├── OrderItem.java
│   └── OrderStatus.java        # Enum: PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
├── repository/                 # Spring Data JPA interfaces (database access)
│   ├── CustomerRepository.java
│   ├── ProductRepository.java
│   ├── CartItemRepository.java
│   ├── OrderRepository.java
│   └── OrderItemRepository.java
├── service/                    # Business logic layer
│   ├── CustomerService.java
│   ├── ProductService.java
│   ├── CartService.java
│   └── OrderService.java
├── controller/                 # REST API endpoints
│   ├── CustomerController.java
│   ├── ProductController.java
│   ├── CartController.java
│   └── OrderController.java
├── dto/                        # Request/response objects (keep entities decoupled from the API)
│   ├── CustomerRegistrationRequest.java
│   ├── CustomerResponse.java
│   ├── CustomerUpdateRequest.java
│   ├── AddToCartRequest.java
│   ├── UpdateCartItemRequest.java
│   ├── CartItemResponse.java
│   ├── CartResponse.java
│   ├── OrderItemResponse.java
│   └── OrderResponse.java
└── exception/                  # Custom exceptions and global error handling
    ├── ResourceNotFoundException.java
    ├── DuplicateResourceException.java
    ├── InsufficientStockException.java
    ├── ErrorResponse.java
    └── GlobalExceptionHandler.java
