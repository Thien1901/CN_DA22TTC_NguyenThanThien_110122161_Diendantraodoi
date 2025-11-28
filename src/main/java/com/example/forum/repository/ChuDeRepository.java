package com.example.forum.repository;

import com.example.forum.model.ChuDeEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChuDeRepository extends MongoRepository<ChuDeEntity, String> {
    Optional<ChuDeEntity> findByMachude(String machude);
    List<ChuDeEntity> findAllByOrderByThutuAsc();
}
