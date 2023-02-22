package com.gridu.store.service.implementation;

import com.gridu.store.dto.request.ProductRequestDto;
import com.gridu.store.dto.response.ProductShopResponseDto;
import com.gridu.store.model.ProductEntity;
import com.gridu.store.model.ShopItemEntity;
import com.gridu.store.repository.ProductRepo;
import com.gridu.store.repository.ShopItemRepo;
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
    private final ShopItemRepo shopItemRepo;

    @Override
    public List<ProductShopResponseDto> getAll(Pageable pageable) {
        return shopItemRepo.findAll(pageable).stream()
                .map(ProductServiceImpl::getProductResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ProductShopResponseDto addProduct(ProductRequestDto requestDto) {
        ShopItemEntity shopItemByProductTitleAndProductPrice = shopItemRepo.findByProductTitleAndProductPrice(
                requestDto.getTitle(), requestDto.getPrice());
        if (shopItemByProductTitleAndProductPrice != null) {
            shopItemByProductTitleAndProductPrice.setAvailable(
                    shopItemByProductTitleAndProductPrice.getAvailable() + requestDto.getQuantity());
            return getProductResponseDto(shopItemByProductTitleAndProductPrice);
        } else {
            ProductEntity product = getProduct(requestDto);
            ShopItemEntity shopItemEntity = getShopItemEntity(requestDto, product);
            product.setShopItem(shopItemEntity);
            shopItemEntity = shopItemRepo.save(shopItemEntity);
            return getProductResponseDto(shopItemEntity);
        }
    }

    private static ProductShopResponseDto getProductResponseDto(ShopItemEntity shopItemEntity) {
        return ProductShopResponseDto.builder()
                .id(shopItemEntity.getId())
                .title(shopItemEntity.getProduct().getTitle())
                .price(shopItemEntity.getProduct().getPrice())
                .available(shopItemEntity.getAvailable())
                .build();
    }

    private static ShopItemEntity getShopItemEntity(ProductRequestDto requestDto, ProductEntity product) {
        return ShopItemEntity.builder()
                .product(product)
                .available(requestDto.getQuantity())
                .build();
    }

    private static ProductEntity getProduct(ProductRequestDto requestDto) {
        return ProductEntity.builder()
                .title(requestDto.getTitle())
                .price(requestDto.getPrice())
                .build();
    }

    public ShopItemEntity getShopItem(Long id) {
        return shopItemRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Item not found"));
    }

    public ProductEntity getProduct(Long id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Product not found"));
    }
}
