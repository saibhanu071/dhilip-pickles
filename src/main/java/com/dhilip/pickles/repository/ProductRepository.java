package com.dhilip.pickles.repository;

import com.dhilip.pickles.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
