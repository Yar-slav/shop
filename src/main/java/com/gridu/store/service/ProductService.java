package com.gridu.store.service;

import com.gridu.store.dto.request.ProductRequestDto;
import com.gridu.store.dto.response.ProductShopResponseDto;
import com.gridu.store.model.ProductEntity;
import com.gridu.store.model.ShopItemEntity;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    List<ProductShopResponseDto> getAll(Pageable pageable);

    ProductShopResponseDto addProduct(ProductRequestDto requestDto);

    ProductEntity getProduct(Long id);
    ShopItemEntity getShopItem(Long id);
}
