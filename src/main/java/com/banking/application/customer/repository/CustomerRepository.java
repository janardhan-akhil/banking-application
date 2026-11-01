package com.banking.application.customer.repository;

import com.banking.application.customer.entity.Customer;
import com.banking.application.customer.entity.CustomerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByEmail(String email);
    Optional<Customer> findByCustomerReference(String reference);
    long countByStatus(CustomerStatus status);
}
