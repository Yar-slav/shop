package com.gridu.store.service.implementation;

import com.gridu.store.dto.response.MessageResponseDto;
import com.gridu.store.dto.response.OrderResponseDto;
import com.gridu.store.model.OrderDetailEntity;
import com.gridu.store.model.OrderEntity;
import com.gridu.store.model.OrderStatus;
import com.gridu.store.model.ShopItemEntity;
import com.gridu.store.model.UserEntity;
import com.gridu.store.repository.OrderDetailRepo;
import com.gridu.store.repository.OrderRepo;
import com.gridu.store.service.CartService;
import com.gridu.store.service.OrderService;
import com.gridu.store.service.ProductService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepo orderRepo;
    private final OrderDetailRepo orderDetailRepo;
    private final CartService cartService;
    private final ProductService productService;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void checkout(UserEntity userEntity) {
        BigDecimal totalPrice = BigDecimal.valueOf(0D);
        HashMap<Long, Long> itemsList = cartService.getItemsList();
        if (itemsList.isEmpty()) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(202), "Your cart is empty");
        }
        for (Map.Entry<Long, Long> entry : itemsList.entrySet()) {
            Long quantity = entry.getValue();
            ShopItemEntity shopItem = productService.getShopItem(entry.getKey());
            cartService.checkQuantity(shopItem.getAvailable(), quantity);
            shopItem.setAvailable(shopItem.getAvailable() - quantity);
            totalPrice = totalPrice.add(shopItem.getProduct().getPrice().multiply(BigDecimal.valueOf(quantity)));
        }
        OrderEntity order = getOrderEntity(userEntity, totalPrice);
        orderRepo.save(order);
        addToOrderDetailEntity(itemsList, order);
        itemsList.clear();

    }

    @Transactional
    @Override
    public MessageResponseDto cancelOrder(Long orderId, UserEntity userEntity) {
        OrderEntity order = orderRepo.findByIdAndUser(orderId, userEntity)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Order not found"));
        if(order.getOrderStatus().equals(OrderStatus.CANCEL)){
            throw new ResponseStatusException(HttpStatusCode.valueOf(202), "Order already canceled");
        }
        List<OrderDetailEntity> orderDetailEntities = orderDetailRepo.findAllByOrder(order);
        for (OrderDetailEntity orderDetail : orderDetailEntities) {
            ShopItemEntity shopItem = productService.getShopItem(orderDetail.getProductId());
            shopItem.setAvailable(shopItem.getAvailable() + orderDetail.getQuantity());
        }
        orderDetailRepo.deleteAllByOrder(order);
        order.setOrderStatus(OrderStatus.CANCEL);
        order.setCanceledOn(LocalDateTime.now());
        return new MessageResponseDto("The order: " + orderId + " has been canceled successfully");
    }

    @Override
    public List<OrderResponseDto> getAllOrder(UserEntity userEntity) {
        List<OrderResponseDto> orderResponseDtoList = new ArrayList<>();
        List<OrderEntity> orders = orderRepo.findAllByUser(userEntity);
        for(OrderEntity order: orders) {
            orderResponseDtoList.add(getOrderResponseDto(order));
        }
        orderResponseDtoList = orderResponseListSortedByDate(orderResponseDtoList);
        return orderResponseDtoList;
    }

    private void addToOrderDetailEntity(HashMap<Long, Long> itemsList, OrderEntity order) {
        for (Map.Entry<Long, Long> entry : itemsList.entrySet()) {
            orderDetailRepo.save(OrderDetailEntity.builder()
                    .productId(entry.getKey())
                    .quantity(entry.getValue())
                    .order(order)
                    .build()
            );
        }
    }

    private OrderEntity getOrderEntity(UserEntity userEntity, BigDecimal totalPrice) {
        return OrderEntity.builder()
                .user(userEntity)
                .orderStatus(OrderStatus.PLACED)
                .orderedOn(LocalDateTime.now())
                .totalPrice(totalPrice)
                .build();
    }

    private OrderResponseDto getOrderResponseDto(OrderEntity order) {
        return OrderResponseDto.builder()
                .orderId(order.getId())
                .status(order.getOrderStatus())
                .date(getDateTime(order))
                .totalPrice(order.getTotalPrice())
                .build();
    }

    private static LocalDateTime getDateTime(OrderEntity order) {
        if (order.getOrderStatus().equals(OrderStatus.CANCEL)) {
            return order.getCanceledOn();
        } else {
            return order.getOrderedOn();
        }
    }

    private static List<OrderResponseDto> orderResponseListSortedByDate(List<OrderResponseDto> orderResponseDtoList) {
        return orderResponseDtoList.stream()
                .sorted(Comparator.comparing(OrderResponseDto::getDate).reversed())
                .collect(Collectors.toList());
    }
}
