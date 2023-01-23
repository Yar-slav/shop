package com.gridu.store.service;

import com.gridu.store.dto.request.UserCartModifyDto;
import com.gridu.store.dto.request.UserCartRequestDto;
import com.gridu.store.dto.response.CartResponseDto;
import com.gridu.store.dto.response.ProductForCartResponse;
import com.gridu.store.dto.response.ProductResponseDto;
import com.gridu.store.exception.ApiException;
import com.gridu.store.exception.Exceptions;
import com.gridu.store.model.ProductEntity;
import com.gridu.store.model.UserEntity;
import com.gridu.store.model.CartEntity;
import com.gridu.store.repository.ProductRepo;
import com.gridu.store.repository.UserRepo;
import com.gridu.store.repository.CartRepo;
import com.gridu.store.secure.config.JwtService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final JwtService jwtService;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;
    private final CartRepo cartRepo;

    @Transactional
    @Override
    public ProductResponseDto addItemToCart(UserCartRequestDto requestDto, String authHeader) {
        UserEntity userEntity = getUserEntityByToken(authHeader);
        ProductEntity productEntity = getProductEntity(requestDto.getId());
        pickUpAndSaveProductsFromTheStoreIfQuantityAvailable(requestDto.getQuantity(), productEntity);
        addProductToCart(requestDto, userEntity);
        return ProductResponseDto.builder()
                .id(productEntity.getId())
                .title(productEntity.getTitle())
                .available(requestDto.getQuantity())
                .price(productEntity.getPrice())
                .build();
    }

    @Override
    public CartResponseDto getCart(String authHeader) {
        Long productsNumber = 0L;
        double totalPrice = 0;
        UserEntity userEntity = getUserEntityByToken(authHeader);
        List<CartEntity> allCartByUser = cartRepo.findAllByUser(userEntity);
        List<ProductForCartResponse> products = new ArrayList<>();
        for (CartEntity cart: allCartByUser) {
            productsNumber++;
            ProductEntity product = cart.getProduct();
            products.add(ProductForCartResponse.builder()
                    .numberOfProduct(productsNumber)
                    .title(product.getTitle())
                    .price(product.getPrice())
                    .quantities(cart.getQuantity())
                    .build());
            totalPrice += product.getPrice() * cart.getQuantity();
        }
        return CartResponseDto.builder()
                .products(products)
                .totalPrice(totalPrice)
                .build();
    }

    @Transactional
    @Override
    public Boolean deleteProductFromCart(Long id, String authHeader) {
        UserEntity userEntity = getUserEntityByToken(authHeader);
        ProductEntity productEntity = getProductEntity(id);
        CartEntity cartEntity = getCartEntityByUserAndProductId(userEntity, id);
        cartRepo.delete(cartEntity);
        productEntity.setAvailable(productEntity.getAvailable() + cartEntity.getQuantity());
        productRepo.save(productEntity);
        return true;
    }

    @Transactional
    @Override
    public ProductResponseDto modifyNumberOfItem(String authHeader, UserCartModifyDto requestDto) {
        UserEntity userEntity = getUserEntityByToken(authHeader);
        Long productId = requestDto.getProductId();
        ProductEntity productEntity = getProductEntity(productId);
        CartEntity cartEntity = getCartEntityByUserAndProductId(userEntity, productId);
        long needQuantity = requestDto.getQuantity() - cartEntity.getQuantity();
        pickUpAndSaveProductsFromTheStoreIfQuantityAvailable(needQuantity, productEntity);
        cartEntity.setQuantity(requestDto.getQuantity());
        cartRepo.save(cartEntity);
        return ProductResponseDto.builder()
                .id(cartEntity.getProduct().getId())
                .title(cartEntity.getProduct().getTitle())
                .price(cartEntity.getProduct().getPrice())
                .available(requestDto.getQuantity())
                .build();
    }


    private void pickUpAndSaveProductsFromTheStoreIfQuantityAvailable(
            Long needQuantity, ProductEntity productEntity) {
        if(productEntity.getAvailable() >= needQuantity) {
            productEntity.setAvailable(productEntity.getAvailable() - needQuantity);
            productRepo.save(productEntity);
        } else {
            throw new ApiException(Exceptions.PRODUCTS_NOT_ENOUGH);
        }
    }

    private void addProductToCart(UserCartRequestDto requestDto, UserEntity userEntity) {
        boolean cartEntityExist = false;
        ProductEntity productEntity = getProductEntity(requestDto.getId());
        CartEntity byUserAndProductId = cartRepo.findByUserAndProductId(
                userEntity, requestDto.getId()).orElse(null);
        if(byUserAndProductId != null) {
            byUserAndProductId.setQuantity(byUserAndProductId.getQuantity() + requestDto.getQuantity());
            cartRepo.save(byUserAndProductId);
            cartEntityExist = true;
        }
        if(!cartEntityExist) {
            CartEntity cartEntity = CartEntity.builder()
                    .user(userEntity)
                    .product(productEntity)
                    .quantity(requestDto.getQuantity())
                    .build();
            cartRepo.save(cartEntity);
        }
    }

    private ProductEntity getProductEntity(Long productId) {
        return productRepo.findById(productId)
                .orElseThrow(() -> new ApiException(Exceptions.PRODUCT_NOT_FOUND));
    }

    private CartEntity getCartEntityByUserAndProductId(UserEntity userEntity, Long productId) {
        return cartRepo.findByUserAndProductId(userEntity, productId)
                .orElseThrow(() -> new ApiException(Exceptions.PRODUCT_NOT_FOUND));
    }

    private UserEntity getUserEntityByToken(String authHeader) {
        String token = authHeader.substring(7);
        String userEmail = jwtService.extractUsername(token);
        return userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new ApiException(Exceptions.USER_NOT_FOUND));
    }
}
