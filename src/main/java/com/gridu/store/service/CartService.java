package com.gridu.store.service;

import com.gridu.store.dto.request.UserCartModifyDto;
import com.gridu.store.dto.request.UserCartRequestDto;
import com.gridu.store.dto.response.CartResponseDto;
import com.gridu.store.dto.response.ProductResponseDto;
import com.gridu.store.model.UserEntity;

public interface CartService {

    void addItemToCart(UserCartRequestDto requestDto, UserEntity userEntity);

    CartResponseDto getCart(UserEntity userEntity);

    void deleteProductFromCart(Long id, UserEntity userEntity);

    void modifyNumberOfItem(UserEntity userEntity, UserCartModifyDto requestDto);
}
