package com.gridu.store.dto.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CartResponseDto {
    private List<ProductInformationForCart> products;
    private BigDecimal totalPrice;
}
