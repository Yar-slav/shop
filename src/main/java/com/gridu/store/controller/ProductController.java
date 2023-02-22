package com.gridu.store.controller;

import com.gridu.store.dto.request.ProductRequestDto;
import com.gridu.store.dto.response.ProductShopResponseDto;
import com.gridu.store.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping()
    public ProductShopResponseDto addProduct(
            @Valid @RequestBody ProductRequestDto requestDto) {
        return productService.addProduct(requestDto);
    }

    @GetMapping
    public List<ProductShopResponseDto> getAllProducts(
            Pageable pageable) {
        return productService.getAll(pageable);
    }
}
