package com.gridu.store.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

// The task stated:
// The information has to be session-scoped: once session expires - user will get new empty cart.
//
// Database - isn't a good choice for storing carts
// because you need to cover at least closing the browser - and reopening it
// to imitate creating a new session and receiving a new cart
// I'd suggest to use sessions for such case, no need to store it in a database
// You may store orders here, not cart
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "carts")
@Builder
public class CartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @ToString.Exclude
    private ProductEntity product;

    private Long quantity;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CartStatus cartStatus;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "ordered_on")
    private LocalDateTime orderedOn;

    @Column(name = "canceled_on")
    private LocalDateTime canceledOn;
}
