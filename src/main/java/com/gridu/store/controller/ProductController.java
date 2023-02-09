package com.gridu.store.controller;

import com.gridu.store.dto.request.ProductRequestDto;
import com.gridu.store.dto.response.ProductResponseDto;
import com.gridu.store.model.UserEntity;
import com.gridu.store.service.ProductService;
import com.gridu.store.service.implementation.AuthServiceImpl;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final AuthServiceImpl authServiceImpl;


    @PostMapping()
    public ProductResponseDto addProduct(
            @Valid @RequestBody ProductRequestDto requestDto,
            @RequestHeader("Authorization") String authHeader) {
        UserEntity userEntity = authServiceImpl.getUserEntityByToken(authHeader);
        return productService.addProduct(requestDto, userEntity);
    }

    @GetMapping
    public List<ProductResponseDto> getAllProducts(
            Pageable pageable,
            @RequestHeader("Authorization") String authHeader) {
        UserEntity userEntity = authServiceImpl.getUserEntityByToken(authHeader);
        return productService.getAll(pageable, userEntity);
    }
}
