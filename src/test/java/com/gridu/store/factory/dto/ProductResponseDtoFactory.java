package com.gridu.store.factory.dto;

import com.gridu.store.dto.response.ProductShopResponseDto;
import java.util.List;

public class ProductResponseDtoFactory {

    public static List<ProductShopResponseDto> createProductResponseDTOs() {
     return List.of(
                new ProductShopResponseDto(1L, "phone", 10L, 2000),
                new ProductShopResponseDto(2L, "phone2", 10L, 3000),
                new ProductShopResponseDto(3L, "phone3", 10L, 4000)
        );
    }
}
