package com.example.forum.security;

import com.example.forum.model.NguoiDung;
import com.example.forum.model.VaiTro;
import com.example.forum.repository.NguoiDungRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final NguoiDungRepository nguoiDungRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();
        
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");
        
        log.info("OAuth2 Login thành công - Email: {}, Name: {}", email, name);
        
        // Kiểm tra user đã tồn tại chưa
        Optional<NguoiDung> existingUser = nguoiDungRepository.findByEmail(email);
        
        if (existingUser.isEmpty()) {
            // Tạo user mới từ Google account
            NguoiDung nguoiDung = new NguoiDung();
            nguoiDung.setEmail(email);
            nguoiDung.setHoten(name);
            nguoiDung.setAnhdaidien(picture);
            
            // Tạo tên đăng nhập từ email (phần trước @)
            String username = email.split("@")[0];
            // Đảm bảo username unique
            String baseUsername = username;
            int counter = 1;
            while (nguoiDungRepository.existsByTendangnhap(username)) {
                username = baseUsername + counter;
                counter++;
            }
            nguoiDung.setTendangnhap(username);
            
            // Không cần mật khẩu cho OAuth2 user (set random hash)
            nguoiDung.setMatkhauhash("OAUTH2_USER_NO_PASSWORD");
            nguoiDung.setTrangthai("hoatdong");
            nguoiDung.setNgaytao(LocalDateTime.now());
            nguoiDung.setLanhoatdongcuoi(LocalDateTime.now());
            nguoiDung.setVaitro(new VaiTro("THANHVIEN", "Thành viên"));
            
            nguoiDungRepository.save(nguoiDung);
            log.info("Tạo user mới từ Google: {}", username);
        } else {
            // Cập nhật thông tin user nếu cần
            NguoiDung nguoiDung = existingUser.get();
            
            // Kiểm tra tài khoản bị khóa
            if ("bikhoa".equals(nguoiDung.getTrangthai())) {
                response.sendRedirect("/dang-nhap?error=blocked");
                return;
            }
            
            // Cập nhật avatar từ Google nếu chưa có
            if (nguoiDung.getAnhdaidien() == null || nguoiDung.getAnhdaidien().isEmpty()) {
                nguoiDung.setAnhdaidien(picture);
            }
            nguoiDung.setLanhoatdongcuoi(LocalDateTime.now());
            nguoiDungRepository.save(nguoiDung);
            log.info("User đã tồn tại, cập nhật thông tin: {}", nguoiDung.getTendangnhap());
        }
        
        setDefaultTargetUrl("/");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
