package com.gridu.store.controller;

import com.gridu.store.dto.request.UserCartModifyDto;
import com.gridu.store.dto.request.UserCartRequestDto;
import com.gridu.store.dto.response.CartResponseDto;
import com.gridu.store.dto.response.ProductResponseDto;
import com.gridu.store.model.UserEntity;
import com.gridu.store.service.CartService;
import com.gridu.store.service.implementation.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final AuthServiceImpl authServiceImpl;

    @PostMapping()
    public void addItemToCart(
            @Valid @RequestBody UserCartRequestDto requestDto,
            @RequestHeader("Authorization") String authHeader
    ) {
        UserEntity userEntity = authServiceImpl.getUserEntityByToken(authHeader);
        cartService.addItemToCart(requestDto, userEntity);
    }

    @GetMapping()
    public CartResponseDto getCart(
            @RequestHeader("Authorization") String authHeader
    ) {
        UserEntity userEntity = authServiceImpl.getUserEntityByToken(authHeader);
        return cartService.getCart(userEntity);
    }

    @DeleteMapping
    public void deleteProduct(
            @RequestParam Long product_id,
            @RequestHeader("Authorization") String authHeader
    ) {
        UserEntity userEntity = authServiceImpl.getUserEntityByToken(authHeader);
        cartService.deleteProductFromCart(product_id, userEntity);
    }

    @PatchMapping()
    public void modifyNumberOfItem(
            @Valid @RequestBody UserCartModifyDto requestDto,
            @RequestHeader("Authorization") String authHeader
    ) {
        UserEntity userEntity = authServiceImpl.getUserEntityByToken(authHeader);
        cartService.modifyNumberOfItem(userEntity, requestDto);
    }
}
