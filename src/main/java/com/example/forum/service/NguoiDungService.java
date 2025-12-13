package com.example.forum.service;

import com.example.forum.model.NguoiDung;
import com.example.forum.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class NguoiDungService {
    
    private final NguoiDungRepository nguoiDungRepository;
    
    public List<NguoiDung> layTatCa() {
        return nguoiDungRepository.findAll();
    }
    
    public Optional<NguoiDung> timTheoId(@NonNull String id) {
        return nguoiDungRepository.findById(id);
    }
    
    public Optional<NguoiDung> timTheoTenDangNhap(String tendangnhap) {
        return nguoiDungRepository.findByTendangnhap(tendangnhap);
    }
    
    public Optional<NguoiDung> timTheoEmail(String email) {
        return nguoiDungRepository.findByEmail(email);
    }
    
    public boolean tonTaiTenDangNhap(String tendangnhap) {
        return nguoiDungRepository.existsByTendangnhap(tendangnhap);
    }
    
    public boolean tonTaiEmail(String email) {
        return nguoiDungRepository.existsByEmail(email);
    }
    
    public NguoiDung luu(@NonNull NguoiDung nguoiDung) {
        return nguoiDungRepository.save(nguoiDung);
    }
    
    public void xoa(@NonNull String id) {
        nguoiDungRepository.deleteById(id);
    }
    
    public long dem() {
        return nguoiDungRepository.count();
    }
    
    // Lấy người dùng mới nhất
    public List<NguoiDung> layMoiNhat(int soLuong) {
        List<NguoiDung> all = nguoiDungRepository.findAll(Sort.by(Sort.Direction.DESC, "ngaytao"));
        return all.size() > soLuong ? all.subList(0, soLuong) : all;
    }
}
