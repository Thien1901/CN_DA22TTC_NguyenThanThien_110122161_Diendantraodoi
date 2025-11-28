package com.example.forum.service;

import com.example.forum.model.CauTraLoi;
import com.example.forum.repository.CauTraLoiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CauTraLoiService {
    
    private final CauTraLoiRepository cauTraLoiRepository;
    
    public List<CauTraLoi> timTheoCauHoi(String macauhoi) {
        return cauTraLoiRepository.findByMacauhoiOrderByNgaytraloi(macauhoi);
    }
    
    public List<CauTraLoi> timTheoNguoiDung(String manguoidung) {
        return cauTraLoiRepository.findByManguoidungOrderByNgaytraloiDesc(manguoidung);
    }
    
    public Optional<CauTraLoi> timTheoId(String id) {
        return cauTraLoiRepository.findById(id);
    }
    
    public CauTraLoi luu(CauTraLoi cauTraLoi) {
        return cauTraLoiRepository.save(cauTraLoi);
    }
    
    public void xoa(String id) {
        cauTraLoiRepository.deleteById(id);
    }
    
    public void xoaTheoCauHoi(String macauhoi) {
        cauTraLoiRepository.deleteByMacauhoi(macauhoi);
    }
    
    public long demTheoCauHoi(String macauhoi) {
        return cauTraLoiRepository.countByMacauhoi(macauhoi);
    }
    
    public long demTheoNguoiDung(String manguoidung) {
        return cauTraLoiRepository.countByManguoidung(manguoidung);
    }
}
