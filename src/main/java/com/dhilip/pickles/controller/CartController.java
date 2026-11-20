package com.dhilip.pickles.controller;

import com.dhilip.pickles.entity.Customer;
import com.dhilip.pickles.repository.CustomerRepository;
import com.dhilip.pickles.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final CustomerRepository customerRepository;

    public CartController(
            CartService cartService,
            CustomerRepository customerRepository) {
        this.cartService = cartService;
        this.customerRepository = customerRepository;
    }

    private String getCustomerMobile(Authentication authentication) {
        Customer customer = customerRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Customer not found"));

        return customer.getMobile();
    }

    @PostMapping("/items")
    public CartService.CartResponse addItem(
            Authentication authentication,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {

        return cartService.addItem(
                getCustomerMobile(authentication), productId, quantity);
    }

    @GetMapping
    public CartService.CartResponse getCart(Authentication authentication) {
        return cartService.getCart(getCustomerMobile(authentication));
    }

    @PutMapping("/items/{itemId}")
    public CartService.CartResponse updateQuantity(
            Authentication authentication,
            @PathVariable Long itemId,
            @RequestParam Integer quantity) {

        return cartService.updateQuantity(
                getCustomerMobile(authentication), itemId, quantity);
    }

    @DeleteMapping("/items/{itemId}")
    public String removeItem(
            Authentication authentication,
            @PathVariable Long itemId) {

        cartService.removeItem(getCustomerMobile(authentication), itemId);
        return "Cart item removed successfully";
    }
}
