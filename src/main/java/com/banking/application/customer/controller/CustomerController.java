
package com.banking.application.customer.controller;

import com.banking.application.customer.dto.CreateCustomerRequest;
import com.banking.application.customer.dto.CustomerResponse;
import com.banking.application.customer.service.CustomerService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> register(
            @Valid @RequestBody CreateCustomerRequest request) {

        CustomerResponse response = customerService.register(request);

        return ResponseEntity
                .created(URI.create("/api/v1/customers/" + response.id()))
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(customerService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<CustomerResponse>> getAll(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(customerService.getAll(pageable));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countCustomers() {
        long count = customerService.countActiveCustomers();
        return ResponseEntity.ok(count);
    }
}
