package com.gridu.store.service;

import com.gridu.store.dto.request.ProductRequestDto;
import com.gridu.store.dto.response.ProductResponseDto;
import com.gridu.store.model.UserEntity;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    List<ProductResponseDto> getAll(Pageable pageable, UserEntity userEntity);

    ProductResponseDto addProduct(ProductRequestDto requestDto, UserEntity userEntity);

}
