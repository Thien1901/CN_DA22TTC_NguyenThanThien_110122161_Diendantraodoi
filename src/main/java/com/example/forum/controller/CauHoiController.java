package com.example.forum.controller;

import com.example.forum.model.*;
import com.example.forum.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/cau-hoi")
@RequiredArgsConstructor
public class CauHoiController {
    
    private final CauHoiService cauHoiService;
    private final CauTraLoiService cauTraLoiService;
    private final NguoiDungService nguoiDungService;
    private final ChuDeService chuDeService;
    
    @Value("${upload.path:uploads}")
    private String uploadPath;
    
    @GetMapping("/{id}")
    public String xemChiTiet(@PathVariable String id, Model model) {
        Optional<CauHoi> cauHoiOpt = cauHoiService.timTheoId(id);
        
        if (cauHoiOpt.isEmpty()) {
            return "redirect:/";
        }
        
        CauHoi cauHoi = cauHoiOpt.get();
        cauHoiService.tangLuotXem(id);
        
        model.addAttribute("cauHoi", cauHoi);
        model.addAttribute("cauTraLois", cauTraLoiService.timTheoCauHoi(id));
        
        return "cau-hoi/chi-tiet";
    }
    
    @GetMapping("/dang-moi")
    public String formDangMoi(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/dang-nhap";
        }
        model.addAttribute("danhSachChuDe", chuDeService.layTatCa());
        return "cau-hoi/dang-moi";
    }
    
    @PostMapping("/dang-moi")
    public String dangCauHoi(
            @RequestParam String tieude,
            @RequestParam String noidung,
            @RequestParam(required = false) List<MultipartFile> files,
            @RequestParam String machude,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        if (authentication == null) {
            return "redirect:/dang-nhap";
        }
        
        Optional<NguoiDung> nguoiDungOpt = nguoiDungService.timTheoTenDangNhap(authentication.getName());
        if (nguoiDungOpt.isEmpty()) {
            return "redirect:/dang-nhap";
        }
        
        NguoiDung nguoiDung = nguoiDungOpt.get();
        
        // Lấy thông tin chủ đề từ database
        Optional<ChuDeEntity> chuDeOpt = chuDeService.timTheoMa(machude);
        if (chuDeOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Chủ đề không hợp lệ!");
            return "redirect:/cau-hoi/dang-moi";
        }
        ChuDeEntity chuDeEntity = chuDeOpt.get();
        
        CauHoi cauHoi = new CauHoi();
        cauHoi.setTieude(tieude);
        cauHoi.setNoidung(noidung);
        cauHoi.setManguoidung(nguoiDung.getManguoidung());
        cauHoi.setTennguoidung(nguoiDung.getHoten());
        cauHoi.setNgaydang(LocalDateTime.now());
        cauHoi.setNgaycapnhat(LocalDateTime.now());
        cauHoi.setDaduocduyet(false);
        
        // Xử lý upload file
        List<String> dinhkemList = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            try {
                Path uploadDir = Paths.get(uploadPath);
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }
                
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        String originalFilename = file.getOriginalFilename();
                        String extension = "";
                        if (originalFilename != null && originalFilename.contains(".")) {
                            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                        }
                        String newFilename = UUID.randomUUID().toString() + extension;
                        Path filePath = uploadDir.resolve(newFilename);
                        Files.copy(file.getInputStream(), filePath);
                        dinhkemList.add("/uploads/" + newFilename);
                    }
                }
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Lỗi khi upload file: " + e.getMessage());
                return "redirect:/cau-hoi/dang-moi";
            }
        }
        
        if (!dinhkemList.isEmpty()) {
            cauHoi.setDinhkem(dinhkemList);
        }
        
        // Thêm chủ đề từ database
        cauHoi.setChude(new ChuDe(chuDeEntity.getMachude(), chuDeEntity.getTenchude()));
        
        cauHoiService.luu(cauHoi);
        redirectAttributes.addFlashAttribute("success", "Câu hỏi đã được đăng và đang chờ duyệt!");
        
        return "redirect:/";
    }
    
    @GetMapping("/{id}/sua")
    public String formSua(@PathVariable String id, Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/dang-nhap";
        }
        
        Optional<CauHoi> cauHoiOpt = cauHoiService.timTheoId(id);
        if (cauHoiOpt.isEmpty()) {
            return "redirect:/";
        }
        
        CauHoi cauHoi = cauHoiOpt.get();
        Optional<NguoiDung> nguoiDungOpt = nguoiDungService.timTheoTenDangNhap(authentication.getName());
        
        if (nguoiDungOpt.isEmpty()) {
            return "redirect:/cau-hoi/" + id;
        }
        
        NguoiDung nguoiDung = nguoiDungOpt.get();
        boolean isOwner = nguoiDung.getManguoidung().equals(cauHoi.getManguoidung());
        boolean isAdmin = "ADMIN".equals(nguoiDung.getVaitro().getMavaitro());
        
        // Chỉ người đăng hoặc admin mới được sửa
        if (!isOwner && !isAdmin) {
            return "redirect:/cau-hoi/" + id;
        }
        
        model.addAttribute("cauHoi", cauHoi);
        
        return "cau-hoi/sua";
    }
    
    @PostMapping("/{id}/sua")
    public String suaCauHoi(
            @PathVariable String id,
            @RequestParam String tieude,
            @RequestParam String noidung,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        if (authentication == null) {
            return "redirect:/dang-nhap";
        }
        
        Optional<CauHoi> cauHoiOpt = cauHoiService.timTheoId(id);
        if (cauHoiOpt.isEmpty()) {
            return "redirect:/";
        }
        
        CauHoi cauHoi = cauHoiOpt.get();
        cauHoi.setTieude(tieude);
        cauHoi.setNoidung(noidung);
        cauHoi.setNgaycapnhat(LocalDateTime.now());
        
        cauHoiService.luu(cauHoi);
        redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
        
        return "redirect:/cau-hoi/" + id;
    }
    
    @PostMapping("/{id}/xoa")
    public String xoaCauHoi(
            @PathVariable String id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        if (authentication == null) {
            return "redirect:/dang-nhap";
        }
        
        Optional<CauHoi> cauHoiOpt = cauHoiService.timTheoId(id);
        if (cauHoiOpt.isEmpty()) {
            return "redirect:/";
        }
        
        CauHoi cauHoi = cauHoiOpt.get();
        Optional<NguoiDung> nguoiDungOpt = nguoiDungService.timTheoTenDangNhap(authentication.getName());
        
        if (nguoiDungOpt.isEmpty()) {
            return "redirect:/";
        }
        
        NguoiDung nguoiDung = nguoiDungOpt.get();
        boolean isOwner = nguoiDung.getManguoidung().equals(cauHoi.getManguoidung());
        boolean isAdmin = "ADMIN".equals(nguoiDung.getVaitro().getMavaitro());
        
        // Chỉ người đăng hoặc admin mới được xóa
        if (!isOwner && !isAdmin) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền xóa câu hỏi này!");
            return "redirect:/cau-hoi/" + id;
        }
        
        // Xóa các câu trả lời liên quan
        cauTraLoiService.xoaTheoCauHoi(id);
        
        // Xóa câu hỏi
        cauHoiService.xoa(id);
        
        redirectAttributes.addFlashAttribute("success", "Đã xóa câu hỏi thành công!");
        return "redirect:/";
    }
    
    @PostMapping("/{id}/tra-loi")
    public String traLoi(
            @PathVariable String id,
            @RequestParam String noidung,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        if (authentication == null) {
            return "redirect:/dang-nhap";
        }
        
        Optional<CauHoi> cauHoiOpt = cauHoiService.timTheoId(id);
        if (cauHoiOpt.isEmpty()) {
            return "redirect:/";
        }
        
        CauHoi cauHoi = cauHoiOpt.get();
        
        Optional<NguoiDung> nguoiDungOpt = nguoiDungService.timTheoTenDangNhap(authentication.getName());
        if (nguoiDungOpt.isEmpty()) {
            return "redirect:/dang-nhap";
        }
        
        NguoiDung nguoiDung = nguoiDungOpt.get();
        
        CauTraLoi cauTraLoi = new CauTraLoi();
        cauTraLoi.setMacauhoi(cauHoi.getId()); // Lưu ID câu hỏi
        cauTraLoi.setManguoidung(nguoiDung.getManguoidung());
        cauTraLoi.setTennguoidung(nguoiDung.getHoten());
        cauTraLoi.setAnhdaidien(nguoiDung.getAnhdaidien());
        cauTraLoi.setNoidung(noidung);
        cauTraLoi.setNgaytraloi(LocalDateTime.now());
        
        cauTraLoiService.luu(cauTraLoi);
        
        // Cập nhật số bình luận
        long soBinhLuan = cauTraLoiService.demTheoCauHoi(id);
        cauHoiService.capNhatSoBinhLuan(id, (int) soBinhLuan);
        
        redirectAttributes.addFlashAttribute("success", "Đã gửi câu trả lời!");
        
        return "redirect:/cau-hoi/" + id;
    }
    
    @PostMapping("/{cauhoiId}/tra-loi/{id}/xoa")
    public String xoaTraLoi(
            @PathVariable String cauhoiId,
            @PathVariable String id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        if (authentication == null) {
            return "redirect:/dang-nhap";
        }
        
        Optional<CauTraLoi> cauTraLoiOpt = cauTraLoiService.timTheoId(id);
        if (cauTraLoiOpt.isPresent()) {
            Optional<NguoiDung> nguoiDungOpt = nguoiDungService.timTheoTenDangNhap(authentication.getName());
            if (nguoiDungOpt.isPresent()) {
                NguoiDung nd = nguoiDungOpt.get();
                CauTraLoi tl = cauTraLoiOpt.get();
                
                if (nd.getManguoidung().equals(tl.getManguoidung()) || "ADMIN".equals(nd.getVaitro().getMavaitro())) {
                    cauTraLoiService.xoa(id);
                    long soBinhLuan = cauTraLoiService.demTheoCauHoi(cauhoiId);
                    cauHoiService.capNhatSoBinhLuan(cauhoiId, (int) soBinhLuan);
                    redirectAttributes.addFlashAttribute("success", "Đã xóa câu trả lời!");
                }
            }
        }
        
        return "redirect:/cau-hoi/" + cauhoiId;
    }
}
