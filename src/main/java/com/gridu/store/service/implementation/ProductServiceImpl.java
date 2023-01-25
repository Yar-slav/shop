package com.gridu.store.service.implementation;

import com.gridu.store.dto.request.ProductRequestDto;
import com.gridu.store.dto.response.ProductResponseDto;
import com.gridu.store.mapper.ProductMapper;
import com.gridu.store.model.ProductEntity;
import com.gridu.store.repository.ProductRepo;
import com.gridu.store.service.ProductService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;
    private final ProductMapper productMapper;

    @Override
    public List<ProductResponseDto> getAll() {
        List<ProductEntity> allProducts = productRepo.findAll();
        return allProducts.stream()
                .map(productMapper::toProductResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponseDto addProduct(ProductRequestDto requestDto) {
        ProductEntity productEntity = productMapper.toProductEntity(requestDto);
        ProductEntity byTitleAndPrice = productRepo.findByTitleAndPrice(requestDto.getTitle(), requestDto.getPrice());
        if(byTitleAndPrice != null){
            byTitleAndPrice.setAvailable(byTitleAndPrice.getAvailable() + requestDto.getQuantity());
            productEntity = byTitleAndPrice;
        }
        productRepo.save(productEntity);
        return productMapper.toProductResponseDto(productEntity);
    }
}
