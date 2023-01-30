package com.gridu.store.service;

import com.gridu.store.dto.request.ProductRequestDto;
import com.gridu.store.dto.response.ProductResponseDto;
import java.util.List;

public interface ProductService {

    List<ProductResponseDto> getAll();

    ProductResponseDto addProduct(ProductRequestDto requestDtom, String token);

}
