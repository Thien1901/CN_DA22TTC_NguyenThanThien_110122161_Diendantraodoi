package com.example.forum.controller;

import com.example.forum.model.*;
import com.example.forum.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/cau-hoi")
@RequiredArgsConstructor
@SuppressWarnings("null")
public class CauHoiController {
    
    private final CauHoiService cauHoiService;
    private final CauTraLoiService cauTraLoiService;
    private final NguoiDungService nguoiDungService;
    private final ChuDeService chuDeService;
    private final ThongBaoService thongBaoService;
    
    @Value("${upload.path:uploads}")
    private String uploadPath;
    
    @GetMapping("/{id}")
    public String xemChiTiet(@PathVariable String id, Model model, HttpSession session, Authentication authentication) {
        Optional<CauHoi> cauHoiOpt = cauHoiService.timTheoId(id);
        
        if (cauHoiOpt.isEmpty()) {
            return "redirect:/";
        }
        
        CauHoi cauHoi = cauHoiOpt.get();
        
        // Kiểm tra xem user đã xem câu hỏi này trong session chưa
        @SuppressWarnings("unchecked")
        Set<String> viewedQuestions = (Set<String>) session.getAttribute("viewedQuestions");
        if (viewedQuestions == null) {
            viewedQuestions = new HashSet<>();
        }
        
        // Chỉ tăng lượt xem nếu chưa xem trong session này
        if (!viewedQuestions.contains(id)) {
            cauHoiService.tangLuotXem(id);
            viewedQuestions.add(id);
            session.setAttribute("viewedQuestions", viewedQuestions);
            // Refresh lại câu hỏi để lấy lượt xem mới
            cauHoi = cauHoiService.timTheoId(id).orElse(cauHoi);
        }
        
        model.addAttribute("cauHoi", cauHoi);
        // Sử dụng cấu trúc cây cho bình luận lồng nhau
        model.addAttribute("cauTraLois", cauTraLoiService.timTheoCauHoiDangCay(id));
        
        // Kiểm tra quyền sửa/xóa
        boolean isOwner = false;
        boolean isAdmin = false;
        boolean isLoggedIn = authentication != null;
        if (authentication != null) {
            // Lấy manguoidung từ tendangnhap
            Optional<NguoiDung> currentUserOpt = nguoiDungService.timTheoTenDangNhap(authentication.getName());
            if (currentUserOpt.isPresent()) {
                String currentUserId = currentUserOpt.get().getManguoidung();
                isOwner = currentUserId.equals(cauHoi.getManguoidung());
            }
            isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        }
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isLoggedIn", isLoggedIn);
        
        // Kiểm tra user đã vote chưa
        String userVote = null;
        if (authentication != null) {
            Optional<NguoiDung> nguoiDungOpt = nguoiDungService.timTheoTenDangNhap(authentication.getName());
            if (nguoiDungOpt.isPresent()) {
                String manguoidung = nguoiDungOpt.get().getManguoidung();
                if (cauHoi.getNguoiDaThich() != null && cauHoi.getNguoiDaThich().contains(manguoidung)) {
                    userVote = "up";
                } else if (cauHoi.getNguoiKhongThich() != null && cauHoi.getNguoiKhongThich().contains(manguoidung)) {
                    userVote = "down";
                }
            }
        }
        model.addAttribute("userVote", userVote);
        
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
            @RequestParam(required = false) String noidung,
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
        
        // Debug log để kiểm tra nội dung
        System.out.println("=== DEBUG: Nội dung câu hỏi ===");
        System.out.println("Tiêu đề: " + tieude);
        System.out.println("Nội dung: " + noidung);
        System.out.println("Nội dung length: " + (noidung != null ? noidung.length() : "null"));
        System.out.println("==============================");
        
        CauHoi cauHoi = new CauHoi();
        cauHoi.setTieude(tieude);
        cauHoi.setNoidung(noidung);
        cauHoi.setManguoidung(nguoiDung.getManguoidung());
        cauHoi.setTennguoidung(nguoiDung.getHoten());
        cauHoi.setNgaydang(LocalDateTime.now());
        cauHoi.setNgaycapnhat(LocalDateTime.now());
        cauHoi.setDaduocduyet(false); // Cần admin duyệt trước khi hiển thị
        
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
            @RequestParam(required = false) String noidung,
            @RequestParam(required = false) List<MultipartFile> hinhAnh,
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
        cauTraLoi.setMacauhoi(cauHoi.getId());
        cauTraLoi.setManguoidung(nguoiDung.getManguoidung());
        cauTraLoi.setTennguoidung(nguoiDung.getHoten());
        cauTraLoi.setAnhdaidien(nguoiDung.getAnhdaidien());
        cauTraLoi.setNoidung(noidung);
        cauTraLoi.setNgaytraloi(LocalDateTime.now());
        
        // Upload hình ảnh đính kèm
        List<String> dinhKemList = new ArrayList<>();
        if (hinhAnh != null && !hinhAnh.isEmpty()) {
            try {
                Path uploadDir = Paths.get(uploadPath, "comments");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }
                for (MultipartFile file : hinhAnh) {
                    if (!file.isEmpty() && file.getContentType() != null && file.getContentType().startsWith("image/")) {
                        String extension = "";
                        String originalFilename = file.getOriginalFilename();
                        if (originalFilename != null && originalFilename.contains(".")) {
                            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                        }
                        String newFilename = UUID.randomUUID().toString() + extension;
                        Path filePath = uploadDir.resolve(newFilename);
                        Files.copy(file.getInputStream(), filePath);
                        dinhKemList.add("/uploads/comments/" + newFilename);
                    }
                }
            } catch (IOException e) {
                System.err.println("Lỗi upload ảnh bình luận: " + e.getMessage());
            }
        }
        if (!dinhKemList.isEmpty()) {
            cauTraLoi.setDinhkem(dinhKemList);
        }
        
