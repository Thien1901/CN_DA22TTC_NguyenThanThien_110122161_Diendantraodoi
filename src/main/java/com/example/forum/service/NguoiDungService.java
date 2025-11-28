package com.example.forum.service;

import com.example.forum.model.NguoiDung;
import com.example.forum.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NguoiDungService {
    
    private final NguoiDungRepository nguoiDungRepository;
    
    public List<NguoiDung> layTatCa() {
        return nguoiDungRepository.findAll();
    }
    
    public Optional<NguoiDung> timTheoId(String id) {
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
    
    public NguoiDung luu(NguoiDung nguoiDung) {
        return nguoiDungRepository.save(nguoiDung);
    }
    
    public void xoa(String id) {
        nguoiDungRepository.deleteById(id);
    }
    
    public long dem() {
        return nguoiDungRepository.count();
    }
}
