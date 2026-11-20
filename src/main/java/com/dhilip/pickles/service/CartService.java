package com.dhilip.pickles.service;

import com.dhilip.pickles.entity.CartItem;
import com.dhilip.pickles.entity.Product;
import com.dhilip.pickles.repository.CartItemRepository;
import com.dhilip.pickles.repository.ProductRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(
            CartItemRepository cartRepository,
            ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public CartResponse addItem(String mobile, Long productId, Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Quantity must be greater than zero"
            );
        }

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Product not found"
            ));

        CartItem item = cartRepository
            .findByCustomerMobileAndProductId(mobile, productId)
            .orElse(new CartItem(mobile, productId, 0));

        int newQuantity = item.getQuantity() + quantity;

        if (newQuantity > product.getStockQuantity()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Not enough stock available"
            );
        }

        item.setQuantity(newQuantity);
        cartRepository.save(item);

        return getCart(mobile);
    }

    public CartResponse getCart(String mobile) {

        List<CartLine> lines = cartRepository.findByCustomerMobile(mobile)
            .stream()
            .map(item -> {
                Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found"
                    ));

                BigDecimal lineTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

                return new CartLine(
                    item.getId(),
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    item.getQuantity(),
                    lineTotal
                );
            })
            .toList();

        BigDecimal total = lines.stream()
            .map(CartLine::lineTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(mobile, lines, total);
    }

    @Transactional
    public CartResponse updateQuantity(
            String mobile, Long itemId, Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Quantity must be greater than zero"
            );
        }

        CartItem item = cartRepository.findByIdAndCustomerMobile(itemId, mobile)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Cart item not found"
            ));

        Product product = productRepository.findById(item.getProductId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Product not found"
            ));

        if (quantity > product.getStockQuantity()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Not enough stock available"
            );
        }

        item.setQuantity(quantity);
        cartRepository.save(item);

        return getCart(mobile);
    }

    @Transactional
    public void removeItem(String mobile, Long itemId) {

        CartItem item = cartRepository.findByIdAndCustomerMobile(itemId, mobile)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Cart item not found"
            ));

        cartRepository.delete(item);
    }

    public record CartLine(
        Long cartItemId,
        Long productId,
        String productName,
        BigDecimal price,
        Integer quantity,
        BigDecimal lineTotal
    ) {}

    public record CartResponse(
        String customerMobile,
        List<CartLine> items,
        BigDecimal total
    ) {}
}
