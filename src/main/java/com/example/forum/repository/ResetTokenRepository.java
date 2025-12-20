package com.example.forum.repository;

import com.example.forum.model.ResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetTokenRepository extends MongoRepository<ResetToken, String> {
    Optional<ResetToken> findByToken(String token);
    Optional<ResetToken> findByEmailAndDaSuDung(String email, boolean daSuDung);
    void deleteByEmail(String email);
}
