package com.dhilip.pickles.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "cart_items",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"customer_mobile", "product_id"}
    )
)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_mobile", nullable = false)
    private String customerMobile;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    public CartItem() {
    }

    public CartItem(String customerMobile, Long productId, Integer quantity) {
        this.customerMobile = customerMobile;
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public String getCustomerMobile() {
        return customerMobile;
    }

    public void setCustomerMobile(String customerMobile) {
        this.customerMobile = customerMobile;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
