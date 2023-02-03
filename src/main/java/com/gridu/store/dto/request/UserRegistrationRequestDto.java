package com.gridu.store.dto.request;

import com.gridu.store.lib.ValidEmail;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationRequestDto {

    // Optional: that's cool that you know how to implement custom validators
    // but there is a predefined @Email validator in jakarta standard :)
    @ValidEmail
    private String email;

    @NotBlank(message = "Password can't be null or whitespace")
    private String password;
}
