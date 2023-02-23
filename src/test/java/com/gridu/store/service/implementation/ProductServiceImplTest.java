package com.gridu.store.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.gridu.store.dto.request.ProductRequestDto;
import com.gridu.store.dto.response.ProductShopResponseDto;
import com.gridu.store.model.ProductEntity;
import com.gridu.store.model.ShopItemEntity;
import com.gridu.store.repository.ProductRepo;
import com.gridu.store.repository.ShopItemRepo;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepo productRepo;

    @Mock
    private ShopItemRepo shopItemRepo;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void getAllProducts() {
        List<ShopItemEntity> productsEntity = new ArrayList<>();
        productsEntity.add(new ShopItemEntity(1L, 10L, new ProductEntity(1L, "book1", BigDecimal.valueOf(100), null)));
        productsEntity.add(new ShopItemEntity(2L, 10L, new ProductEntity(2L, "book2", BigDecimal.valueOf(200), null)));
        productsEntity.add(new ShopItemEntity(3L, 10L, new ProductEntity(3L, "book3", BigDecimal.valueOf(300), null)));
        Pageable pageable = PageRequest.of(0, 3);
        Page<ShopItemEntity> pageEntity = new PageImpl<>(productsEntity);

        when(shopItemRepo.findAll(pageable)).thenReturn(pageEntity);

        List<ProductShopResponseDto> result = productService.getAll(pageable);
        assertEquals(result.size(), 3);
    }

    @Test
    void addProduct_ifProductNotExist() {
        ProductShopResponseDto responseDto = new ProductShopResponseDto(1L, "book", 10L, BigDecimal.valueOf(300));
        ProductRequestDto requestDto = new ProductRequestDto("book", 10L, BigDecimal.valueOf(300));
        ProductEntity product = new ProductEntity(1L, "book", BigDecimal.valueOf(300), null);
        ShopItemEntity shopItemEntity = new ShopItemEntity(1L, 10L, product);

        when(shopItemRepo.findByProductTitleAndProductPrice(requestDto.getTitle(), requestDto.getPrice())).thenReturn(null);
        lenient().when(productRepo.save(any(ProductEntity.class))).thenReturn(product);
        when(shopItemRepo.save(any(ShopItemEntity.class))).thenReturn(shopItemEntity);

        ProductShopResponseDto result = productService.addProduct(requestDto);
        assertEquals(responseDto, result);
    }

    @Test
    void addProduct_ifProductExist() {
        ProductShopResponseDto responseDto = new ProductShopResponseDto(2L, "book", 11L, BigDecimal.valueOf(300));
        ProductRequestDto requestDto = new ProductRequestDto("book", 1L, BigDecimal.valueOf(300));
        ProductEntity product = new ProductEntity(2L, "book", BigDecimal.valueOf(300), null);
        ShopItemEntity existingItem = new ShopItemEntity(2L, 10L, product);

        when(shopItemRepo.findByProductTitleAndProductPrice(requestDto.getTitle(), requestDto.getPrice()))
                .thenReturn(existingItem);

        ProductShopResponseDto result = productService.addProduct(requestDto);
        assertEquals(responseDto, result);
    }

    @Test
    void getProduct_IfProductExist() {
        ProductEntity product = new ProductEntity(1L, "book", BigDecimal.valueOf(300), null);
        when(productRepo.findById(product.getId())).thenReturn(Optional.of(product));

        ProductEntity result = productService.getProduct(product.getId());
        assertEquals(product, result);
    }

    @Test
    void getProduct_IfProductNotExist() {
        when(productRepo.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                        () -> productService.getProduct(1L));
        assertEquals("Product not found", exception.getReason());
        assertEquals(HttpStatusCode.valueOf(404), exception.getStatusCode());
    }

    @Test
    void getShopItem_IfShopItemExist() {
        ProductEntity product = new ProductEntity(1L, "book", BigDecimal.valueOf(300), null);
        ShopItemEntity shopItem = new ShopItemEntity(2L, 10L, product);

        when(shopItemRepo.findById(shopItem.getId())).thenReturn(Optional.of(shopItem));

        ShopItemEntity result = productService.getShopItem(shopItem.getId());
        assertEquals(shopItem, result);
    }

    @Test
    void getShopItem_IfShopItemNotExist() {
        when(shopItemRepo.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.getShopItem(1L));
        assertEquals("Item not found", exception.getReason());
        assertEquals(HttpStatusCode.valueOf(404), exception.getStatusCode());
    }
}