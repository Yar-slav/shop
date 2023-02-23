package com.gridu.store.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.gridu.store.dto.response.MessageResponseDto;
import com.gridu.store.dto.response.OrderResponseDto;
import com.gridu.store.model.OrderDetailEntity;
import com.gridu.store.model.OrderEntity;
import com.gridu.store.model.OrderStatus;
import com.gridu.store.model.ProductEntity;
import com.gridu.store.model.ShopItemEntity;
import com.gridu.store.model.UserEntity;
import com.gridu.store.model.UserRole;
import com.gridu.store.repository.OrderDetailRepo;
import com.gridu.store.repository.OrderRepo;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private  CartServiceImpl cartService;

    @Mock
    private ProductServiceImpl productService;
    @Mock
    private OrderRepo orderRepo;
    @Mock
    private OrderDetailRepo orderDetailRepo;
    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void checkout_ifYourCartNotEmpty() {
        UserEntity user = new UserEntity(1L, "user@gmail.com", "passwordEncode", UserRole.USER, null);
        HashMap<Long, Long> itemsList = new HashMap<>();
        itemsList.put(1L, 100L);
        itemsList.put(2L, 100L);
        ProductEntity product1 = new ProductEntity(1L, "book1", 100, null);
        ProductEntity product2 = new ProductEntity(2L, "book2", 100, null);
        ShopItemEntity shopItem1 = new ShopItemEntity(1L, 10L ,product1);
        ShopItemEntity shopItem2 = new ShopItemEntity(2L, 10L ,product2);
        OrderEntity order = new OrderEntity(1L, user, OrderStatus.ORDER_PLACED, LocalDateTime.now(), null, 2000.0);

        when(cartService.getItemsList()).thenReturn(itemsList);
        when(productService.getShopItem(1L)).thenReturn(shopItem1);
        when(productService.getShopItem(2L)).thenReturn(shopItem2);
        when(orderRepo.save(any(OrderEntity.class))).thenReturn(order);
        orderService.checkout(user);

        assertTrue(itemsList.isEmpty());
    }

    @Test
    void checkout_ifYourCartEmpty() {
        UserEntity user = new UserEntity(1L, "user@gmail.com", "passwordEncode", UserRole.USER, null);
        HashMap<Long, Long> itemsList = new HashMap<>();

        when(cartService.getItemsList()).thenReturn(itemsList);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> orderService.checkout(user));
        assertEquals("Your cart is empty", exception.getReason());
        assertEquals(HttpStatusCode.valueOf(202), exception.getStatusCode());
    }

    @Test
    void cancelOrder() {
        MessageResponseDto responseDto = new MessageResponseDto("The order: " + 1 + " has been canceled successfully");
        UserEntity user = new UserEntity(1L, "user@gmail.com", "passwordEncode", UserRole.USER, null);
        OrderEntity order = new OrderEntity(1L, user, OrderStatus.ORDER_PLACED, LocalDateTime.now(), null, 2000.0);
        List<OrderDetailEntity> orderDetailEntities = new ArrayList<>();
        orderDetailEntities.add(new OrderDetailEntity(1L, 1L, 10L, order));
        orderDetailEntities.add(new OrderDetailEntity(2L, 2L, 10L, order));
        ShopItemEntity shopItem1 = new ShopItemEntity(1L, 10L, any());
        ShopItemEntity shopItem2 = new ShopItemEntity(2L, 10L, any());

        when(orderRepo.findByIdAndUser(1L, user)).thenReturn(Optional.of(order));
        when(orderDetailRepo.findAllByOrder(order)).thenReturn(orderDetailEntities);
        when(productService.getShopItem(1L)).thenReturn(shopItem1);
        when(productService.getShopItem(2L)).thenReturn(shopItem2);
        doNothing().when(orderDetailRepo).deleteAllByOrder(order);

        MessageResponseDto result = orderService.cancelOrder(1L, user);

        assertEquals(order.getOrderStatus(), OrderStatus.CANCEL);
        assertEquals(responseDto, result);

    }

    @Test
    void cancelOrder_orderNotFound() {
        UserEntity user = new UserEntity(1L, "user@gmail.com", "passwordEncode", UserRole.USER, null);

        when(orderRepo.findByIdAndUser(9L, user)).thenReturn(Optional.empty());


        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> orderService.cancelOrder(9L, user));
        assertEquals("Order not found", exception.getReason());
        assertEquals(HttpStatusCode.valueOf(404), exception.getStatusCode());
    }

    @Test
    void cancelOrder_orderAlreadyCanceled() {
        UserEntity user = new UserEntity(1L, "user@gmail.com", "passwordEncode", UserRole.USER, null);
        OrderEntity order = new OrderEntity(9L, user, OrderStatus.CANCEL, LocalDateTime.now(), null, 2000.0);

        when(orderRepo.findByIdAndUser(9L, user)).thenReturn(Optional.of(order));


        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> orderService.cancelOrder(9L, user));
        assertEquals("Order already canceled", exception.getReason());
        assertEquals(HttpStatusCode.valueOf(202), exception.getStatusCode());
    }

    @Test
    void getAllOrder() {
        UserEntity user = new UserEntity(1L, "user@gmail.com", "passwordEncode", UserRole.USER, null);
        OrderEntity order1 = new OrderEntity(1L, user, OrderStatus.CANCEL, LocalDateTime.now(), LocalDateTime.now(), 2000.0);
        OrderEntity order2 = new OrderEntity(2L, user, OrderStatus.ORDER_PLACED, LocalDateTime.now(), null, 5000.0);
        List<OrderResponseDto> responseDtoList = new ArrayList<>();
        responseDtoList.add(new OrderResponseDto(order2.getId(), order2.getOrderedOn(), order2.getTotalPrice(), order2.getOrderStatus()));
        responseDtoList.add(new OrderResponseDto(order1.getId(), order1.getCanceledOn(), order1.getTotalPrice(), order1.getOrderStatus()));
        List<OrderEntity> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);

        when(orderRepo.findAllByUser(user)).thenReturn(orders);
        List<OrderResponseDto> result = orderService.getAllOrder(user);
        assertEquals(responseDtoList, result);

    }
}