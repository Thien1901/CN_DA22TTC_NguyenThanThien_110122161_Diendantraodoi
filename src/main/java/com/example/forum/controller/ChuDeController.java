package com.example.forum.controller;

import com.example.forum.model.CauHoi;
import com.example.forum.model.ChuDeEntity;
import com.example.forum.service.CauHoiService;
import com.example.forum.service.ChuDeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/chu-de")
@RequiredArgsConstructor
public class ChuDeController {
    
    private final ChuDeService chuDeService;
    private final CauHoiService cauHoiService;
    
    @GetMapping("/{machude}")
    public String xemTheoChuDe(
            @PathVariable String machude,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        
        Optional<ChuDeEntity> chuDeOpt = chuDeService.timTheoMa(machude);
        if (chuDeOpt.isEmpty()) {
            return "redirect:/";
        }
        
        ChuDeEntity chuDe = chuDeOpt.get();
        Page<CauHoi> cauHoiPage = cauHoiService.timTheoChuDe(machude, page, 10);
        
        model.addAttribute("chuDe", chuDe);
        model.addAttribute("cauHois", cauHoiPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", cauHoiPage.getTotalPages());
        model.addAttribute("totalItems", cauHoiPage.getTotalElements());
        
        return "chu-de/danh-sach";
    }
}
