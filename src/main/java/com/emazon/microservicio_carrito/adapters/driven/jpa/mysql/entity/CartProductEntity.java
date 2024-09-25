package com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.entity;

import com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.util.DrivenConstants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = DrivenConstants.CART_PRODUCT_TABLE_NAME)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = DrivenConstants.COLUMN_CART_PRODUCT_ID)
    private Long cartProductId;

    @Column(name = DrivenConstants.COLUMN_CART_ID, nullable = false)
    private Long cartId;

    @Column(name = DrivenConstants.COLUMN_CART_PRODUCT_PRODUCT_ID, nullable = false)
    private Long productId;

    @Column(name = DrivenConstants.COLUMN_CART_PRODUCT_NAME, nullable = false)
    private String name;

    @Column(name = DrivenConstants.COLUMN_CART_PRODUCT_QUANTITY, nullable = false)
    private Long quantity;

    @Column(name = DrivenConstants.COLUMN_CART_UNIT_PRICE, nullable = false)
    private BigDecimal unitPrice;

    @Column(name = DrivenConstants.COLUMN_CART_TOTAL_PRICE, nullable = false)
    private BigDecimal totalPrice;
}
