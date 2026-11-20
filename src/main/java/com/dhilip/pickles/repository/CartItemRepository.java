package com.dhilip.pickles.repository;

import com.dhilip.pickles.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCustomerMobile(String customerMobile);

    Optional<CartItem> findByCustomerMobileAndProductId(
        String customerMobile, Long productId
    );

    Optional<CartItem> findByIdAndCustomerMobile(
        Long id, String customerMobile
    );
}
