package com.gridu.store.service;

import com.gridu.store.model.ConfirmationToken;
import java.util.Optional;

public interface ConfirmationTokenService {
    void saveConfirmationToken(ConfirmationToken token);

    public Optional<ConfirmationToken> getToken(String token);

    public int setConfirmedAt(String token);
}
