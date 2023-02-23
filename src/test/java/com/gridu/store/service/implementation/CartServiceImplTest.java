package com.gridu.store.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.gridu.store.dto.request.UserCartModifyDto;
import com.gridu.store.dto.request.UserCartRequestDto;
import com.gridu.store.dto.response.CartResponseDto;
import com.gridu.store.dto.response.ProductInformationForCart;
import com.gridu.store.model.ProductEntity;
import com.gridu.store.model.ShopItemEntity;
import com.gridu.store.service.Cart;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private Cart cart;
    @Mock
    private ProductServiceImpl productService;

    @InjectMocks
    private CartServiceImpl cartService;


    @Test
    void AddProductToCart() {
        UserCartRequestDto userCartRequestDto = new UserCartRequestDto(1L, 5L);
        ShopItemEntity shopItem = new ShopItemEntity(1L, 10L, any());
        HashMap<Long, Long> itemsList = new HashMap<>();

        when(productService.getShopItem(1L)).thenReturn(shopItem);
        when(cart.getItemsList()).thenReturn(itemsList);

        cartService.addItemToCart(userCartRequestDto);

        assertEquals(5L, itemsList.get(1L));
    }

    @Test
    void addProductToCart_ifProductAlreadyAddedToCart() {
        UserCartRequestDto userCartRequestDto = new UserCartRequestDto(1L, 5L);
        ShopItemEntity shopItem = new ShopItemEntity(1L, 10L, any());
        HashMap<Long, Long> itemsList = new HashMap<>();
        itemsList.put(1L, 3L);

        when(productService.getShopItem(1L)).thenReturn(shopItem);
        when(cart.getItemsList()).thenReturn(itemsList);

        cartService.addItemToCart(userCartRequestDto);

        assertEquals(8L, itemsList.get(1L));
    }

    @Test
    void addItemToCart_IfProductsQuantityNotEnough() {
        UserCartRequestDto userCartRequestDto = new UserCartRequestDto(1L, 5L);
        ShopItemEntity shopItem = new ShopItemEntity(1L, 10L, any());
        HashMap<Long, Long> itemsList = new HashMap<>();
        itemsList.put(1L, 8L);

        when(productService.getShopItem(1L)).thenReturn(shopItem);
        when(cart.getItemsList()).thenReturn(itemsList);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> cartService.addItemToCart(userCartRequestDto));
        assertEquals("Amount of products not enough", exception.getReason());
        assertEquals(HttpStatusCode.valueOf(202), exception.getStatusCode());
    }


    @Test
    void getCart_ifCartNotEmpty() {
        HashMap<Long, Long> itemsList = new HashMap<>();
        itemsList.put(1L, 8L);
        itemsList.put(2L, 10L);
        ProductEntity product1 = new ProductEntity(1L, "book1", 100, null);
        ProductEntity product2 = new ProductEntity(2L, "book2", 100, null);
        List<ProductInformationForCart> productInformationForCarts = new ArrayList<>();
        productInformationForCarts.add(new ProductInformationForCart(product1.getId(), product1.getTitle(), product1.getPrice(), 8L,  800));
        productInformationForCarts.add(new ProductInformationForCart(product2.getId(), product2.getTitle(), product2.getPrice(), 10L, 1000));
        CartResponseDto cartResponseDto = CartResponseDto.builder()
                .products(productInformationForCarts)
                .totalPrice(1800)
                .build();

        when(cart.getItemsList()).thenReturn(itemsList);
        when(productService.getProduct(1L)).thenReturn(product1);
        when(productService.getProduct(2L)).thenReturn(product2);

        CartResponseDto result = cartService.getCart();
        assertEquals(cartResponseDto, result);
    }

    @Test
    void getCart_ifCartEmpty() {
        HashMap<Long, Long> itemsList = new HashMap<>();
        CartResponseDto cartResponseDto = CartResponseDto.builder()
                .products(Collections.emptyList())
                .totalPrice(0)
                .build();

        when(cart.getItemsList()).thenReturn(itemsList);

        CartResponseDto result = cartService.getCart();
        assertEquals(cartResponseDto, result);
    }

    @Test
    void deleteProductFromCart_ifProductExist() {
        Long productId = 1L;
        HashMap<Long, Long> itemsList = new HashMap<>();
        itemsList.put(1L, 8L);

        when(cart.getItemsList()).thenReturn(itemsList);
        cartService.deleteProductFromCart(productId);

        assertTrue(itemsList.isEmpty());
    }

    @Test
    void deleteProductFromCart_ifProductNotExist() {
        Long productId = 2L;
        HashMap<Long, Long> itemsList = new HashMap<>();
        itemsList.put(1L, 8L);

        when(cart.getItemsList()).thenReturn(itemsList);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> cartService.deleteProductFromCart(productId));
        assertEquals("Product not found", exception.getReason());
        assertEquals(HttpStatusCode.valueOf(404), exception.getStatusCode());
    }

    @Test
    void modifyNumberOfItem_ifProductExist() {
        UserCartModifyDto request = new UserCartModifyDto(1L, 10L);
        ShopItemEntity shopItem = new ShopItemEntity(1L, 10L, any());
        HashMap<Long, Long> itemsList = new HashMap<>();
        itemsList.put(1L, 8L);

        when(productService.getShopItem(request.getProductId())).thenReturn(shopItem);
        when(cart.getItemsList()).thenReturn(itemsList);

        cartService.modifyNumberOfItem(request);

        assertEquals(10L, itemsList.get(request.getProductId()));
    }
}
