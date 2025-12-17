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
@SuppressWarnings("null")
public class AdminController {
    
    private final NguoiDungService nguoiDungService;
    private final CauHoiService cauHoiService;
    private final CauTraLoiService cauTraLoiService;
    private final ChuDeService chuDeService;
    private final BaoCaoService baoCaoService;
    
    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("tongNguoiDung", nguoiDungService.dem());
        model.addAttribute("cauHoiChoDuyet", cauHoiService.demChoDuyet());
        model.addAttribute("tongCauHoi", cauHoiService.dem());
        model.addAttribute("cauHoiMoiNhat", cauHoiService.layMoiNhat(10));
        model.addAttribute("tongLuotXem", cauHoiService.tinhTongLuotXem());
        
        // Lấy hoạt động gần đây (5 câu hỏi mới nhất)
        model.addAttribute("hoatDongGanDay", cauHoiService.layMoiNhat(5));
        
        // Lấy danh sách người dùng mới đăng ký
        model.addAttribute("nguoiDungMoi", nguoiDungService.layMoiNhat(3));
        
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
    
    @PostMapping("/cau-hoi/{id}/tu-choi")
    public String tuChoiCauHoi(@PathVariable String id, @RequestParam String lyDoTuChoi, RedirectAttributes redirectAttributes) {
        cauHoiService.tuChoiCauHoi(id, lyDoTuChoi);
        redirectAttributes.addFlashAttribute("success", "Đã từ chối câu hỏi!");
        return "redirect:/admin/cau-hoi/cho-duyet";
    }
    
    @GetMapping("/cau-hoi/da-tu-choi")
    public String cauHoiDaTuChoi(Model model) {
        model.addAttribute("cauHois", cauHoiService.layCauHoiDaTuChoi());
        return "admin/cau-hoi/da-tu-choi";
    }
    
    // =================== BÁO CÁO VI PHẠM ===================
    @GetMapping("/bao-cao")
    public String quanLyBaoCao(@RequestParam(required = false) String trangThai, Model model) {
        if (trangThai != null && !trangThai.isEmpty()) {
            model.addAttribute("danhSachBaoCao", baoCaoService.layBaoCaoTheoTrangThai(trangThai));
        } else {
            model.addAttribute("danhSachBaoCao", baoCaoService.layTatCaBaoCao());
        }
        model.addAttribute("trangThaiFilter", trangThai);
        return "admin/bao-cao";
    }
    
    @PostMapping("/bao-cao/{id}/xu-ly")
    public String xuLyBaoCao(@PathVariable String id, @RequestParam String trangThai, RedirectAttributes redirectAttributes) {
        baoCaoService.xuLyBaoCao(id, trangThai, null);
        String msg = "DA_XU_LY".equals(trangThai) ? "Đã xác nhận vi phạm!" : "Đã từ chối báo cáo!";
        redirectAttributes.addFlashAttribute("success", msg);
        return "redirect:/admin/bao-cao";
    }
    
    @PostMapping("/bao-cao/{id}/xoa")
    public String xoaBaoCao(@PathVariable String id, RedirectAttributes redirectAttributes) {
        baoCaoService.xoaBaoCao(id);
        redirectAttributes.addFlashAttribute("success", "Đã xóa báo cáo!");
        return "redirect:/admin/bao-cao";
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
    
    @GetMapping("/chu-de/{machude}/sua")
    public String formSuaChuDe(@PathVariable String machude, Model model) {
        Optional<ChuDeEntity> chuDeOpt = chuDeService.timTheoMa(machude);
        if (chuDeOpt.isEmpty()) {
            return "redirect:/admin/chu-de";
        }
        model.addAttribute("chuDe", chuDeOpt.get());
        return "admin/chu-de/form";
    }
    
    @PostMapping("/chu-de/luu")
    public String luuChuDe(@ModelAttribute ChuDeEntity chuDe, RedirectAttributes redirectAttributes) {
        // Nếu là chủ đề mới (id null hoặc empty)
        if (chuDe.getId() == null || chuDe.getId().isEmpty()) {
            chuDe.setId(null); // Đảm bảo MongoDB tự tạo ID mới
            // Tạo mã chủ đề unique dựa trên timestamp
            chuDe.setMachude("CD" + System.currentTimeMillis());
            // Thứ tự = số chủ đề hiện tại + 1
            chuDe.setThutu((int) chuDeService.dem() + 1);
        }
        chuDeService.luu(chuDe);
        redirectAttributes.addFlashAttribute("success", "Đã lưu chủ đề thành công!");
        return "redirect:/admin/chu-de";
    }
    
    @PostMapping("/chu-de/{machude}/xoa")
    public String xoaChuDe(@PathVariable String machude, RedirectAttributes redirectAttributes) {
        chuDeService.xoaTheoMa(machude);
        redirectAttributes.addFlashAttribute("success", "Đã xóa chủ đề!");
        return "redirect:/admin/chu-de";
    }
}
