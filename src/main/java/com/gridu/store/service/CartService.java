package com.gridu.store.service;

import com.gridu.store.dto.request.UserCartModifyDto;
import com.gridu.store.dto.request.UserCartRequestDto;
import com.gridu.store.dto.response.CartResponseDto;
import java.util.HashMap;

public interface CartService {

    void addItemToCart(UserCartRequestDto requestDto);

    CartResponseDto getCart();

    void deleteProductFromCart(Long id);

    void modifyNumberOfItem(UserCartModifyDto requestDto);

    HashMap<Long, Long> getItemsList();

    void checkQuantity(Long available, Long needQuantity);
}
