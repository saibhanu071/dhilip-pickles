package com.dhilip.pickles.controller;

import com.dhilip.pickles.entity.Customer;
import com.dhilip.pickles.security.JwtService;
import com.dhilip.pickles.service.CustomerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CustomerService customerService;
    private final JwtService jwtService;

    public AuthController(
            CustomerService customerService,
            JwtService jwtService) {
        this.customerService = customerService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {

        Customer customer = new Customer();
        customer.setFullName(request.fullName());
        customer.setMobile(request.mobile());
        customer.setEmail(request.email().trim().toLowerCase());
        customer.setPassword(request.password());

        Customer savedCustomer = customerService.register(customer);

        return new AuthResponse(
                jwtService.generateToken(savedCustomer),
                CustomerResponse.from(savedCustomer)
        );
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {

        Customer customer = customerService.validateLogin(
                request.email().trim().toLowerCase(),
                request.password()
        );

        return new AuthResponse(
                jwtService.generateToken(customer),
                CustomerResponse.from(customer)
        );
    }

    public record RegisterRequest(
            @NotBlank @Size(max = 100) String fullName,
            @NotBlank @Pattern(regexp = "^[6-9][0-9]{9}$") String mobile,
            @NotBlank @Email String email,
            @NotBlank @Size(min = 8, max = 72) String password
    ) {}

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {}

    public record CustomerResponse(
            Long id,
            String fullName,
            String mobile,
            String email,
            String role
    ) {
        public static CustomerResponse from(Customer customer) {
            return new CustomerResponse(
                    customer.getId(),
                    customer.getFullName(),
                    customer.getMobile(),
                    customer.getEmail(),
                    customer.getRole()
            );
        }
    }

    public record AuthResponse(
            String token,
            CustomerResponse customer
    ) {}
}
