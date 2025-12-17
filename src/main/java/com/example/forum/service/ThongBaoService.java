package com.example.forum.service;

import com.example.forum.model.NguoiDung;
import com.example.forum.model.ThongBao;
import com.example.forum.repository.NguoiDungRepository;
import com.example.forum.repository.ThongBaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ThongBaoService {

    private final ThongBaoRepository thongBaoRepository;
    private final NguoiDungRepository nguoiDungRepository;

    public void guiThongBaoChoAdmin(String tieuDe, String noiDung, String loai, String duongDan) {
        List<NguoiDung> admins = nguoiDungRepository.findByVaitroMavaitro("ADMIN");
        for (NguoiDung admin : admins) {
            ThongBao tb = new ThongBao();
            tb.setMaNguoiNhan(admin.getManguoidung());
            tb.setTieuDe(tieuDe);
            tb.setNoiDung(noiDung);
            tb.setLoai(loai);
            tb.setDuongDan(duongDan);
            tb.setDaDoc(false);
            tb.setNgayTao(LocalDateTime.now());
            thongBaoRepository.save(tb);
        }
    }

    public List<ThongBao> layThongBao(String maNguoiDung) {
        return thongBaoRepository.findByMaNguoiNhanOrderByNgayTaoDesc(maNguoiDung);
    }

    public long demChuaDoc(String maNguoiDung) {
        return thongBaoRepository.countByMaNguoiNhanAndDaDoc(maNguoiDung, false);
    }

    public void danhDauDaDoc(String id) {
        thongBaoRepository.findById(id).ifPresent(tb -> {
            tb.setDaDoc(true);
            thongBaoRepository.save(tb);
        });
    }

    public void danhDauTatCaDaDoc(String maNguoiDung) {
        List<ThongBao> list = thongBaoRepository.findByMaNguoiNhanOrderByNgayTaoDesc(maNguoiDung);
        list.forEach(tb -> {
            if (!tb.getDadoc()) {
                tb.setDaDoc(true);
                thongBaoRepository.save(tb);
            }
        });
    }
    
    // Method cho CauHoiController gọi khi có reply
    public void taoThongBao(String maNguoiNhan, String tieuDe, String noiDung, String duongDan,
                            String loai, String maNguoiGui, String tenNguoiGui, String anhNguoiGui) {
        ThongBao tb = new ThongBao();
        tb.setMaNguoiNhan(maNguoiNhan);
        tb.setTieuDe(tieuDe);
        tb.setNoiDung(noiDung);
        tb.setDuongDan(duongDan);
        tb.setLoai(loai);
        tb.setDaDoc(false);
        tb.setNgayTao(LocalDateTime.now());
        tb.setTenNguoiGui(tenNguoiGui);
        tb.setAvatarNguoiGui(anhNguoiGui);
        thongBaoRepository.save(tb);
    }
}
