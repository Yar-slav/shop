package com.gridu.store.service;

import com.gridu.store.dto.request.UserLoginRequest;
import com.gridu.store.dto.request.UserRegistrationRequestDto;
import com.gridu.store.dto.response.LoginResponseDto;
import com.gridu.store.model.UserEntity;

public interface AuthService {

    void register(UserRegistrationRequestDto userRegistrationRequestDto);

    LoginResponseDto login(UserLoginRequest request);

    UserEntity getUserEntityByToken(String authHeader);
}
