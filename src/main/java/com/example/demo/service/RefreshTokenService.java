package com.example.demo.service;

import com.example.demo.domain.RefreshToken;
import com.example.demo.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken addRefreshToken(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void deleteRefreshToken(String refreshToken) {
        refreshTokenRepository.findByValue(refreshToken).ifPresent(refreshTokenRepository::delete);
    }

    @Transactional(readOnly = true)
    public Optional<RefreshToken> findRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByValue(refreshToken);
    }
}
