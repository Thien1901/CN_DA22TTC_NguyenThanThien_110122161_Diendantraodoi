package com.example.forum.controller;

import com.example.forum.model.NguoiDung;
import com.example.forum.model.ThongBao;
import com.example.forum.service.NguoiDungService;
import com.example.forum.service.ThongBaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/thong-bao")
@RequiredArgsConstructor
public class ThongBaoController {
    
    private final ThongBaoService thongBaoService;
    private final NguoiDungService nguoiDungService;
    
    @GetMapping("/api/danh-sach")
    @ResponseBody
    public ResponseEntity<List<ThongBao>> layDanhSach(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.ok(List.of());
        }
        
        Optional<NguoiDung> nguoiDungOpt = nguoiDungService.timTheoTenDangNhap(authentication.getName());
        if (nguoiDungOpt.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        
        List<ThongBao> thongBaos = thongBaoService.layThongBao(nguoiDungOpt.get().getManguoidung());
        return ResponseEntity.ok(thongBaos);
    }
    
    @GetMapping("/api/dem-chua-doc")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> demChuaDoc(Authentication authentication) {
        Map<String, Long> result = new HashMap<>();
        
        if (authentication == null) {
            result.put("count", 0L);
            return ResponseEntity.ok(result);
        }
        
        Optional<NguoiDung> nguoiDungOpt = nguoiDungService.timTheoTenDangNhap(authentication.getName());
        if (nguoiDungOpt.isEmpty()) {
            result.put("count", 0L);
            return ResponseEntity.ok(result);
        }
        
        long count = thongBaoService.demChuaDoc(nguoiDungOpt.get().getManguoidung());
        result.put("count", count);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/api/doc/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> danhDauDaDoc(@PathVariable String id) {
        thongBaoService.danhDauDaDoc(id);
        Map<String, String> result = new HashMap<>();
        result.put("status", "success");
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/api/doc-tat-ca")
    @ResponseBody
    public ResponseEntity<Map<String, String>> danhDauTatCaDaDoc(Authentication authentication) {
        if (authentication != null) {
            Optional<NguoiDung> nguoiDungOpt = nguoiDungService.timTheoTenDangNhap(authentication.getName());
            nguoiDungOpt.ifPresent(nd -> thongBaoService.danhDauTatCaDaDoc(nd.getManguoidung()));
        }
        Map<String, String> result = new HashMap<>();
        result.put("status", "success");
        return ResponseEntity.ok(result);
    }
}
