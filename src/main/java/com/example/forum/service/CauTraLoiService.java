package com.example.forum.service;

import com.example.forum.model.CauTraLoi;
import com.example.forum.repository.CauTraLoiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CauTraLoiService {
    
    private final CauTraLoiRepository cauTraLoiRepository;
    
    public List<CauTraLoi> timTheoCauHoi(String macauhoi) {
        return cauTraLoiRepository.findByMacauhoiOrderByNgaytraloi(macauhoi);
    }
    
    /**
     * Lấy danh sách bình luận theo cấu trúc cây (nested)
     */
    public List<CauTraLoi> timTheoCauHoiDangCay(String macauhoi) {
        // Lấy tất cả bình luận của câu hỏi
        List<CauTraLoi> tatCaBinhLuan = cauTraLoiRepository.findByMacauhoiOrderByNgaytraloi(macauhoi);
        
        // Tạo map để truy cập nhanh theo ID
        Map<String, CauTraLoi> binhLuanMap = tatCaBinhLuan.stream()
                .collect(Collectors.toMap(CauTraLoi::getMacautraloi, bl -> bl));
        
        // Khởi tạo danh sách con cho mỗi bình luận
        tatCaBinhLuan.forEach(bl -> bl.setCacTraLoiCon(new ArrayList<>()));
        
        // Danh sách bình luận gốc (không có cha)
        List<CauTraLoi> binhLuanGoc = new ArrayList<>();
        
        // Phân loại: gốc hoặc con
        for (CauTraLoi bl : tatCaBinhLuan) {
            if (bl.getMacautraloicha() == null || bl.getMacautraloicha().isEmpty()) {
                // Bình luận gốc
                binhLuanGoc.add(bl);
            } else {
                // Bình luận con - thêm vào danh sách con của cha
                CauTraLoi cha = binhLuanMap.get(bl.getMacautraloicha());
                if (cha != null) {
                    cha.getCacTraLoiCon().add(bl);
                } else {
                    // Nếu không tìm thấy cha, coi như bình luận gốc
                    binhLuanGoc.add(bl);
                }
            }
        }
        
        return binhLuanGoc;
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
        // Xóa tất cả bình luận con trước
        List<CauTraLoi> cacTraLoiCon = cauTraLoiRepository.findByMacautraloichaOrderByNgaytraloi(id);
        for (CauTraLoi con : cacTraLoiCon) {
            xoa(con.getMacautraloi()); // Đệ quy xóa con của con
        }
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
    
    /**
     * Tìm các bình luận con của một bình luận
     */
    public List<CauTraLoi> timCacTraLoiCon(String macautraloicha) {
        return cauTraLoiRepository.findByMacautraloichaOrderByNgaytraloi(macautraloicha);
    }
}
