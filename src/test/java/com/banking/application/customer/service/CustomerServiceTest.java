
package com.banking.application.customer.service;

import com.banking.application.customer.dto.CreateCustomerRequest;
import com.banking.application.customer.dto.CustomerResponse;
import com.banking.application.customer.entity.Customer;
import com.banking.application.customer.exception.DuplicateCustomerException;
import com.banking.application.customer.exception.CustomerNotFoundException;
import com.banking.application.customer.repository.CustomerRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void shouldRegisterCustomerSuccessfully() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "John", "Smith", "JOHN@example.com", "+919876543210"
        );

        when(customerRepository.existsByEmail("john@example.com"))
                .thenReturn(false);

        when(customerRepository.save(any(Customer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CustomerResponse response = customerService.register(request);

        assertThat(response.email()).isEqualTo("john@example.com");
        assertThat(response.firstName()).isEqualTo("John");
        assertThat(response.customerReference()).isNotBlank();

        verify(customerRepository).existsByEmail("john@example.com");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void shouldRejectDuplicateEmail() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "John", "Smith", "john@example.com", null
        );

        when(customerRepository.existsByEmail("john@example.com"))
                .thenReturn(true);

        assertThrows(
                DuplicateCustomerException.class,
                () -> customerService.register(request)
        );

        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldThrowWhenCustomerDoesNotExist() {
        when(customerRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.getById(999L)
        );
    }
}
