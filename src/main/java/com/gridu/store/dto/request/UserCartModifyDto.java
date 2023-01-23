package com.gridu.store.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserCartModifyDto {

    @JsonProperty("id")
    private Long productId;
    private Long quantity;
}
