package com.example.forum.repository;

import com.example.forum.model.ThongBao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThongBaoRepository extends MongoRepository<ThongBao, String> {
    List<ThongBao> findByManguoidungOrderByNgaytaoDesc(String manguoidung);
    List<ThongBao> findTop10ByManguoidungOrderByNgaytaoDesc(String manguoidung);
    List<ThongBao> findByManguoidungAndDadocOrderByNgaytaoDesc(String manguoidung, boolean dadoc);
    long countByManguoidungAndDadoc(String manguoidung, boolean dadoc);
}
