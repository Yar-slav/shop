package com.gridu.store.service;

import com.gridu.store.dto.EmailConfirmationResponseDto;
import com.gridu.store.dto.UserRegistrationRequestDto;
import com.gridu.store.dto.UserRegistrationResponseDto;
import com.gridu.store.exception.ApiException;
import com.gridu.store.exception.Exceptions;
import com.gridu.store.mapper.UserMapper;
import com.gridu.store.model.ConfirmationToken;
import com.gridu.store.model.UserEntity;
import com.gridu.store.model.UserRole;
import com.gridu.store.repository.UserRepo;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserMapper userMapper;
    private final UserRepo userRepo;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    @Transactional
    @Override
    public UserRegistrationResponseDto register(UserRegistrationRequestDto userRegistrationRequestDto) {
        checkIfUserExist(userRegistrationRequestDto);
        UserRegistrationResponseDto userRegistrationResponseDto = new UserRegistrationResponseDto();

        UserEntity userEntity = userMapper.toUserEntity(userRegistrationRequestDto);
        String encode = bCryptPasswordEncoder.encode(userEntity.getPassword());
        userEntity.setPassword(encode);
        userEntity.setUserRole(UserRole.USER);
        userEntity = userRepo.save(userEntity);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                userEntity
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        userRegistrationResponseDto.setMessage(
                "User with email: " + userEntity.getEmail() + " is successfully registered");
        userRegistrationResponseDto.setToken("Token: "+ token);
        return userRegistrationResponseDto;
    }

    @Override
    public EmailConfirmationResponseDto confirmation(String token) {
        EmailConfirmationResponseDto emailConfirmationResponseDto = new EmailConfirmationResponseDto();
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() -> new ApiException(Exceptions.TOKEN_NOT_FOUND));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new ApiException(Exceptions.ALREADY_CONFIRMED);
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new ApiException(Exceptions.TOKEN_EXPIRED);
        }

        confirmationTokenService.setConfirmedAt(token);
        emailConfirmationResponseDto.setEmail(confirmationToken.getUserEntity().getEmail());
        emailConfirmationResponseDto.setMessage("Email confirmed");
        return emailConfirmationResponseDto;
    }

    private void checkIfUserExist(UserRegistrationRequestDto userRegistrationRequestDto) {
        boolean present = userRepo
                .findByEmail(userRegistrationRequestDto.getEmail())
                .isPresent();
        if(present) {
            throw new ApiException(Exceptions.USER_EXIST);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email: " + email + " not found"));
    }
}
