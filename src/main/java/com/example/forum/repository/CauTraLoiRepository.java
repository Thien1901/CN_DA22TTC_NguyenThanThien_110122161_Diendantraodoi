package com.example.forum.repository;

import com.example.forum.model.CauTraLoi;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CauTraLoiRepository extends MongoRepository<CauTraLoi, String> {
    List<CauTraLoi> findByMacauhoiOrderByNgaytraloi(String macauhoi);
    List<CauTraLoi> findByManguoidungOrderByNgaytraloiDesc(String manguoidung);
    long countByMacauhoi(String macauhoi);
    long countByManguoidung(String manguoidung);
    void deleteByMacauhoi(String macauhoi);
}
