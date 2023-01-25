package com.gridu.store.service.implementation;

import com.gridu.store.dto.response.CheckoutResponseDto;
import com.gridu.store.exception.ApiException;
import com.gridu.store.exception.Exceptions;
import com.gridu.store.model.CartEntity;
import com.gridu.store.model.ProductEntity;
import com.gridu.store.model.UserEntity;
import com.gridu.store.repository.CartRepo;
import com.gridu.store.repository.ProductRepo;
import com.gridu.store.service.OrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final AuthServiceImpl authServiceImpl;
    private final CartRepo cartRepo;
    private final ProductRepo productRepo;

    @Transactional
    @Override
    public CheckoutResponseDto checkout(String authHeader) {
        UserEntity userEntity = authServiceImpl.getUserEntityByToken(authHeader);
        List<CartEntity> allCartByUser = cartRepo.findAllByUser(userEntity);
        for (CartEntity cart: allCartByUser) {
            ProductEntity product = cart.getProduct();
            if (product.getAvailable() < cart.getQuantity()) {
                throw new ApiException(Exceptions.PRODUCTS_NOT_ENOUGH);
            }
            product.setAvailable(product.getAvailable() - cart.getQuantity());
            productRepo.save(product);
        }
        cartRepo.deleteByUser(userEntity);
        return CheckoutResponseDto.builder()
                .message("The order has been placed successfully")
                .build();
    }
}
