package com.gridu.store.controller;

import com.gridu.store.dto.response.CheckoutResponseDto;
import com.gridu.store.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PutMapping("/checkout")
    public ResponseEntity<CheckoutResponseDto> checkout(
            @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(orderService.checkout(authHeader));
    }
}
