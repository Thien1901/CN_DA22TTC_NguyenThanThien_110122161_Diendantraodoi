package com.example.forum.controller;

import com.example.forum.model.ChuDeEntity;
import com.example.forum.model.NguoiDung;
import com.example.forum.service.ChuDeService;
import com.example.forum.service.NguoiDungService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Optional;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {
    
    private final NguoiDungService nguoiDungService;
    private final ChuDeService chuDeService;
    
    @ModelAttribute("currentUser")
    public NguoiDung getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            Optional<NguoiDung> nguoiDungOpt = nguoiDungService.timTheoTenDangNhap(authentication.getName());
            return nguoiDungOpt.orElse(null);
        }
        return null;
    }
    
    @ModelAttribute("danhSachChuDeGlobal")
    public List<ChuDeEntity> getDanhSachChuDe() {
        return chuDeService.layTatCa();
    }
}
