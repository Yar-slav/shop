package com.gridu.store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductForCartResponse {
    // Optional: This naming is confusing a little,
    // If it's for ordering purposes then I'd suggest to use order word in the name, so it would be easier to understand
    private Long numberOfProduct;
    private String title;
    private double price;
    private Long quantities;
    private double subtotalPrice;
}
