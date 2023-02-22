package com.gridu.store.service.implementation;

import com.gridu.store.dto.request.UserLoginRequest;
import com.gridu.store.dto.request.UserRegistrationRequestDto;
import com.gridu.store.dto.response.LoginResponseDto;
import com.gridu.store.model.UserEntity;
import com.gridu.store.model.UserRole;
import com.gridu.store.repository.UserRepo;
import com.gridu.store.secure.config.JwtService;
import com.gridu.store.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public void register(UserRegistrationRequestDto requestDto) {
        checkIfUserExist(requestDto);
        UserEntity userEntity = UserEntity.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .userRole(UserRole.USER)
                .build();
        try {
            userRepo.save(userEntity);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(409), "User with this email already exist");
        }
    }

    @Transactional
    @Override
    public LoginResponseDto login(UserLoginRequest requestDto) {
        authenticate(requestDto);
        UserEntity userEntity = getUserByEmail(requestDto.getEmail());
        String token = jwtService.generateToken(userEntity);
        return new LoginResponseDto(token);
    }

    private void authenticate(UserLoginRequest requestDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getEmail(),
                            requestDto.getPassword()));
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(401), "Incorrect password");
        }
    }

    private void checkIfUserExist(UserRegistrationRequestDto userRegistrationRequestDto) {
        boolean existsByEmail = userRepo.existsByEmail(userRegistrationRequestDto.getEmail());
        if(existsByEmail) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(409), "User with this email already exist");
        }
    }

    public UserEntity getUserEntityByToken(String authHeader) {
        String token = authHeader.substring(7);
        String userEmail = jwtService.extractUsername(token);
        return getUserByEmail(userEmail);
    }

    private UserEntity getUserByEmail(String userEmail) {
        return userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "User not found"));
    }

}
