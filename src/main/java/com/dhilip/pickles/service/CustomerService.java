package com.dhilip.pickles.service;

import com.dhilip.pickles.entity.Customer;
import com.dhilip.pickles.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Customer register(Customer customer) {

        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email already registered");
        }

        if (customerRepository.existsByMobile(customer.getMobile())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Mobile already registered");
        }

        customer.setId(null);
        customer.setRole("CUSTOMER");
        customer.setPassword(
                passwordEncoder.encode(customer.getPassword()));

        return customerRepository.save(customer);
    }

    public Customer validateLogin(String email, String password) {

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid email or password"));

        if (!passwordEncoder.matches(
                password, customer.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password");
        }

        return customer;
    }
}
