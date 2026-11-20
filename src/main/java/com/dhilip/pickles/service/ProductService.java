package com.dhilip.pickles.service;

import com.dhilip.pickles.entity.Product;
import com.dhilip.pickles.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product addProduct(Product product) {
        product.setId(null);
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Product not found"));
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Product existing = getProductById(id);
        existing.setName(updatedProduct.getName());
        existing.setDescription(updatedProduct.getDescription());
        existing.setPrice(updatedProduct.getPrice());
        existing.setStockQuantity(updatedProduct.getStockQuantity());
        existing.setImageUrl(updatedProduct.getImageUrl());
        existing.setActive(updatedProduct.getActive());
        return productRepository.save(existing);
    }

    public void deleteProduct(Long id) {
        productRepository.delete(getProductById(id));
    }
}
