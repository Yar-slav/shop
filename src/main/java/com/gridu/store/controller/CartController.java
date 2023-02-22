package com.gridu.store.controller;

import com.gridu.store.dto.request.UserCartModifyDto;
import com.gridu.store.dto.request.UserCartRequestDto;
import com.gridu.store.dto.response.CartResponseDto;
import com.gridu.store.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping()
    public void addItemToCart(
            @Valid @RequestBody UserCartRequestDto requestDto) {
        cartService.addItemToCart(requestDto);
    }

    @GetMapping()
    public CartResponseDto getCart() {
        return cartService.getCart();
    }

    @DeleteMapping
    public void deleteProduct(
            @RequestParam Long product_id
    ) {
        cartService.deleteProductFromCart(product_id);
    }

    @PatchMapping()
    public void modifyNumberOfItem(
            @Valid @RequestBody UserCartModifyDto requestDto
    ) {
        cartService.modifyNumberOfItem(requestDto);
    }
}
