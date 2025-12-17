package com.example.forum.repository;

import com.example.forum.model.ThongBao;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ThongBaoRepository extends MongoRepository<ThongBao, String> {
    List<ThongBao> findByMaNguoiNhanOrderByNgayTaoDesc(String maNguoiNhan);
    long countByMaNguoiNhanAndDaDoc(String maNguoiNhan, boolean daDoc);
}
