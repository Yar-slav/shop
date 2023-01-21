package com.gridu.store.service;

import com.gridu.store.dto.request.UserShoppingCartRequestDto;
import com.gridu.store.dto.response.CartResponseDto;
import com.gridu.store.dto.response.ProductResponseDto;

public interface CartService {

    ProductResponseDto addItemToCart(UserShoppingCartRequestDto requestDto, String authHeader);

    CartResponseDto getCart(String authHeader);
}
