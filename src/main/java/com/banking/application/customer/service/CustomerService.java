
package com.banking.application.customer.service;

import com.banking.application.customer.dto.CreateCustomerRequest;
import com.banking.application.customer.dto.CustomerResponse;
import com.banking.application.customer.entity.Customer;
import com.banking.application.customer.entity.CustomerStatus;
import com.banking.application.customer.exception.CustomerNotFoundException;
import com.banking.application.customer.exception.DuplicateCustomerException;
import com.banking.application.customer.repository.CustomerRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public CustomerResponse register(CreateCustomerRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if (customerRepository.existsByEmail(email)) {
            throw new DuplicateCustomerException(email);
        }

        Customer customer = new Customer(
                UUID.randomUUID().toString(),
                request.firstName().trim(),
                request.lastName().trim(),
                email,
                request.phoneNumber()
        );

        return toResponse(customerRepository.save(customer));
    }

    @Transactional(readOnly = true)
    public CustomerResponse getById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        return toResponse(customer);
    }

    @Transactional(readOnly = true)
    public Page<CustomerResponse> getAll(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public long countActiveCustomers() {
        return customerRepository.countByStatus(CustomerStatus.ACTIVE);
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getCustomerReference(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getStatus(),
                customer.getCreatedAt()
        );
    }
}
