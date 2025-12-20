package com.example.forum.controller;

import com.example.forum.model.NguoiDung;
import com.example.forum.model.ResetToken;
import com.example.forum.model.VaiTro;
import com.example.forum.service.EmailService;
import com.example.forum.service.NguoiDungService;
import com.example.forum.service.ResetTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final NguoiDungService nguoiDungService;
    private final PasswordEncoder passwordEncoder;
    private final ResetTokenService resetTokenService;
    private final EmailService emailService;
    
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
    public String xuLyQuenMatKhau(@RequestParam String email, Model model) {
        // Kiểm tra email có tồn tại không
        Optional<NguoiDung> nguoiDungOpt = nguoiDungService.timTheoEmail(email);
        
        if (nguoiDungOpt.isEmpty()) {
            model.addAttribute("error", "Email không tồn tại trong hệ thống!");
            return "quen-mat-khau";
        }
        
        NguoiDung nguoiDung = nguoiDungOpt.get();
        
        // Kiểm tra nếu là tài khoản OAuth2 (không có mật khẩu)
        if ("OAUTH2_USER_NO_PASSWORD".equals(nguoiDung.getMatkhauhash())) {
            model.addAttribute("error", "Tài khoản này đăng nhập bằng Google. Vui lòng sử dụng đăng nhập Google!");
            return "quen-mat-khau";
        }
        
        // Tạo token reset
        String token = resetTokenService.taoToken(email);
        String resetLink = "http://localhost:8080/dat-lai-mat-khau?token=" + token;
        
        try {
            // Gửi email
            emailService.guiEmailDatLaiMatKhau(email, nguoiDung.getHoten(), token);
            model.addAttribute("success", true);
            log.info("Đã gửi email đặt lại mật khẩu cho: {}", email);
        } catch (Exception e) {
            log.error("Lỗi gửi email: {}", e.getMessage());
            // Vẫn hiển thị link để user có thể reset (trong trường hợp email không gửi được)
            model.addAttribute("success", true);
            model.addAttribute("resetLink", resetLink);
            model.addAttribute("emailError", true);
        }
        
        return "quen-mat-khau";
    }
    
    @GetMapping("/dat-lai-mat-khau")
    public String trangDatLaiMatKhau(@RequestParam String token, Model model) {
        // Kiểm tra token hợp lệ
        if (!resetTokenService.xacThucToken(token)) {
            model.addAttribute("error", "Link đặt lại mật khẩu không hợp lệ hoặc đã hết hạn!");
            model.addAttribute("tokenInvalid", true);
            return "dat-lai-mat-khau";
        }
        
        model.addAttribute("token", token);
        return "dat-lai-mat-khau";
    }
    
    @PostMapping("/dat-lai-mat-khau")
    public String xuLyDatLaiMatKhau(
            @RequestParam String token,
            @RequestParam String matkhau,
            @RequestParam String xacnhanmatkhau,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        // Kiểm tra token
        if (!resetTokenService.xacThucToken(token)) {
            model.addAttribute("error", "Link đặt lại mật khẩu không hợp lệ hoặc đã hết hạn!");
            model.addAttribute("tokenInvalid", true);
            return "dat-lai-mat-khau";
        }
        
        // Validate mật khẩu
        if (!matkhau.equals(xacnhanmatkhau)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp!");
            model.addAttribute("token", token);
            return "dat-lai-mat-khau";
        }
        
        if (matkhau.length() < 6) {
            model.addAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự!");
            model.addAttribute("token", token);
            return "dat-lai-mat-khau";
        }
        
        // Lấy email từ token
        Optional<ResetToken> resetTokenOpt = resetTokenService.timTheoToken(token);
        if (resetTokenOpt.isEmpty()) {
            model.addAttribute("error", "Token không hợp lệ!");
            model.addAttribute("tokenInvalid", true);
            return "dat-lai-mat-khau";
        }
        
        String email = resetTokenOpt.get().getEmail();
        Optional<NguoiDung> nguoiDungOpt = nguoiDungService.timTheoEmail(email);
        
        if (nguoiDungOpt.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy tài khoản!");
            model.addAttribute("tokenInvalid", true);
            return "dat-lai-mat-khau";
        }
        
        // Cập nhật mật khẩu mới
        NguoiDung nguoiDung = nguoiDungOpt.get();
        nguoiDung.setMatkhauhash(passwordEncoder.encode(matkhau));
        nguoiDungService.luu(nguoiDung);
        
        // Đánh dấu token đã sử dụng
        resetTokenService.danhDauDaSuDung(token);
        
        log.info("Đã đặt lại mật khẩu cho: {}", email);
        redirectAttributes.addFlashAttribute("success", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập.");
        
        return "redirect:/dang-nhap";
    }
}
