package com.gridu.store.controller;

import com.gridu.store.dto.UserRegistrationRequestDto;
import com.gridu.store.service.UserService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;

    @PostMapping("/registration")
    public ResponseEntity registration(@Valid @RequestBody UserRegistrationRequestDto requestDto) {
        return ResponseEntity.ok(userService.register(requestDto));
    }

    @GetMapping("/registration/confirm")
    public ResponseEntity confirmation(@RequestParam String token) {
        return ResponseEntity.ok(userService.confirmation(token));
    }
}
