package com.dhilip.pickles.config;

import com.dhilip.pickles.entity.Customer;
import com.dhilip.pickles.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.bootstrap:false}")
    private boolean enabled;

    @Value("${app.admin.full-name:}")
    private String fullName;

    @Value("${app.admin.mobile:}")
    private String mobile;

    @Value("${app.admin.email:}")
    private String email;

    @Value("${app.admin.password:}")
    private String password;

    public AdminBootstrap(
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!enabled) {
            return;
        }

        if (customerRepository.existsByRole("ADMIN")) {
            System.out.println("Admin account already exists. Bootstrap skipped.");
            return;
        }

        if (fullName.isBlank() || email.isBlank()
                || password.isBlank() || mobile.isBlank()
                || !mobile.matches("^[6-9][0-9]{9}$")
                || password.length() < 12) {
            throw new IllegalStateException(
                    "Valid admin details are required. Password must be at least 12 characters.");
        }

        if (customerRepository.existsByEmail(email)
                || customerRepository.existsByMobile(mobile)) {
            throw new IllegalStateException(
                    "Admin email or mobile already belongs to another account.");
        }

        Customer admin = new Customer();
        admin.setFullName(fullName);
        admin.setMobile(mobile);
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRole("ADMIN");

        customerRepository.save(admin);

        System.out.println("Initial ADMIN account created successfully.");
    }
}
