package com.example.forum.controller;

import com.example.forum.service.CauHoiService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import com.example.forum.model.CauHoi;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HomeController {
    
    private final CauHoiService cauHoiService;
    
    @GetMapping("/")
    public String trangChu(Model model, @RequestParam(defaultValue = "0") int page) {
        model.addAttribute("cauHois", cauHoiService.layCauHoiDaDuyet(page, 10));
        model.addAttribute("cauHoiMoiNhat", cauHoiService.layMoiNhat(5));
        model.addAttribute("cauHoiNoiBat", cauHoiService.layXemNhieuNhat());
        return "home";
    }
    
    @GetMapping("/tat-ca-cau-hoi")
    public String tatCaCauHoi(Model model, @RequestParam(defaultValue = "0") int page) {
        Page<CauHoi> cauHoiPage = cauHoiService.layCauHoiDaDuyet(page, 10);
        model.addAttribute("cauHois", cauHoiPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", cauHoiPage.getTotalPages());
        model.addAttribute("totalItems", cauHoiPage.getTotalElements());
        return "tat-ca-cau-hoi";
    }
    
    @GetMapping("/tim-kiem")
    public String timKiem(@RequestParam(required = false) String q, 
                         @RequestParam(defaultValue = "0") int page, 
                         Model model) {
        if (q != null && !q.trim().isEmpty()) {
            Page<CauHoi> cauHoiPage = cauHoiService.timKiem(q, page, 10);
            model.addAttribute("cauHois", cauHoiPage.getContent());
            model.addAttribute("keyword", q);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", cauHoiPage.getTotalPages());
            model.addAttribute("totalItems", cauHoiPage.getTotalElements());
        } else {
            model.addAttribute("keyword", "");
            model.addAttribute("totalItems", 0);
        }
        return "tim-kiem";
    }
}
