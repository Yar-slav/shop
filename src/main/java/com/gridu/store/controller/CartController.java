package com.gridu.store.controller;

import com.gridu.store.dto.request.UserShoppingCartRequestDto;
import com.gridu.store.dto.response.CartResponseDto;
import com.gridu.store.dto.response.ProductResponseDto;
import com.gridu.store.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/cart")
    public ResponseEntity<ProductResponseDto> addItemToCart(
            @Valid @RequestBody UserShoppingCartRequestDto requestDto,
            @RequestHeader("Authorization") String authHeader
    ) {
        return ResponseEntity.ok(cartService.addItemToCart(requestDto, authHeader));
    }

    @GetMapping("/cart")
    public ResponseEntity<CartResponseDto> getCart(
            @RequestHeader("Authorization") String authHeader
    ) {
        return ResponseEntity.ok(cartService.getCart(authHeader));
    }

    @DeleteMapping("/cart")
    public ResponseEntity<Boolean> deleteProduct(
            @RequestParam Long product_id,
            @RequestHeader("Authorization") String authHeader
    ) {
        return ResponseEntity.ok(cartService.deleteProductFromCart(product_id, authHeader));
    }
}
