package com.example.forum.controller;

import com.example.forum.model.ChuDeEntity;
import com.example.forum.model.NguoiDung;
import com.example.forum.service.ChuDeService;
import com.example.forum.service.NguoiDungService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalControllerAdvice {
    
    private final NguoiDungService nguoiDungService;
    private final ChuDeService chuDeService;
    
    @ModelAttribute("currentUser")
    public NguoiDung getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() 
                    && !"anonymousUser".equals(authentication.getPrincipal())) {
                
                // Kiểm tra nếu là OAuth2 user
                if (authentication.getPrincipal() instanceof OAuth2User) {
                    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                    String email = oAuth2User.getAttribute("email");
                    Optional<NguoiDung> nguoiDungOpt = nguoiDungService.timTheoEmail(email);
                    return nguoiDungOpt.orElse(null);
                }
                
                // Form login user
                Optional<NguoiDung> nguoiDungOpt = nguoiDungService.timTheoTenDangNhap(authentication.getName());
                return nguoiDungOpt.orElse(null);
            }
        } catch (Exception e) {
            log.error("Lỗi khi lấy currentUser: ", e);
        }
        return null;
    }
    
    @ModelAttribute("danhSachChuDeGlobal")
    public List<ChuDeEntity> getDanhSachChuDe() {
        try {
            return chuDeService.layTatCa();
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách chủ đề: ", e);
            return Collections.emptyList();
        }
    }
}
