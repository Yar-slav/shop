package com.gridu.store.service;

import com.gridu.store.model.ConfirmationToken;
import com.gridu.store.repository.ConfirmationTokenRepo;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

    private final ConfirmationTokenRepo confirmationTokenRepo;

    @Override
    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepo.save(token);
    }

    @Override
    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepo.findByToken(token);
    }

    @Override
    public int setConfirmedAt(String token) {
        return confirmationTokenRepo.updateConfirmedAt(token, LocalDateTime.now());
    }
}
