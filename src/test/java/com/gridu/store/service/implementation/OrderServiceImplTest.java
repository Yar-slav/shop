//package com.gridu.store.service.implementation;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.when;
//
//import com.gridu.store.dto.response.CheckoutResponseDto;
//import com.gridu.store.exception.ApiException;
//import com.gridu.store.exception.Exceptions;
//import com.gridu.store.model.CartEntity;
//import com.gridu.store.model.ProductEntity;
//import com.gridu.store.model.UserEntity;
//import com.gridu.store.model.UserRole;
//import com.gridu.store.repository.CartRepo;
//import com.gridu.store.repository.ProductRepo;
//import java.util.List;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//@ExtendWith(MockitoExtension.class)
//class OrderServiceImplTest {
//
//    private final String token = "Bearer token";
//
//    @Mock
//    private AuthServiceImpl authServiceImpl;
//    @Mock
//    private CartRepo cartRepo;
//    @Mock
//    private ProductRepo productRepo;
//    @InjectMocks
//    private OrderServiceImpl orderService;
//
//    @Test
//    void checkout() {
//        CheckoutResponseDto responseDto = CheckoutResponseDto.builder().message("The order has been placed successfully").build();
//        UserEntity user = new UserEntity(1L, "user@gmail.com", "passwordEncode", UserRole.USER, null);
//        ProductEntity productEntity1 = new ProductEntity(5L, "book1", 100L, 300, null);
//        ProductEntity productEntity2 = new ProductEntity(6L, "book2", 100L, 500, null);
//        List<CartEntity> allCartByUser = List.of(
//                new CartEntity(1L, user, productEntity1, 10L),
//                new CartEntity(2L, user, productEntity2, 10L)
//        );
//
//        when(authServiceImpl.getUserEntityByToken(token)).thenReturn(user);
//        when(cartRepo.findAllByUser(user)).thenReturn(allCartByUser);
//        for (CartEntity cartEntity : allCartByUser) {
//            ProductEntity product = cartEntity.getProduct();
//            product.setAvailable(product.getAvailable() - cartEntity.getQuantity());
//            when(productRepo.save(product)).thenReturn(product);
//        }
//        doNothing().when(cartRepo).deleteByUser(user);
//
//        CheckoutResponseDto result = orderService.checkout(token);
//        assertEquals(responseDto, result);
//    }
//
//    @Test
//    void checkout_ifProductNotEnough() {
//        UserEntity user = new UserEntity(1L, "user@gmail.com", "passwordEncode", UserRole.USER, null);
//        ProductEntity productEntity1 = new ProductEntity(5L, "book1", 100L, 300, null);
//        ProductEntity productEntity2 = new ProductEntity(6L, "book2", 100L, 500, null);
//        List<CartEntity> allCartByUser = List.of(
//                new CartEntity(1L, user, productEntity1, 1000L),
//                new CartEntity(2L, user, productEntity2, 10L)
//        );
//
//        when(authServiceImpl.getUserEntityByToken(token)).thenReturn(user);
//        when(cartRepo.findAllByUser(user)).thenReturn(allCartByUser);
//        ApiException apiException = assertThrows(ApiException.class,
//                () -> orderService.checkout(token));
//
//        assertEquals(Exceptions.PRODUCTS_NOT_ENOUGH, apiException.getExceptions());
//    }
//}