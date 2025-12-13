package com.example.forum.repository;

import com.example.forum.model.BaoCao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaoCaoRepository extends MongoRepository<BaoCao, String> {
    List<BaoCao> findByTrangThaiOrderByNgayBaoCaoDesc(String trangThai);
    List<BaoCao> findAllByOrderByNgayBaoCaoDesc();
    List<BaoCao> findByMaNguoiBaoCaoOrderByNgayBaoCaoDesc(String maNguoiBaoCao);
    boolean existsByMaNguoiBaoCaoAndMaDoiTuong(String maNguoiBaoCao, String maDoiTuong);
}
