package com.example.forum.service;

import com.example.forum.model.BaoCao;
import com.example.forum.repository.BaoCaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BaoCaoService {

    @Autowired
    private BaoCaoRepository baoCaoRepository;

    public BaoCao taoBaoCao(BaoCao baoCao) {
        baoCao.setNgayBaoCao(LocalDateTime.now());
        baoCao.setTrangThai("CHO_XU_LY");
        return baoCaoRepository.save(baoCao);
    }

    public List<BaoCao> layTatCaBaoCao() {
        return baoCaoRepository.findAllByOrderByNgayBaoCaoDesc();
    }

    public List<BaoCao> layBaoCaoTheoTrangThai(String trangThai) {
        return baoCaoRepository.findByTrangThaiOrderByNgayBaoCaoDesc(trangThai);
    }

    public List<BaoCao> layBaoCaoCuaNguoiDung(String maNguoiDung) {
        return baoCaoRepository.findByMaNguoiBaoCaoOrderByNgayBaoCaoDesc(maNguoiDung);
    }

    public Optional<BaoCao> layBaoCaoTheoId(String id) {
        return baoCaoRepository.findById(id);
    }

    public boolean daBaoCao(String maNguoiBaoCao, String maDoiTuong) {
        return baoCaoRepository.existsByMaNguoiBaoCaoAndMaDoiTuong(maNguoiBaoCao, maDoiTuong);
    }

    public BaoCao xuLyBaoCao(String id, String trangThai, String ghiChuXuLy) {
        Optional<BaoCao> optBaoCao = baoCaoRepository.findById(id);
        if (optBaoCao.isPresent()) {
            BaoCao baoCao = optBaoCao.get();
            baoCao.setTrangThai(trangThai);
            baoCao.setGhiChuAdmin(ghiChuXuLy);
            baoCao.setNgayXuLy(LocalDateTime.now());
            return baoCaoRepository.save(baoCao);
        }
        return null;
    }

    public void xoaBaoCao(String id) {
        baoCaoRepository.deleteById(id);
    }
}
