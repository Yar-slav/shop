package com.gridu.store.service;

import com.gridu.store.dto.response.MessageResponseDto;
import com.gridu.store.dto.response.OrderResponseDto;
import com.gridu.store.model.UserEntity;
import java.util.List;

public interface OrderService {

    void checkout(UserEntity userEntity);

    MessageResponseDto cancelOrder(Long numberOfOrder, UserEntity userEntity);

    List<OrderResponseDto> getAllOrder(UserEntity userEntity);
}
