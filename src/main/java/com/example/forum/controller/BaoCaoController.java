package com.example.forum.controller;

import com.example.forum.model.BaoCao;
import com.example.forum.model.NguoiDung;
import com.example.forum.model.VaiTro;
import com.example.forum.service.BaoCaoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/bao-cao")
public class BaoCaoController {

    @Autowired
    private BaoCaoService baoCaoService;

    // API tạo báo cáo (AJAX)
    @PostMapping("/gui")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> guiBaoCao(
            @RequestBody BaoCao baoCao,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        NguoiDung nguoiDung = (NguoiDung) session.getAttribute("nguoiDung");
        
        if (nguoiDung == null) {
            response.put("success", false);
            response.put("message", "Vui lòng đăng nhập để báo cáo");
            return ResponseEntity.badRequest().body(response);
        }

        // Kiểm tra đã báo cáo chưa
        if (baoCaoService.daBaoCao(nguoiDung.getManguoidung(), baoCao.getMaDoiTuong())) {
            response.put("success", false);
            response.put("message", "Bạn đã báo cáo nội dung này rồi");
            return ResponseEntity.badRequest().body(response);
        }

        baoCao.setMaNguoiBaoCao(nguoiDung.getManguoidung());
        baoCao.setTenNguoiBaoCao(nguoiDung.getHoten());
        baoCaoService.taoBaoCao(baoCao);

        response.put("success", true);
        response.put("message", "Báo cáo đã được gửi thành công");
        return ResponseEntity.ok(response);
    }


    // Trang quản lý báo cáo (Admin)
    @GetMapping("/quan-ly")
    public String quanLyBaoCao(
            @RequestParam(required = false) String trangThai,
            Model model,
            HttpSession session) {
        
        NguoiDung nguoiDung = (NguoiDung) session.getAttribute("nguoiDung");
        if (nguoiDung == null || nguoiDung.getVaitro() != VaiTro.ADMIN) {
            return "redirect:/dang-nhap";
        }

        List<BaoCao> danhSachBaoCao;
        if (trangThai != null && !trangThai.isEmpty()) {
            danhSachBaoCao = baoCaoService.layBaoCaoTheoTrangThai(trangThai);
        } else {
            danhSachBaoCao = baoCaoService.layTatCaBaoCao();
        }

        model.addAttribute("danhSachBaoCao", danhSachBaoCao);
        model.addAttribute("trangThaiFilter", trangThai);
        return "admin/quan-ly-bao-cao";
    }

    // Xử lý báo cáo (Admin)
    @PostMapping("/xu-ly/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> xuLyBaoCao(
            @PathVariable String id,
            @RequestParam String trangThai,
            @RequestParam(required = false) String ghiChuXuLy,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        NguoiDung nguoiDung = (NguoiDung) session.getAttribute("nguoiDung");
        
        if (nguoiDung == null || nguoiDung.getVaitro() != VaiTro.ADMIN) {
            response.put("success", false);
            response.put("message", "Không có quyền thực hiện");
            return ResponseEntity.status(403).body(response);
        }

        BaoCao baoCao = baoCaoService.xuLyBaoCao(id, trangThai, ghiChuXuLy);
        if (baoCao != null) {
            response.put("success", true);
            response.put("message", "Đã xử lý báo cáo thành công");
        } else {
            response.put("success", false);
            response.put("message", "Không tìm thấy báo cáo");
        }
        return ResponseEntity.ok(response);
    }

    // Xóa báo cáo (Admin)
    @DeleteMapping("/xoa/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> xoaBaoCao(
            @PathVariable String id,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        NguoiDung nguoiDung = (NguoiDung) session.getAttribute("nguoiDung");
        
        if (nguoiDung == null || nguoiDung.getVaitro() != VaiTro.ADMIN) {
            response.put("success", false);
            response.put("message", "Không có quyền thực hiện");
            return ResponseEntity.status(403).body(response);
        }

        baoCaoService.xoaBaoCao(id);
        response.put("success", true);
        response.put("message", "Đã xóa báo cáo");
        return ResponseEntity.ok(response);
    }
}
