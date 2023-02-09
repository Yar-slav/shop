package com.gridu.store.service;

import com.gridu.store.dto.request.UserLoginRequest;
import com.gridu.store.dto.request.UserRegistrationRequestDto;
import com.gridu.store.dto.response.LoginResponseDto;
import com.gridu.store.dto.response.MessageResponseDto;

public interface AuthService {

    void register(UserRegistrationRequestDto userRegistrationRequestDto);

    LoginResponseDto login(UserLoginRequest token);
}
