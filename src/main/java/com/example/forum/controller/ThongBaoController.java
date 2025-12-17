package com.example.forum.controller;

import com.example.forum.model.NguoiDung;
import com.example.forum.model.ThongBao;
import com.example.forum.service.NguoiDungService;
import com.example.forum.service.ThongBaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/thong-bao")
@RequiredArgsConstructor
public class ThongBaoController {

    private final ThongBaoService thongBaoService;
    private final NguoiDungService nguoiDungService;

    @GetMapping("/dem")
    public ResponseEntity<Map<String, Object>> demChuaDoc(Authentication auth) {
        Map<String, Object> res = new HashMap<>();
        
        if (auth == null) {
            res.put("count", 0);
            return ResponseEntity.ok(res);
        }
        
        NguoiDung nd = nguoiDungService.timTheoTenDangNhap(auth.getName()).orElse(null);
        if (nd == null) {
            res.put("count", 0);
            return ResponseEntity.ok(res);
        }
        
        res.put("count", thongBaoService.demChuaDoc(nd.getManguoidung()));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/danh-sach")
    public ResponseEntity<List<ThongBao>> danhSach(Authentication auth) {
        if (auth == null) {
            return ResponseEntity.ok(List.of());
        }
        
        NguoiDung nd = nguoiDungService.timTheoTenDangNhap(auth.getName()).orElse(null);
        if (nd == null) {
            return ResponseEntity.ok(List.of());
        }
        
        return ResponseEntity.ok(thongBaoService.layThongBao(nd.getManguoidung()));
    }

    @PostMapping("/doc/{id}")
    public ResponseEntity<Map<String, Object>> danhDauDoc(@PathVariable String id) {
        thongBaoService.danhDauDaDoc(id);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/doc-tat-ca")
    public ResponseEntity<Map<String, Object>> docTatCa(Authentication auth) {
        Map<String, Object> res = new HashMap<>();
        
        if (auth == null) {
            res.put("success", false);
            return ResponseEntity.ok(res);
        }
        
        NguoiDung nd = nguoiDungService.timTheoTenDangNhap(auth.getName()).orElse(null);
        if (nd != null) {
            thongBaoService.danhDauTatCaDaDoc(nd.getManguoidung());
        }
        
        res.put("success", true);
        return ResponseEntity.ok(res);
    }
}
