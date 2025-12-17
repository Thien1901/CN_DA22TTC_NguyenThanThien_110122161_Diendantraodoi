package com.example.forum.controller;

import com.example.forum.model.NguoiDung;
import com.example.forum.model.ThongBao;
import com.example.forum.service.NguoiDungService;
import com.example.forum.service.ThongBaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/thong-bao")
@RequiredArgsConstructor
public class ThongBaoPageController {

    private final ThongBaoService thongBaoService;
    private final NguoiDungService nguoiDungService;

    @GetMapping
    public String trangThongBao(Authentication auth, Model model) {
        if (auth == null) {
            return "redirect:/dang-nhap";
        }
        
        NguoiDung nd = nguoiDungService.timTheoTenDangNhap(auth.getName()).orElse(null);
        if (nd == null) {
            return "redirect:/dang-nhap";
        }
        
        List<ThongBao> danhSach = thongBaoService.layThongBao(nd.getManguoidung());
        model.addAttribute("danhSachThongBao", danhSach);
        model.addAttribute("soLuongChuaDoc", thongBaoService.demChuaDoc(nd.getManguoidung()));
        
        return "thong-bao/danh-sach";
    }
}
