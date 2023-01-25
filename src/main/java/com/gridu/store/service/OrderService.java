package com.gridu.store.service;

import com.gridu.store.dto.response.CheckoutResponseDto;

public interface OrderService {

    CheckoutResponseDto checkout(String authHeader);
}
