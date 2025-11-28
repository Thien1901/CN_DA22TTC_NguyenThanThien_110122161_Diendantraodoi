package com.example.forum.repository;

import com.example.forum.model.NguoiDung;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NguoiDungRepository extends MongoRepository<NguoiDung, String> {
    Optional<NguoiDung> findByTendangnhap(String tendangnhap);
    Optional<NguoiDung> findByEmail(String email);
    boolean existsByTendangnhap(String tendangnhap);
    boolean existsByEmail(String email);
}
