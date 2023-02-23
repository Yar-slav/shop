package com.gridu.store.dto.response;

import com.gridu.store.model.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {
    private Long orderId;
    private LocalDateTime date;
    private BigDecimal totalPrice;
    private OrderStatus status;
}
