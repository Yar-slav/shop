package com.gridu.store.dto;

import com.gridu.store.lib.ValidEmail;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true, builderMethodName = "of")
public class UserDto {

    @ValidEmail
    private String email;

    @NotBlank(message = "Password can't be null or whitespace")
    private String password;
}
