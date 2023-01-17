package com.gridu.store.service;

import com.gridu.store.dto.EmailConfirmationResponseDto;
import com.gridu.store.dto.UserRegistrationRequestDto;
import com.gridu.store.dto.UserRegistrationResponseDto;

public interface UserService {

    UserRegistrationResponseDto register(UserRegistrationRequestDto userRegistrationRequestDto);

    EmailConfirmationResponseDto confirmation(String token);


}
