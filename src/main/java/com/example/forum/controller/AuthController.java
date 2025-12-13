package com.example.forum.controller;

import com.example.forum.model.NguoiDung;
import com.example.forum.model.VaiTro;
import com.example.forum.service.NguoiDungService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AuthController {
    
    private final NguoiDungService nguoiDungService;
    private final PasswordEncoder passwordEncoder;
    
    @GetMapping("/dang-nhap")
    public String trangDangNhap(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
        }
        return "dang-nhap";
    }
    
    @GetMapping("/dang-ky")
    public String trangDangKy() {
        return "dang-ky";
    }
    
    @PostMapping("/dang-ky")
    public String dangKy(
            @RequestParam String tendangnhap,
            @RequestParam String email,
            @RequestParam String hoten,
            @RequestParam String matkhau,
            @RequestParam String xacnhanmatkhau,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        // Validate
        if (nguoiDungService.tonTaiTenDangNhap(tendangnhap)) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
            return "dang-ky";
        }
        
        if (nguoiDungService.tonTaiEmail(email)) {
            model.addAttribute("error", "Email đã được sử dụng!");
            return "dang-ky";
        }
        
        if (!matkhau.equals(xacnhanmatkhau)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp!");
            return "dang-ky";
        }
        
        if (matkhau.length() < 6) {
            model.addAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự!");
            return "dang-ky";
        }
        
        // Tạo người dùng mới
        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setTendangnhap(tendangnhap);
        nguoiDung.setEmail(email);
        nguoiDung.setHoten(hoten);
        nguoiDung.setMatkhauhash(passwordEncoder.encode(matkhau));
        nguoiDung.setTrangthai("hoatdong");
        nguoiDung.setNgaytao(LocalDateTime.now());
        nguoiDung.setVaitro(new VaiTro("THANHVIEN", "Thành viên"));
        
        nguoiDungService.luu(nguoiDung);
        redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
        
        return "redirect:/dang-nhap";
    }
    
    @GetMapping("/quen-mat-khau")
    public String trangQuenMatKhau() {
        return "quen-mat-khau";
    }
    
    @PostMapping("/quen-mat-khau")
    public String xuLyQuenMatKhau(@RequestParam String email, Model model, RedirectAttributes redirectAttributes) {
        // Kiểm tra email có tồn tại không
        var nguoiDung = nguoiDungService.timTheoEmail(email);
        
        if (nguoiDung.isEmpty()) {
            model.addAttribute("error", "Email không tồn tại trong hệ thống!");
            return "quen-mat-khau";
        }
        
        // TODO: Gửi email đặt lại mật khẩu
        // Trong thực tế, cần tích hợp dịch vụ gửi email và tạo token reset
        
        // Hiển thị thông báo thành công
        model.addAttribute("success", true);
        return "quen-mat-khau";
    }
}
