package com.example.forum.config;

import com.example.forum.service.BaoCaoService;
import com.example.forum.service.CauHoiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = "com.example.forum.controller")
@RequiredArgsConstructor
public class AdminModelAdvice {
    
    private final CauHoiService cauHoiService;
    private final BaoCaoService baoCaoService;
    
    @ModelAttribute("soCauHoiChoDuyet")
    public long soCauHoiChoDuyet() {
        return cauHoiService.demChoDuyet();
    }
    
    @ModelAttribute("soBaoCaoChoXuLy")
    public long soBaoCaoChoXuLy() {
        return baoCaoService.demBaoCaoChoXuLy();
    }
}
