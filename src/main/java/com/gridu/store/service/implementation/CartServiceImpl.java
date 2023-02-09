package com.gridu.store.service.implementation;

import com.gridu.store.dto.request.UserCartModifyDto;
import com.gridu.store.dto.request.UserCartRequestDto;
import com.gridu.store.dto.response.CartResponseDto;
import com.gridu.store.dto.response.ProductInformationForCart;
import com.gridu.store.model.CartEntity;
import com.gridu.store.model.CartStatus;
import com.gridu.store.model.ProductEntity;
import com.gridu.store.model.UserEntity;
import com.gridu.store.repository.CartRepo;
import com.gridu.store.service.CartService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepo cartRepo;
    private final ProductServiceImpl productService;

    @Transactional
    @Override
    public void addItemToCart(UserCartRequestDto requestDto, UserEntity userEntity) {
        ProductEntity productEntity = productService.getProductEntity(requestDto.getId());
        CartEntity existCart = cartRepo.findByUserAndProductIdAndCartStatus(
                userEntity, requestDto.getId(), CartStatus.ADDED_TO_CART).orElse(null);
        if (existCart != null) {
            long needQuantity = existCart.getQuantity() + requestDto.getQuantity();
            checkingWhetherTheProductsQuantityIsAvailable(needQuantity, productEntity.getAvailable());
            existCart.setQuantity(needQuantity);
        } else {
            checkingWhetherTheProductsQuantityIsAvailable(requestDto.getQuantity(), productEntity.getAvailable());
            CartEntity cartEntity = CartEntity.builder()
                    .user(userEntity)
                    .product(productEntity)
                    .quantity(requestDto.getQuantity())
                    .cartStatus(CartStatus.ADDED_TO_CART)
                    .build();
            cartRepo.save(cartEntity);
        }
    }

    @Transactional
    @Override
    public CartResponseDto getCart(UserEntity userEntity) {
        List<ProductInformationForCart> products = new ArrayList<>();
        Long productsNumber = 0L;
        double totalPrice = 0;
        List<CartEntity> allCartByUser = cartRepo.findAllByUserAndCartStatus(userEntity, CartStatus.ADDED_TO_CART);
        for (CartEntity cart : allCartByUser) {
            productsNumber++;
            ProductEntity product = cart.getProduct();
            double subtotalPrice = product.getPrice() * cart.getQuantity();
            products.add(getProductForCartResponse(productsNumber, cart, product, subtotalPrice));
            totalPrice += subtotalPrice;
        }
        return CartResponseDto.builder()
                .products(products)
                .totalPrice(totalPrice)
                .build();
    }

    @Override
    public void deleteProductFromCart(Long id, UserEntity userEntity) {
        CartEntity cartEntity = getCartByUserAndProductIdWithStatusAddedToCart(userEntity, id);
        cartRepo.delete(cartEntity);
    }

    @Transactional
    @Override
    public void modifyNumberOfItem(UserEntity userEntity, UserCartModifyDto requestDto) {
        Long productId = requestDto.getProductId();
        ProductEntity productEntity = productService.getProductEntity(productId);
        CartEntity cartEntity = getCartByUserAndProductIdWithStatusAddedToCart(userEntity, productId);

        checkingWhetherTheProductsQuantityIsAvailable(requestDto.getQuantity(), productEntity.getAvailable());

        cartEntity.setQuantity(requestDto.getQuantity());
    }

    private static ProductInformationForCart getProductForCartResponse(
            Long productsNumber, CartEntity cart, ProductEntity product, double subtotalPrice) {
        return ProductInformationForCart.builder()
                .numberOfProduct(productsNumber)
                .title(product.getTitle())
                .price(product.getPrice())
                .quantities(cart.getQuantity())
                .subtotalPrice(subtotalPrice)
                .build();
    }

    private void checkingWhetherTheProductsQuantityIsAvailable(Long needQuantity, Long available) {
        if (available < needQuantity) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(202), "Amount of products not enough");
        }
    }

    private CartEntity getCartByUserAndProductIdWithStatusAddedToCart(UserEntity userEntity, Long productId) {
        return cartRepo.findByUserAndProductIdAndCartStatus(userEntity, productId, CartStatus.ADDED_TO_CART)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Product not found"));
    }
}
