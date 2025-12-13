package com.example.forum.repository;

import com.example.forum.model.CauHoi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CauHoiRepository extends MongoRepository<CauHoi, String> {
    Page<CauHoi> findByDaduocduyet(boolean daduocduyet, Pageable pageable);
    List<CauHoi> findByDaduocduyet(boolean daduocduyet);
    Page<CauHoi> findByDaduocduyetOrderByNgaydangDesc(boolean daduocduyet, Pageable pageable);
    List<CauHoi> findTop10ByDaduocduyetOrderByNgaydangDesc(boolean daduocduyet);
    List<CauHoi> findTop10ByDaduocduyetOrderByLuotxemDesc(boolean daduocduyet);
    
    @Query("{'chuyennganh.machuyennganh': ?0, 'daduocduyet': true}")
    Page<CauHoi> findByChuyenNganh(String machuyennganh, Pageable pageable);
    
    @Query("{'chuyennganh.machuyennganh': ?0, 'daduocduyet': true}")
    long countByChuyenNganh(String machuyennganh);
    
    List<CauHoi> findByManguoidung(String manguoidung);
    long countByManguoidung(String manguoidung);
    
    @Query("{'tieude': {$regex: ?0, $options: 'i'}, 'daduocduyet': true}")
    Page<CauHoi> timKiem(String tukhoa, Pageable pageable);
    
    long countByDaduocduyet(boolean daduocduyet);
    
    // Tìm câu hỏi theo chủ đề
    @Query("{'chude.machude': ?0, 'daduocduyet': true}")
    Page<CauHoi> findByChuDe(String machude, Pageable pageable);
    
    @Query("{'chude.machude': ?0, 'daduocduyet': true}")
    long countByChuDe(String machude);
}
