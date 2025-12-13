package com.example.forum.service;

import com.example.forum.model.ThongBao;
import com.example.forum.repository.ThongBaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ThongBaoService {
    
    private final ThongBaoRepository thongBaoRepository;
    
    public List<ThongBao> layThongBao(String manguoidung) {
        return thongBaoRepository.findTop10ByManguoidungOrderByNgaytaoDesc(manguoidung);
    }
    
    public List<ThongBao> layTatCaThongBao(String manguoidung) {
        return thongBaoRepository.findByManguoidungOrderByNgaytaoDesc(manguoidung);
    }
    
    public long demChuaDoc(String manguoidung) {
        return thongBaoRepository.countByManguoidungAndDadoc(manguoidung, false);
    }
    
    public ThongBao taoThongBao(String manguoidung, String tieude, String noidung, 
                                 String link, String loai,
                                 String manguoigui, String tennguoigui, String avatarnguoigui) {
        ThongBao thongBao = new ThongBao();
        thongBao.setManguoidung(manguoidung);
        thongBao.setTieude(tieude);
        thongBao.setNoidung(noidung);
        thongBao.setLink(link);
        thongBao.setLoai(loai);
        thongBao.setDadoc(false);
        thongBao.setNgaytao(LocalDateTime.now());
        thongBao.setManguoigui(manguoigui);
        thongBao.setTennguoigui(tennguoigui);
        thongBao.setAvatarnguoigui(avatarnguoigui);
        return thongBaoRepository.save(thongBao);
    }
    
    public void danhDauDaDoc(@NonNull String id) {
        thongBaoRepository.findById(id).ifPresent(tb -> {
            tb.setDadoc(true);
            thongBaoRepository.save(tb);
        });
    }
    
    public void danhDauTatCaDaDoc(String manguoidung) {
        List<ThongBao> dsChuaDoc = thongBaoRepository.findByManguoidungAndDadocOrderByNgaytaoDesc(manguoidung, false);
        dsChuaDoc.forEach(tb -> {
            tb.setDadoc(true);
            thongBaoRepository.save(tb);
        });
    }
    
    public void xoa(@NonNull String id) {
        thongBaoRepository.deleteById(id);
    }
}
