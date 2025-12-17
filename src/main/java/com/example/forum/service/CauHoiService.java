package com.example.forum.service;

import com.example.forum.model.CauHoi;
import com.example.forum.repository.CauHoiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class CauHoiService {
    
    private final CauHoiRepository cauHoiRepository;
    
    public List<CauHoi> layTatCa() {
        return cauHoiRepository.findAll(Sort.by(Sort.Direction.DESC, "ngaydang"));
    }
    
    public Page<CauHoi> layCauHoiDaDuyet(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "ngaydang"));
        return cauHoiRepository.findByDaduocduyet(true, pageable);
    }
    
    public List<CauHoi> layCauHoiChoDuyet() {
        List<CauHoi> cauHois = cauHoiRepository.findByDaduocduyet(false);
        // Lọc bỏ câu hỏi đã bị từ chối
        return cauHois.stream()
                .filter(ch -> ch.getTrangthai() == null || !ch.getTrangthai().equals("tuchoi"))
                .toList();
    }
    
    public List<CauHoi> layMoiNhat(int soLuong) {
        return cauHoiRepository.findTop10ByDaduocduyetOrderByNgaydangDesc(true);
    }
    
    public List<CauHoi> layXemNhieuNhat() {
        return cauHoiRepository.findTop10ByDaduocduyetOrderByLuotxemDesc(true);
    }
    
    public Optional<CauHoi> timTheoId(@NonNull String id) {
        return cauHoiRepository.findById(id);
    }
    
    public List<CauHoi> timTheomanguoidung(String manguoidung) {
        return cauHoiRepository.findByManguoidung(manguoidung);
    }
    
    public long demTheomanguoidung(String manguoidung) {
        return cauHoiRepository.countByManguoidung(manguoidung);
    }
    
    public Page<CauHoi> timKiem(String tukhoa, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "ngaydang"));
        return cauHoiRepository.timKiem(tukhoa, pageable);
    }
    
    public CauHoi luu(CauHoi cauHoi) {
        if (cauHoi.getId() != null) {
            cauHoi.setNgaycapnhat(LocalDateTime.now());
        }
        return cauHoiRepository.save(cauHoi);
    }
    
    public void tangLuotXem(@NonNull String id) {
        Optional<CauHoi> cauHoiOpt = cauHoiRepository.findById(id);
        cauHoiOpt.ifPresent(cauHoi -> {
            cauHoi.setLuotxem(cauHoi.getLuotxem() + 1);
            cauHoiRepository.save(cauHoi);
        });
    }
    
    public void capNhatSoBinhLuan(@NonNull String id, int soLuong) {
        Optional<CauHoi> cauHoiOpt = cauHoiRepository.findById(id);
        cauHoiOpt.ifPresent(cauHoi -> {
            cauHoi.setSoluongbinhluan(soLuong);
            cauHoiRepository.save(cauHoi);
        });
    }
    
    public void duyetCauHoi(@NonNull String id) {
        Optional<CauHoi> cauHoiOpt = cauHoiRepository.findById(id);
        cauHoiOpt.ifPresent(cauHoi -> {
            cauHoi.setDaduocduyet(true);
            cauHoiRepository.save(cauHoi);
        });
    }
    
    public void tuChoiCauHoi(@NonNull String id, String lyDoTuChoi) {
        Optional<CauHoi> cauHoiOpt = cauHoiRepository.findById(id);
        cauHoiOpt.ifPresent(cauHoi -> {
            cauHoi.setTrangthai("tuchoi");
            cauHoi.setLyDoTuChoi(lyDoTuChoi);
            cauHoiRepository.save(cauHoi);
        });
    }
    
    public List<CauHoi> layCauHoiDaTuChoi() {
        List<CauHoi> cauHois = cauHoiRepository.findAll(Sort.by(Sort.Direction.DESC, "ngaydang"));
        return cauHois.stream()
                .filter(ch -> "tuchoi".equals(ch.getTrangthai()))
                .toList();
    }
    
    public void xoa(@NonNull String id) {
        cauHoiRepository.deleteById(id);
    }
    
    public long demChoDuyet() {
        // Đếm câu hỏi chờ duyệt (không bao gồm câu hỏi đã từ chối)
        return layCauHoiChoDuyet().size();
    }
    
    public long dem() {
        return cauHoiRepository.count();
    }
    
    // Tìm câu hỏi theo chủ đề
    public Page<CauHoi> timTheoChuDe(String machude, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "ngaydang"));
        return cauHoiRepository.findByChuDe(machude, pageable);
    }
    
    public long demTheoChuDe(String machude) {
        return cauHoiRepository.countByChuDe(machude);
    }
    
    // Tính tổng lượt xem của tất cả câu hỏi
    public long tinhTongLuotXem() {
        List<CauHoi> allCauHoi = cauHoiRepository.findAll();
        return allCauHoi.stream().mapToLong(CauHoi::getLuotxem).sum();
    }
}
