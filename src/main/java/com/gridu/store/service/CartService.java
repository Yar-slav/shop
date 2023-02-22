package com.gridu.store.service;

import com.gridu.store.dto.request.UserCartModifyDto;
import com.gridu.store.dto.request.UserCartRequestDto;
import com.gridu.store.dto.response.CartResponseDto;

public interface CartService {

    void addItemToCart(UserCartRequestDto requestDto);

    CartResponseDto getCart();

    void deleteProductFromCart(Long id);

    void modifyNumberOfItem(UserCartModifyDto requestDto);
}
