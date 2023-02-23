package com.gridu.store.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gridu.store.dto.request.UserLoginRequest;
import com.gridu.store.dto.request.UserRegistrationRequestDto;
import com.gridu.store.dto.response.LoginResponseDto;
import com.gridu.store.model.UserEntity;
import com.gridu.store.model.UserRole;
import com.gridu.store.repository.UserRepo;
import com.gridu.store.secure.config.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void register_ifUserNotExist() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto("user@gmail.com", "password");
        String passwordEncode = "passwordEncode";
        UserEntity userEntity = new UserEntity(null, requestDto.getEmail(), passwordEncode, UserRole.USER, null);
        UserEntity userSave = new UserEntity(1L, requestDto.getEmail(), passwordEncode, UserRole.USER, null);

        when(userRepo.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn(passwordEncode);
        when(userRepo.save(userEntity)).thenReturn(userSave);

        authService.register(requestDto);
        verify(userRepo, times(1)).save(userEntity);
    }

    @Test
    void Register_ifUserExist() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto("user@gmail.com", "password");

        when(userRepo.existsByEmail(requestDto.getEmail())).thenReturn(true);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authService.register(requestDto));
        assertEquals("User with this email already exist", exception.getReason());
        assertEquals(HttpStatusCode.valueOf(409), exception.getStatusCode());
    }

    @Test
    void Register_ifTwoUserRegisterAtTheSameTime() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto("user@gmail.com", "password");

        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPassword");
        doThrow(DataIntegrityViolationException.class).when(userRepo).save(any());


        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authService.register(requestDto));
        assertEquals("User with this email already exist", exception.getReason());
        assertEquals(HttpStatusCode.valueOf(409), exception.getStatusCode());
    }

    @Test
    void login_correct() {
        UserLoginRequest userLoginRequest = new UserLoginRequest("user@gmail.com", "password");
        UserEntity user = new UserEntity(1L, userLoginRequest.getEmail(), "password", UserRole.USER, null);
        String token = "token";
        LoginResponseDto responseDto = new LoginResponseDto(token);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userLoginRequest.getEmail(), userLoginRequest.getPassword());
        when(authenticationManager.authenticate(authenticationToken)).thenReturn(any());
        when(userRepo.findByEmail(userLoginRequest.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn(token);

        LoginResponseDto result = authService.login(userLoginRequest);
        assertEquals(responseDto, result);
    }

    @Test
    void login_incorrectPassword() {
        UserLoginRequest userLoginRequest = new UserLoginRequest("user@gmail.com", "password1");

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userLoginRequest.getEmail(), userLoginRequest.getPassword());
        when(authenticationManager.authenticate(authenticationToken))
                .thenThrow(new BadCredentialsException("Message"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authService.login(userLoginRequest));
        assertEquals("Incorrect password", exception.getReason());
        assertEquals(HttpStatusCode.valueOf(401), exception.getStatusCode());
    }

    @Test
    void login_userNotFound() {
        UserLoginRequest userLoginRequest = new UserLoginRequest("user11@gmail.com", "password");

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userLoginRequest.getEmail(), userLoginRequest.getPassword());
        when(authenticationManager.authenticate(authenticationToken)).thenReturn(any());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authService.login(userLoginRequest));
        assertEquals("User not found", exception.getReason());
        assertEquals(HttpStatusCode.valueOf(404), exception.getStatusCode());
    }

    @Test
    void getUserEntityByToken() {
        String email = "user@gmail.com";
        UserEntity user = new UserEntity(1L, email, "passwordEncode", UserRole.USER, null);
        String token = "token";
        String authHeader = "Bearer " + token;

        when(jwtService.extractUsername(token)).thenReturn(email);
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        UserEntity result = authService.getUserEntityByToken(authHeader);
        assertEquals(user, result);
    }

    @Test
    void getUserEntityByToken_ifUserNotExist() {
        String email = "user@gmail.com";
        String token = "token";
        String authHeader = "Bearer " + token;

        when(jwtService.extractUsername(token)).thenReturn(email);
        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authService.getUserEntityByToken(authHeader));
        assertEquals("User not found", exception.getReason());
        assertEquals(HttpStatusCode.valueOf(404), exception.getStatusCode());
    }
}