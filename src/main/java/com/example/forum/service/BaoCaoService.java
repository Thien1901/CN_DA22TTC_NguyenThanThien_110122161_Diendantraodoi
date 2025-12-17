package com.example.forum.service;

import com.example.forum.model.BaoCao;
import com.example.forum.repository.BaoCaoRepository;
import com.example.forum.repository.CauHoiRepository;
import com.example.forum.repository.CauTraLoiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BaoCaoService {

    @Autowired
    private BaoCaoRepository baoCaoRepository;
    
    @Autowired
    private CauHoiRepository cauHoiRepository;
    
    @Autowired
    private CauTraLoiRepository cauTraLoiRepository;

    public BaoCao taoBaoCao(BaoCao baoCao) {
        baoCao.setNgayBaoCao(LocalDateTime.now());
        baoCao.setTrangThai("CHO_XU_LY");
        
        // Lấy tiêu đề đối tượng bị báo cáo
        if ("CAUHOI".equals(baoCao.getLoai())) {
            cauHoiRepository.findById(baoCao.getMaDoiTuong()).ifPresent(ch -> {
                baoCao.setTieuDeDoiTuong(ch.getTieude());
            });
        } else if ("CAUTRALOI".equals(baoCao.getLoai())) {
            cauTraLoiRepository.findById(baoCao.getMaDoiTuong()).ifPresent(ctl -> {
                baoCao.setTieuDeDoiTuong(ctl.getNoidung() != null && ctl.getNoidung().length() > 50 
                    ? ctl.getNoidung().substring(0, 50) + "..." 
                    : ctl.getNoidung());
            });
        }
        
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
    
    public long demBaoCaoChoXuLy() {
        return baoCaoRepository.countByTrangThai("CHO_XU_LY");
    }
}
