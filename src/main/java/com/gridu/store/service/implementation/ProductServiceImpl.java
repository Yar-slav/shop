package com.gridu.store.service.implementation;

import com.gridu.store.dto.request.ProductRequestDto;
import com.gridu.store.dto.response.ProductResponseDto;
import com.gridu.store.mapper.ProductMapper;
import com.gridu.store.model.ProductEntity;
import com.gridu.store.model.UserEntity;
import com.gridu.store.repository.ProductRepo;
import com.gridu.store.service.ProductService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;
    private final ProductMapper productMapper;

    @Override
    public List<ProductResponseDto> getAll(Pageable pageable, UserEntity userEntity) {
        return productRepo.findAll(pageable).stream()
                .map(productMapper::toProductResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ProductResponseDto addProduct(ProductRequestDto requestDto, UserEntity userEntity) {
        ProductEntity productEntity = productMapper.toProductEntity(requestDto);
        ProductEntity byTitleAndPrice = productRepo.findByTitleAndPrice(requestDto.getTitle(), requestDto.getPrice());
        if (byTitleAndPrice != null) {
            byTitleAndPrice.setAvailable(byTitleAndPrice.getAvailable() + requestDto.getQuantity());
            productEntity = byTitleAndPrice;
        }
        productEntity = productRepo.save(productEntity);
        return productMapper.toProductResponseDto(productEntity);
    }

    public ProductEntity getProductEntity(Long productId) {
        return productRepo.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Product not found"));
    }
}
