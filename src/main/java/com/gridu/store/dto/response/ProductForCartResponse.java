package com.gridu.store.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductForCartResponse {

    private Long numberOfProduct;
    private String title;
    private double price;
    private Long quantities;
}
