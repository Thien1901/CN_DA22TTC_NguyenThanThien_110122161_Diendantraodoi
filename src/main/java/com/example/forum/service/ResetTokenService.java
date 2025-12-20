package com.example.forum.service;

import com.example.forum.model.ResetToken;
import com.example.forum.repository.ResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResetTokenService {

    private final ResetTokenRepository resetTokenRepository;

    public String taoToken(String email) {
        // Xóa token cũ nếu có
        resetTokenRepository.deleteByEmail(email);
        
        // Tạo token mới
        ResetToken resetToken = new ResetToken();
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setEmail(email);
        resetToken.setNgayTao(LocalDateTime.now());
        resetToken.setNgayHetHan(LocalDateTime.now().plusMinutes(30));
        resetToken.setDaSuDung(false);
        
        resetTokenRepository.save(resetToken);
        return resetToken.getToken();
    }

    public Optional<ResetToken> timTheoToken(String token) {
        return resetTokenRepository.findByToken(token);
    }

    public boolean xacThucToken(String token) {
        Optional<ResetToken> tokenOpt = resetTokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            return false;
        }
        
        ResetToken resetToken = tokenOpt.get();
        return !resetToken.isDaSuDung() && !resetToken.isHetHan();
    }

    public void danhDauDaSuDung(String token) {
        resetTokenRepository.findByToken(token).ifPresent(t -> {
            t.setDaSuDung(true);
            resetTokenRepository.save(t);
        });
    }

    public void xoaToken(String token) {
        resetTokenRepository.findByToken(token).ifPresent(resetTokenRepository::delete);
    }
}
