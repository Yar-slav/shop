package com.gridu.store.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UserCartModifyDto {

    @JsonProperty("id")
    private Long productId;

    @Min(value = 1, message = "Quantity should be one or greater")
    private Long quantity;
}
