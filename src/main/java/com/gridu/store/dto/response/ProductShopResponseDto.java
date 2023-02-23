package com.gridu.store.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductShopResponseDto {
    private Long id;
    private String title;
    private Long available;
    private BigDecimal price;
}
