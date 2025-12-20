package com.example.forum.controller;

import com.example.forum.model.NguoiDung;
import com.example.forum.service.CauHoiService;
import com.example.forum.service.CauTraLoiService;
import com.example.forum.service.NguoiDungService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.oauth2.core.user.OAuth2User;

@Controller
@RequestMapping("/ho-so")
@RequiredArgsConstructor
public class HoSoController {
    
    private final NguoiDungService nguoiDungService;
    private final CauHoiService cauHoiService;
    private final CauTraLoiService cauTraLoiService;
    private final PasswordEncoder passwordEncoder;
    
    // Helper method để lấy username từ cả đăng nhập thường và OAuth2
    private String getUsername(Authentication authentication) {
        if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            // Lấy username từ attribute đã set trong CustomOAuth2UserService
            String username = oauth2User.getAttribute("username");
            if (username != null) {
                return username;
            }
            // Fallback: tìm theo email
            String email = oauth2User.getAttribute("email");
            return nguoiDungService.timTheoEmail(email)
                    .map(NguoiDung::getTendangnhap)
                    .orElse(email);
        }
        return authentication.getName();
    }
    
    @GetMapping
    public String hoSoCaNhan(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/dang-nhap";
        }
        return "redirect:/ho-so/" + getUsername(authentication);
    }
    
    @GetMapping("/{username}")
    public String xemHoSo(@PathVariable String username, Model model) {
        Optional<NguoiDung> nguoiDungOpt = nguoiDungService.timTheoTenDangNhap(username);
        
        if (nguoiDungOpt.isEmpty()) {
            return "redirect:/";
        }
        
        NguoiDung nguoiDung = nguoiDungOpt.get();
        model.addAttribute("nguoiDung", nguoiDung);
        model.addAttribute("cauHois", cauHoiService.timTheomanguoidung(nguoiDung.getManguoidung()));
        model.addAttribute("cauTraLois", cauTraLoiService.timTheoNguoiDung(nguoiDung.getManguoidung()));
        model.addAttribute("tongCauHoi", cauHoiService.demTheomanguoidung(nguoiDung.getManguoidung()));
        model.addAttribute("tongTraLoi", cauTraLoiService.demTheoNguoiDung(nguoiDung.getManguoidung()));
        
        return "ho-so/xem";
    }
    
    @GetMapping("/chinh-sua")
    public String formChinhSua(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/dang-nhap";
        }
        
        String username = getUsername(authentication);
        Optional<NguoiDung> nguoiDungOpt = nguoiDungService.timTheoTenDangNhap(username);
        if (nguoiDungOpt.isEmpty()) {
            return "redirect:/";
        }
        
        model.addAttribute("nguoiDung", nguoiDungOpt.get());
        return "ho-so/chinh-sua";
    }
    
    @PostMapping("/chinh-sua")
    public String luuChinhSua(
            Authentication authentication,
            @RequestParam String hoten,
            @RequestParam String email,
            @RequestParam(required = false) MultipartFile fileAnhDaiDien,
            @RequestParam(required = false) String gioithieu,
            @RequestParam(required = false) String matkhaucu,
            @RequestParam(required = false) String matkhaumoi,
            @RequestParam(required = false) String xacnhanmatkhau,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (authentication == null) {
            return "redirect:/dang-nhap";
        }
        
        String username = getUsername(authentication);
        Optional<NguoiDung> nguoiDungOpt = nguoiDungService.timTheoTenDangNhap(username);
        if (nguoiDungOpt.isEmpty()) {
            return "redirect:/";
        }
        
        NguoiDung nguoiDung = nguoiDungOpt.get();
        
        // Kiểm tra email đã tồn tại với user khác
        Optional<NguoiDung> emailCheck = nguoiDungService.timTheoEmail(email);
        if (emailCheck.isPresent() && !emailCheck.get().getManguoidung().equals(nguoiDung.getManguoidung())) {
            model.addAttribute("error", "Email đã được sử dụng bởi tài khoản khác!");
            model.addAttribute("nguoiDung", nguoiDung);
            return "ho-so/chinh-sua";
        }
        
        // Cập nhật thông tin
        nguoiDung.setHoten(hoten);
        nguoiDung.setEmail(email);
        nguoiDung.setGioithieu(gioithieu);
        nguoiDung.setLanhoatdongcuoi(LocalDateTime.now());
        
        // Upload ảnh đại diện nếu có
        if (fileAnhDaiDien != null && !fileAnhDaiDien.isEmpty()) {
            try {
                String uploadDir = "uploads/avatars";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                
                String originalFilename = fileAnhDaiDien.getOriginalFilename();
                String extension = originalFilename != null && originalFilename.contains(".") 
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                    : ".jpg";
                String newFilename = UUID.randomUUID().toString() + extension;
                
                Path filePath = uploadPath.resolve(newFilename);
                Files.copy(fileAnhDaiDien.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                
                nguoiDung.setAnhdaidien("/uploads/avatars/" + newFilename);
            } catch (IOException e) {
                model.addAttribute("error", "Lỗi khi upload ảnh đại diện!");
                model.addAttribute("nguoiDung", nguoiDung);
                return "ho-so/chinh-sua";
            }
        }
        
        // Đổi mật khẩu nếu có
        if (matkhaumoi != null && !matkhaumoi.isEmpty()) {
            // Kiểm tra mật khẩu hiện tại
            if (matkhaucu == null || matkhaucu.isEmpty()) {
                model.addAttribute("error", "Vui lòng nhập mật khẩu hiện tại!");
                model.addAttribute("nguoiDung", nguoiDung);
                return "ho-so/chinh-sua";
            }
            if (!passwordEncoder.matches(matkhaucu, nguoiDung.getMatkhauhash())) {
                model.addAttribute("error", "Mật khẩu hiện tại không đúng!");
                model.addAttribute("nguoiDung", nguoiDung);
                return "ho-so/chinh-sua";
            }
            if (!matkhaumoi.equals(xacnhanmatkhau)) {
                model.addAttribute("error", "Mật khẩu xác nhận không khớp!");
                model.addAttribute("nguoiDung", nguoiDung);
                return "ho-so/chinh-sua";
            }
            if (matkhaumoi.length() < 6) {
                model.addAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự!");
                model.addAttribute("nguoiDung", nguoiDung);
                return "ho-so/chinh-sua";
            }
            nguoiDung.setMatkhauhash(passwordEncoder.encode(matkhaumoi));
        }
        
        nguoiDungService.luu(nguoiDung);
        redirectAttributes.addFlashAttribute("success", "Cập nhật hồ sơ thành công!");
        
        return "redirect:/ho-so/" + nguoiDung.getTendangnhap();
    }
}
