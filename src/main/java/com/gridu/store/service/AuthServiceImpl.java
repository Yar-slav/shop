package com.gridu.store.service;

import com.gridu.store.dto.request.UserLoginRequest;
import com.gridu.store.dto.request.UserRegistrationRequestDto;
import com.gridu.store.dto.response.LoginResponseDto;
import com.gridu.store.dto.response.UserRegistrationResponseDto;
import com.gridu.store.exception.ApiException;
import com.gridu.store.exception.Exceptions;
import com.gridu.store.model.UserEntity;
import com.gridu.store.model.UserRole;
import com.gridu.store.repository.UserRepo;
import com.gridu.store.secure.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    @Override
    public UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto) {
        checkIfUserExist(requestDto);
        UserEntity userEntity = UserEntity.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .userRole(UserRole.USER)
                .build();
        userRepo.save(userEntity);
        String token = jwtService.generateToken(userEntity);
        return UserRegistrationResponseDto.builder()
                .message("User with email: " + userEntity.getEmail() + " is successfully registered")
                .token("Token: "+ token)
                .build();
    }

    @Transactional
    @Override
    public LoginResponseDto login(UserLoginRequest requestDto) {
        authenticate(requestDto);
        UserEntity userEntity = userRepo.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new ApiException(Exceptions.USER_NOT_FOUND));
        String token = jwtService.generateToken(userEntity);
        return LoginResponseDto.builder()
                .token(token)
                .build();
    }

    private void authenticate(UserLoginRequest requestDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getEmail(),
                            requestDto.getPassword())).isAuthenticated();
        } catch (BadCredentialsException e) {
            throw new ApiException(Exceptions.USER_INCORRECT_PASSWORD);
        }
    }

    private void checkIfUserExist(UserRegistrationRequestDto userRegistrationRequestDto) {
        boolean present = userRepo
                .findByEmail(userRegistrationRequestDto.getEmail())
                .isPresent();
        if(present) {
            throw new ApiException(Exceptions.USER_EXIST);
        }
    }
}
