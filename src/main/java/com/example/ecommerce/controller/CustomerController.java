package com.example.ecommerce.controller;
import com.example.ecommerce.dto.CustomerRegistrationRequest;
import com.example.ecommerce.dto.CustomerResponse;
import com.example.ecommerce.dto.CustomerUpdateRequest;
import com.example.ecommerce.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/register")
    public ResponseEntity<CustomerResponse> register(@Valid @RequestBody CustomerRegistrationRequest request) {
        CustomerResponse response = customerService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getProfile(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateProfile(@PathVariable Long id,
                                                          @RequestBody CustomerUpdateRequest request) {
        return ResponseEntity.ok(customerService.updateProfile(id, request));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newPassword = body.get("newPassword");
        customerService.changePassword(id, newPassword);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        customerService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}