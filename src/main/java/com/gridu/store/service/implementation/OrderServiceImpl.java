package com.gridu.store.service.implementation;

import com.gridu.store.dto.response.MessageResponseDto;
import com.gridu.store.dto.response.OrderResponseDto;
import com.gridu.store.model.CartEntity;
import com.gridu.store.model.CartStatus;
import com.gridu.store.model.ProductEntity;
import com.gridu.store.model.UserEntity;
import com.gridu.store.repository.CartRepo;
import com.gridu.store.service.OrderService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {


    private final SessionFactory sessionFactory;
    private final CartRepo cartRepo;

    @SneakyThrows
    @Transactional
    @Override
    public void checkout(UserEntity userEntity) {
        List<CartEntity> allCartByUser = cartRepo.findAllByUserAndCartStatus(userEntity, CartStatus.ADDED_TO_CART);
        if (allCartByUser.size() == 0) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(202), "Your cart is empty");
        }
        Long orderId = generateOrderId();
        LocalDateTime orderedOn = LocalDateTime.now();
        for (CartEntity cart : allCartByUser) {
            ProductEntity product = cart.getProduct();
            if (product.getAvailable() < cart.getQuantity()) {
                throw new ResponseStatusException(HttpStatusCode.valueOf(202), "Amount of products not enough");
            }
            product.setAvailable(product.getAvailable() - cart.getQuantity());

            cart.setOrderedOn(orderedOn);
            cart.setOrderId(orderId);
            cart.setCartStatus(CartStatus.ORDER_PLACED);
        }
    }

    @Transactional
    @Override
    public MessageResponseDto cancelOrder(Long orderId, UserEntity userEntity) {
        List<CartEntity> cartsByOrderId = getCartsByOrderIdWithStatusOrderPlaced(orderId);
        LocalDateTime canceledOn = LocalDateTime.now();
        for (CartEntity cart : cartsByOrderId) {
            if (!cart.getUser().equals(userEntity)) {
                throw new ResponseStatusException(HttpStatusCode.valueOf(404), "This number of order does not belong to you");
            }
            ProductEntity product = cart.getProduct();
            product.setAvailable(product.getAvailable() + cart.getQuantity());

            cart.setCanceledOn(canceledOn);
            cart.setCartStatus(CartStatus.CANCEL);
        }
        return new MessageResponseDto("The order: " + orderId + " has been canceled successfully");
    }

    @Override
    public List<OrderResponseDto> getAllOrder(UserEntity userEntity) {
        List<OrderResponseDto> orderResponseDtoList = new ArrayList<>();
        List<CartEntity> carts = cartRepo.findAllByUser(userEntity);
        Map<Long, List<CartEntity>> map = carts.stream()
                .filter(cart -> !cart.getCartStatus().equals(CartStatus.ADDED_TO_CART))
                .collect(Collectors.groupingBy(CartEntity::getOrderId));

        for (Map.Entry<Long, List<CartEntity>> cartEntities : map.entrySet()) {
            orderResponseDtoList.add(getOrderResponseDto(cartEntities));
        }
        orderResponseDtoList = orderResponseListSortedByDate(orderResponseDtoList);
        return orderResponseDtoList;
    }

    private static OrderResponseDto getOrderResponseDto(Map.Entry<Long, List<CartEntity>> cartEntities) {
        CartEntity cart = cartEntities.getValue().get(0);
        return OrderResponseDto.builder()
                .orderId(cartEntities.getKey())
                .status(cart.getCartStatus())
                .date(getDateTime(cart))
                .totalPrice(getOrderTotalPrice(cartEntities.getValue()))
                .build();
    }

    private static LocalDateTime getDateTime(CartEntity cart) {
        if (cart.getCartStatus().equals(CartStatus.CANCEL)) {
            return cart.getCanceledOn();
        } else {
            return cart.getOrderedOn();
        }
    }

    private static double getOrderTotalPrice(List<CartEntity> carts) {
        double totalPrice = 0.0;
        for (CartEntity cart : carts) {
            totalPrice += cart.getProduct().getPrice() * cart.getQuantity();
        }
        return totalPrice;
    }

    private List<CartEntity> getCartsByOrderIdWithStatusOrderPlaced(Long orderId) {
        List<CartEntity> cartsByOrderId = cartRepo.findAllByOrderIdAndCartStatus(orderId, CartStatus.ORDER_PLACED);
        if (cartsByOrderId.equals(Collections.emptyList())) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404), "Order not found");
        }
        return cartsByOrderId;
    }

    // Please, use separate order entity to couple order items with each other
    // With this approach you would still can face situation when you have the same orderId for items from different orders
    // Also it doesn't scale well and require additional unnecessary calls to database
    // Let databases do this job for us
    private Long generateOrderId() {
        Long orderId;
        do {
            orderId = Math.abs(new Random().nextLong());
        } while (!cartRepo.findAllByOrderId(orderId).equals(Collections.emptyList()));
        return orderId;
    }

    private static List<OrderResponseDto> orderResponseListSortedByDate(List<OrderResponseDto> orderResponseDtoList) {
        return orderResponseDtoList.stream()
                .sorted(Comparator.comparing(OrderResponseDto::getDate).reversed())
                .collect(Collectors.toList());
    }
}
