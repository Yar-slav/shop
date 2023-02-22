package com.gridu.store.controller;

import com.gridu.store.dto.response.MessageResponseDto;
import com.gridu.store.dto.response.OrderResponseDto;
import com.gridu.store.model.UserEntity;
import com.gridu.store.service.OrderService;
import com.gridu.store.service.implementation.AuthServiceImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final AuthServiceImpl authServiceImpl;

    @PutMapping("/checkout")
    public void checkout(
            @RequestHeader("Authorization") String authHeader) {
        UserEntity userEntity = authServiceImpl.getUserEntityByToken(authHeader);
        orderService.checkout(userEntity);
    }

    @PatchMapping("/cancel/{number-of-order}")
    public MessageResponseDto cancelOrder(
            @PathVariable(name = "number-of-order") Long numberOfOrder,
            @RequestHeader("Authorization") String authHeader) {
        UserEntity userEntity = authServiceImpl.getUserEntityByToken(authHeader);
        return orderService.cancelOrder(numberOfOrder, userEntity);
    }

    @GetMapping
    public List<OrderResponseDto> getAllOrder(
            @RequestHeader("Authorization") String authHeader) {
        UserEntity userEntity = authServiceImpl.getUserEntityByToken(authHeader);
    return orderService.getAllOrder(userEntity);
    }
}
