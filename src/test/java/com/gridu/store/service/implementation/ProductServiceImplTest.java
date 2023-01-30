package com.gridu.store.service.implementation;

import static com.gridu.store.factory.dto.ProductResponseDtoFactory.createProductResponseDTOs;
import static com.gridu.store.factory.model.ProductEntityFactory.createProductsEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.gridu.store.dto.request.ProductRequestDto;
import com.gridu.store.dto.response.ProductResponseDto;
import com.gridu.store.mapper.ProductMapper;
import com.gridu.store.model.ProductEntity;
import com.gridu.store.repository.ProductRepo;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    private final String token = "Bearer token";

    @Mock
    private ProductRepo productRepo;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    @DisplayName("GetAllProducts")
    void getAll() {
        List<ProductResponseDto> productResponseDTOs = createProductResponseDTOs();
        List<ProductEntity> productEntities = createProductsEntity();

        when(productRepo.findAll()).thenReturn(productEntities);
        for (int i = 0; i < productEntities.size(); i++) {
            when(productMapper.toProductResponseDto(productEntities.get(i)))
                    .thenReturn(productResponseDTOs.get(i));
        }

        List<ProductResponseDto> result = productService.getAll();
        assertEquals(productResponseDTOs, result);
    }

    @Test
    @DisplayName("AddProduct_ifProductNotExist")
    void addProduct() {
        ProductResponseDto responseDto = new ProductResponseDto(1L, "book", 10L, 300);
        ProductRequestDto requestDto = new ProductRequestDto("book", 10L, 300);
        ProductEntity productEntity = new ProductEntity(null, "book", 10L, 300, null);
        ProductEntity productEntityAfterSave = new ProductEntity(1L, "book", 10L, 300, null);

        when(productMapper.toProductEntity(requestDto)).thenReturn(productEntity);
        when(productRepo.findByTitleAndPrice("book", 300)).thenReturn(null);
        when(productRepo.save(productEntity)).thenReturn(productEntityAfterSave);
        when(productMapper.toProductResponseDto(productEntityAfterSave)).thenReturn(responseDto);

        ProductResponseDto result = productService.addProduct(requestDto, token);
        assertEquals(responseDto, result);
    }

    @Test
    @DisplayName("AddProduct_ifProductExist")
    void addProductIfExist() {
        ProductResponseDto responseDto = new ProductResponseDto(2L, "book", 11L, 300);
        ProductRequestDto requestDto = new ProductRequestDto("book", 1L, 300);
        ProductEntity productEntity = new ProductEntity(null, "book", 1L, 300, null);
        ProductEntity byTitleAndPrice = new ProductEntity(2L, "book", 10L, 300, null);

        when(productMapper.toProductEntity(requestDto)).thenReturn(productEntity);
        when(productRepo.findByTitleAndPrice("book", 300)).thenReturn(byTitleAndPrice);
        byTitleAndPrice.setAvailable(11L);
        when(productRepo.save(byTitleAndPrice)).thenReturn(byTitleAndPrice);
        when(productMapper.toProductResponseDto(byTitleAndPrice)).thenReturn(responseDto);

        ProductResponseDto result = productService.addProduct(requestDto, token);
        assertEquals(responseDto, result);
    }
}