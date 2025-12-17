package com.example.forum.controller;

import com.example.forum.model.BaoCao;
import com.example.forum.model.NguoiDung;
import com.example.forum.service.BaoCaoService;
import com.example.forum.service.NguoiDungService;
import com.example.forum.service.ThongBaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/bao-cao")
@RequiredArgsConstructor
public class BaoCaoController {

    private final BaoCaoService baoCaoService;
    private final ThongBaoService thongBaoService;
    private final NguoiDungService nguoiDungService;

    @PostMapping("/gui")
    public ResponseEntity<Map<String, Object>> guiBaoCao(
            @RequestBody Map<String, String> body,
            Authentication auth) {
        
        Map<String, Object> res = new HashMap<>();
        
        if (auth == null) {
            res.put("success", false);
            res.put("message", "Vui lòng đăng nhập");
            return ResponseEntity.badRequest().body(res);
        }
        
        NguoiDung nguoiDung = nguoiDungService.timTheoTenDangNhap(auth.getName()).orElse(null);
        if (nguoiDung == null) {
            res.put("success", false);
            res.put("message", "Không tìm thấy người dùng");
            return ResponseEntity.badRequest().body(res);
        }
        
        String loai = body.get("loai");
        String maDoiTuong = body.get("maDoiTuong");
        String lyDo = body.get("lyDo");
        String moTa = body.get("moTa");
        
        if (baoCaoService.daBaoCao(nguoiDung.getManguoidung(), maDoiTuong)) {
            res.put("success", false);
            res.put("message", "Bạn đã báo cáo nội dung này rồi");
            return ResponseEntity.badRequest().body(res);
        }
        
        BaoCao bc = new BaoCao();
        bc.setLoai(loai);
        bc.setMaDoiTuong(maDoiTuong);
        bc.setLyDo(lyDo);
        bc.setMoTa(moTa);
        bc.setMaNguoiBaoCao(nguoiDung.getManguoidung());
        bc.setTenNguoiBaoCao(nguoiDung.getHoten());
        baoCaoService.taoBaoCao(bc);
        
        String loaiText = "CAUHOI".equals(loai) ? "câu hỏi" : "câu trả lời";
        
        // Gửi thông báo cho admin
        thongBaoService.guiThongBaoChoAdmin(
            "Báo cáo mới: " + loaiText,
            nguoiDung.getHoten() + " báo cáo " + loaiText + " - Lý do: " + lyDo,
            "BAO_CAO_MOI",
            "/admin/bao-cao"
        );
        
        // Gửi thông báo cho người dùng đã báo cáo
        thongBaoService.taoThongBao(
            nguoiDung.getManguoidung(),
            "Báo cáo đã được gửi",
            "Báo cáo " + loaiText + " của bạn đã được gửi thành công. Admin sẽ xem xét và xử lý.",
            "/ho-so",
            "BAO_CAO_DA_GUI",
            null, null, null
        );
        
        res.put("success", true);
        res.put("message", "Đã gửi báo cáo thành công");
        return ResponseEntity.ok(res);
    }

    @PostMapping("/xu-ly/{id}")
    public ResponseEntity<Map<String, Object>> xuLy(
            @PathVariable String id,
            @RequestParam String trangThai,
            Authentication auth) {
        
        Map<String, Object> res = new HashMap<>();
        
        if (auth == null || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            res.put("success", false);
            res.put("message", "Không có quyền");
            return ResponseEntity.status(403).body(res);
        }
        
        baoCaoService.xuLyBaoCao(id, trangThai, null);
        res.put("success", true);
        res.put("message", "Đã xử lý");
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/xoa/{id}")
    public ResponseEntity<Map<String, Object>> xoa(@PathVariable String id, Authentication auth) {
        Map<String, Object> res = new HashMap<>();
        
        if (auth == null || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            res.put("success", false);
            return ResponseEntity.status(403).body(res);
        }
        
        baoCaoService.xoaBaoCao(id);
        res.put("success", true);
        return ResponseEntity.ok(res);
    }
}
