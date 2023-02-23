package com.gridu.store.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductInformationForCart {
    private Long numberOfProduct;
    private String title;
    private BigDecimal price;
    private Long quantities;
    private BigDecimal subtotalPrice;
}