        cauTraLoiService.luu(cauTraLoi);
        
        // Cập nhật số bình luận
        long soBinhLuan = cauTraLoiService.demTheoCauHoi(id);
        cauHoiService.capNhatSoBinhLuan(id, (int) soBinhLuan);
        
        // Tạo thông báo cho người đăng câu hỏi (nếu không phải chính mình trả lời)
        if (!nguoiDung.getManguoidung().equals(cauHoi.getManguoidung())) {
            String tieude = "Có người trả lời câu hỏi của bạn";
            String noiDungTB = nguoiDung.getHoten() + " đã trả lời câu hỏi: \"" + 
                              (cauHoi.getTieude().length() > 50 ? cauHoi.getTieude().substring(0, 50) + "..." : cauHoi.getTieude()) + "\"";
            String link = "/cau-hoi/" + id;
            thongBaoService.taoThongBao(
                cauHoi.getManguoidung(),
                tieude,
                noiDungTB,
                link,
                "REPLY",
                nguoiDung.getManguoidung(),
                nguoiDung.getHoten(),
                nguoiDung.getAnhdaidien()
            );
        }
        
        // Redirect về comment mới tạo với query param để hiện toast
        return "redirect:/cau-hoi/" + id + "?commented=1#comment-" + cauTraLoi.getMacautraloi();
    }
    
    /**
     * Trả lời một bình luận (reply to comment)
     */
    @PostMapping("/{cauhoiId}/tra-loi/{traloiId}/phan-hoi")
    public String phanHoiBinhLuan(
            @PathVariable String cauhoiId,
            @PathVariable String traloiId,
            @RequestParam String noidung,
            @RequestParam(required = false) List<MultipartFile> hinhAnh,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        if (authentication == null) {
            return "redirect:/dang-nhap";
        }
        
        Optional<CauHoi> cauHoiOpt = cauHoiService.timTheoId(cauhoiId);
        if (cauHoiOpt.isEmpty()) {
            return "redirect:/";
        }
        
        CauHoi cauHoi = cauHoiOpt.get();
        
        // Tìm bình luận cha
        Optional<CauTraLoi> binhLuanChaOpt = cauTraLoiService.timTheoId(traloiId);
        if (binhLuanChaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy bình luận!");
            return "redirect:/cau-hoi/" + cauhoiId;
        }
        
        CauTraLoi binhLuanCha = binhLuanChaOpt.get();
        
        Optional<NguoiDung> nguoiDungOpt = nguoiDungService.timTheoTenDangNhap(authentication.getName());
        if (nguoiDungOpt.isEmpty()) {
            return "redirect:/dang-nhap";
        }
        
        NguoiDung nguoiDung = nguoiDungOpt.get();
        
        // Xác định bình luận gốc (root comment)
        String rootCommentId = binhLuanCha.getMacautraloicha();
        if (rootCommentId == null || rootCommentId.isEmpty()) {
            rootCommentId = traloiId;
        }
        
        // Tạo bình luận con
        CauTraLoi binhLuanCon = new CauTraLoi();
        binhLuanCon.setMacauhoi(cauhoiId);
        binhLuanCon.setManguoidung(nguoiDung.getManguoidung());
        binhLuanCon.setTennguoidung(nguoiDung.getHoten());
        binhLuanCon.setAnhdaidien(nguoiDung.getAnhdaidien());
        binhLuanCon.setNoidung(noidung);
        binhLuanCon.setNgaytraloi(LocalDateTime.now());
        binhLuanCon.setMacautraloicha(rootCommentId);
        
        // Upload hình ảnh đính kèm
        List<String> dinhKemList = new ArrayList<>();
        if (hinhAnh != null && !hinhAnh.isEmpty()) {
            try {
                Path uploadDir = Paths.get(uploadPath, "comments");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }
                for (MultipartFile file : hinhAnh) {
                    if (!file.isEmpty() && file.getContentType() != null && file.getContentType().startsWith("image/")) {
                        String extension = "";
                        String originalFilename = file.getOriginalFilename();
                        if (originalFilename != null && originalFilename.contains(".")) {
                            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                        }
                        String newFilename = UUID.randomUUID().toString() + extension;
                        Path filePath = uploadDir.resolve(newFilename);
                        Files.copy(file.getInputStream(), filePath);
                        dinhKemList.add("/uploads/comments/" + newFilename);
                    }
                }
            } catch (IOException e) {
                System.err.println("Lỗi upload ảnh bình luận: " + e.getMessage());
            }
        }
        if (!dinhKemList.isEmpty()) {
            binhLuanCon.setDinhkem(dinhKemList);
        }
        binhLuanCon.setTenNguoiDuocTraLoi(binhLuanCha.getTennguoidung()); // Lưu tên người được reply
        
        cauTraLoiService.luu(binhLuanCon);
        
        // Cập nhật số bình luận
        long soBinhLuan = cauTraLoiService.demTheoCauHoi(cauhoiId);
        cauHoiService.capNhatSoBinhLuan(cauhoiId, (int) soBinhLuan);
        
        // Gửi thông báo cho người được reply (nếu không phải chính mình)
        if (!nguoiDung.getManguoidung().equals(binhLuanCha.getManguoidung())) {
            String tieude = "Có người phản hồi bình luận của bạn";
            String noiDungTB = nguoiDung.getHoten() + " đã phản hồi bình luận của bạn trong câu hỏi: \"" + 
                              (cauHoi.getTieude().length() > 40 ? cauHoi.getTieude().substring(0, 40) + "..." : cauHoi.getTieude()) + "\"";
            String link = "/cau-hoi/" + cauhoiId;
            thongBaoService.taoThongBao(
                binhLuanCha.getManguoidung(),
                tieude,
                noiDungTB,
                link,
                "REPLY",
                nguoiDung.getManguoidung(),
                nguoiDung.getHoten(),
                nguoiDung.getAnhdaidien()
            );
        }
        
        // Nếu người đăng câu hỏi khác người reply VÀ khác người được reply -> thông báo cho họ
        if (!nguoiDung.getManguoidung().equals(cauHoi.getManguoidung()) 
            && !binhLuanCha.getManguoidung().equals(cauHoi.getManguoidung())) {
            String tieude = "Có thảo luận mới trong câu hỏi của bạn";
            String noiDungTB = nguoiDung.getHoten() + " đã tham gia thảo luận trong câu hỏi của bạn";
            String link = "/cau-hoi/" + cauhoiId;
            thongBaoService.taoThongBao(
                cauHoi.getManguoidung(),
                tieude,
                noiDungTB,
                link,
                "REPLY",
                nguoiDung.getManguoidung(),
                nguoiDung.getHoten(),
                nguoiDung.getAnhdaidien()
            );
        }
        
        // Redirect về comment cha với query param để hiện toast
        return "redirect:/cau-hoi/" + cauhoiId + "?replied=1#comment-" + rootCommentId;
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
                    return "redirect:/cau-hoi/" + cauhoiId + "?deleted=1#comments-container";
                }
            }
        }
        
        return "redirect:/cau-hoi/" + cauhoiId + "#comments-container";
    }
    
    /**
     * API Vote câu hỏi (hữu ích / không hữu ích)
     */
    @PostMapping("/{id}/vote")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> voteCauHoi(
            @PathVariable String id,
            @RequestParam String type,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (authentication == null) {
            response.put("success", false);
            response.put("message", "Vui lòng đăng nhập để vote");
            return ResponseEntity.ok(response);
        }
        
        Optional<CauHoi> cauHoiOpt = cauHoiService.timTheoId(id);
        if (cauHoiOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Không tìm thấy câu hỏi");
            return ResponseEntity.ok(response);
        }
        
        Optional<NguoiDung> nguoiDungOpt = nguoiDungService.timTheoTenDangNhap(authentication.getName());
        if (nguoiDungOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Không tìm thấy người dùng");
            return ResponseEntity.ok(response);
        }
        
        CauHoi cauHoi = cauHoiOpt.get();
        String manguoidung = nguoiDungOpt.get().getManguoidung();
        
        // Khởi tạo list nếu null
        if (cauHoi.getNguoiDaThich() == null) {
            cauHoi.setNguoiDaThich(new ArrayList<>());
        }
        if (cauHoi.getNguoiKhongThich() == null) {
            cauHoi.setNguoiKhongThich(new ArrayList<>());
        }
        
        String userVote = null;
        
        if ("up".equals(type)) {
            if (cauHoi.getNguoiDaThich().contains(manguoidung)) {
                // Đã vote up -> bỏ vote
                cauHoi.getNguoiDaThich().remove(manguoidung);
                userVote = null;
            } else {
                // Vote up
                cauHoi.getNguoiDaThich().add(manguoidung);
                cauHoi.getNguoiKhongThich().remove(manguoidung); // Bỏ vote down nếu có
                userVote = "up";
            }
        } else if ("down".equals(type)) {
            if (cauHoi.getNguoiKhongThich().contains(manguoidung)) {
                // Đã vote down -> bỏ vote
                cauHoi.getNguoiKhongThich().remove(manguoidung);
                userVote = null;
            } else {
                // Vote down
                cauHoi.getNguoiKhongThich().add(manguoidung);
                cauHoi.getNguoiDaThich().remove(manguoidung); // Bỏ vote up nếu có
                userVote = "down";
            }
        }
        
        // Tính lại số vote
        int voteCount = cauHoi.getNguoiDaThich().size() - cauHoi.getNguoiKhongThich().size();
        cauHoi.setLuotthich(voteCount);
        
        cauHoiService.luu(cauHoi);
        
        response.put("success", true);
        response.put("voteCount", voteCount);
        response.put("userVote", userVote);
        
        return ResponseEntity.ok(response);
    }
}
