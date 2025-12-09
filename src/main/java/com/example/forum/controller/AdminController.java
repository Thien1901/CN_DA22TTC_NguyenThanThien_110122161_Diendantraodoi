package com.example.forum.controller;

import com.example.forum.model.ChuDeEntity;
import com.example.forum.model.NguoiDung;
import com.example.forum.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final NguoiDungService nguoiDungService;
    private final CauHoiService cauHoiService;
    private final CauTraLoiService cauTraLoiService;
    private final ChuDeService chuDeService;
    
    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("tongNguoiDung", nguoiDungService.dem());
        model.addAttribute("cauHoiChoDuyet", cauHoiService.demChoDuyet());
        model.addAttribute("tongCauHoi", cauHoiService.dem());
        model.addAttribute("cauHoiMoiNhat", cauHoiService.layMoiNhat(10));
        return "admin/dashboard";
    }
    
    // =================== NGƯỜI DÙNG ===================
    @GetMapping("/nguoi-dung")
    public String danhSachNguoiDung(Model model) {
        model.addAttribute("nguoiDungs", nguoiDungService.layTatCa());
        return "admin/nguoi-dung/danh-sach";
    }
    
    @PostMapping("/nguoi-dung/{id}/khoa")
    public String khoaNguoiDung(@PathVariable String id, RedirectAttributes redirectAttributes) {
        Optional<NguoiDung> ndOpt = nguoiDungService.timTheoId(id);
        ndOpt.ifPresent(nd -> {
            nd.setTrangthai("bikhoa");
            nguoiDungService.luu(nd);
        });
        redirectAttributes.addFlashAttribute("success", "Đã khóa tài khoản!");
        return "redirect:/admin/nguoi-dung";
    }
    
    @PostMapping("/nguoi-dung/{id}/mo-khoa")
    public String moKhoaNguoiDung(@PathVariable String id, RedirectAttributes redirectAttributes) {
        Optional<NguoiDung> ndOpt = nguoiDungService.timTheoId(id);
        ndOpt.ifPresent(nd -> {
            nd.setTrangthai("hoatdong");
            nguoiDungService.luu(nd);
        });
        redirectAttributes.addFlashAttribute("success", "Đã mở khóa tài khoản!");
        return "redirect:/admin/nguoi-dung";
    }
    
    @PostMapping("/nguoi-dung/{id}/xoa")
    public String xoaNguoiDung(@PathVariable String id, RedirectAttributes redirectAttributes) {
        nguoiDungService.xoa(id);
        redirectAttributes.addFlashAttribute("success", "Đã xóa người dùng!");
        return "redirect:/admin/nguoi-dung";
    }
    
    // =================== CÂU HỎI ===================
    @GetMapping("/cau-hoi")
    public String danhSachCauHoi(Model model) {
        model.addAttribute("cauHois", cauHoiService.layTatCa());
        return "admin/cau-hoi/danh-sach";
    }
    
    @GetMapping("/cau-hoi/cho-duyet")
    public String cauHoiChoDuyet(Model model) {
        model.addAttribute("cauHois", cauHoiService.layCauHoiChoDuyet());
        return "admin/cau-hoi/cho-duyet";
    }
    
    @PostMapping("/cau-hoi/{id}/duyet")
    public String duyetCauHoi(@PathVariable String id, RedirectAttributes redirectAttributes) {
        cauHoiService.duyetCauHoi(id);
        redirectAttributes.addFlashAttribute("success", "Đã duyệt câu hỏi!");
        return "redirect:/admin/cau-hoi/cho-duyet";
    }
    
    @PostMapping("/cau-hoi/{id}/xoa")
    public String xoaCauHoi(@PathVariable String id, RedirectAttributes redirectAttributes) {
        // Xóa câu trả lời trước
        cauTraLoiService.xoaTheoCauHoi(id);
        cauHoiService.xoa(id);
        redirectAttributes.addFlashAttribute("success", "Đã xóa câu hỏi!");
        return "redirect:/admin/cau-hoi";
    }
    
    // =================== CHỦ ĐỀ ===================
    @GetMapping("/chu-de")
    public String danhSachChuDe(Model model) {
        model.addAttribute("chuDes", chuDeService.layTatCa());
        return "admin/chu-de/danh-sach";
    }
    
    @GetMapping("/chu-de/them")
    public String formThemChuDe(Model model) {
        model.addAttribute("chuDe", new ChuDeEntity());
        return "admin/chu-de/form";
    }
    
    @GetMapping("/chu-de/{id}/sua")
    public String formSuaChuDe(@PathVariable String id, Model model) {
        Optional<ChuDeEntity> chuDeOpt = chuDeService.timTheoId(id);
        if (chuDeOpt.isEmpty()) {
            return "redirect:/admin/chu-de";
        }
        model.addAttribute("chuDe", chuDeOpt.get());
        return "admin/chu-de/form";
    }
    
    @PostMapping("/chu-de/luu")
    public String luuChuDe(@ModelAttribute ChuDeEntity chuDe, RedirectAttributes redirectAttributes) {
        // Nếu là chủ đề mới, tạo mã chủ đề và thứ tự tự động
        if (chuDe.getId() == null || chuDe.getId().isEmpty()) {
            long count = chuDeService.dem();
            chuDe.setMachude(String.valueOf(count + 1));
            chuDe.setThutu((int) count + 1);
        }
        chuDeService.luu(chuDe);
        redirectAttributes.addFlashAttribute("success", "Đã lưu chủ đề thành công!");
        return "redirect:/admin/chu-de";
    }
    
    @PostMapping("/chu-de/{id}/xoa")
    public String xoaChuDe(@PathVariable String id, RedirectAttributes redirectAttributes) {
        chuDeService.xoa(id);
        redirectAttributes.addFlashAttribute("success", "Đã xóa chủ đề!");
        return "redirect:/admin/chu-de";
    }
}
