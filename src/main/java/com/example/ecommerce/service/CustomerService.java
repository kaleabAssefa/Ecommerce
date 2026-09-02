package com.example.ecommerce.service;
import com.example.ecommerce.dto.CustomerRegistrationRequest;
import com.example.ecommerce.dto.CustomerResponse;
import com.example.ecommerce.dto.CustomerUpdateRequest;
import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponse register(CustomerRegistrationRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        Customer customer = new Customer(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword(), // plain text for now — hashed properly in Phase 9
                request.getPhoneNumber()
        );

        Customer saved = customerRepository.save(customer);
        return toResponse(saved);
    }

    public CustomerResponse getProfile(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        return toResponse(customer);
    }

    public CustomerResponse updateProfile(Long id, CustomerUpdateRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setPhoneNumber(request.getPhoneNumber());

        Customer updated = customerRepository.save(customer);
        return toResponse(updated);
    }

    public void changePassword(Long id, String newPassword) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        customer.setPassword(newPassword); // plain text for now — hashed properly in Phase 9
        customerRepository.save(customer);
    }

    public void deleteAccount(Long id) {
        customerRepository.deleteById(id);
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getCreatedAt()
        );
    }
}