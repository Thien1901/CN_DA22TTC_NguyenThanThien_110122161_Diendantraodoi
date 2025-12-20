package com.example.forum.controller;

import com.example.forum.model.CauHoi;
import com.example.forum.model.CauTraLoi;
import com.example.forum.model.NguoiDung;
import com.example.forum.service.CauHoiService;
import com.example.forum.service.CauTraLoiService;
import com.example.forum.service.NguoiDungService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/like")
@RequiredArgsConstructor
public class LikeController {

    private final CauHoiService cauHoiService;
    private final CauTraLoiService cauTraLoiService;
    private final NguoiDungService nguoiDungService;

    private String getManguoidung(Authentication authentication) {
        if (authentication == null) return null;
        
        if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            String email = oauth2User.getAttribute("email");
            return nguoiDungService.timTheoEmail(email)
                    .map(NguoiDung::getManguoidung)
                    .orElse(null);
        }
        
        return nguoiDungService.timTheoTenDangNhap(authentication.getName())
                .map(NguoiDung::getManguoidung)
                .orElse(null);
    }

    // Like câu hỏi
    @PostMapping("/cau-hoi/{id}/like")
    public ResponseEntity<Map<String, Object>> likeCauHoi(@PathVariable String id, Authentication auth) {
        String manguoidung = getManguoidung(auth);
        if (manguoidung == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Chưa đăng nhập"));
        }

        Optional<CauHoi> cauHoiOpt = cauHoiService.timTheoId(id);
        if (cauHoiOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        CauHoi cauHoi = cauHoiOpt.get();
        if (cauHoi.getNguoiDaThich() == null) cauHoi.setNguoiDaThich(new ArrayList<>());
        if (cauHoi.getNguoiKhongThich() == null) cauHoi.setNguoiKhongThich(new ArrayList<>());

        boolean liked = false;
        if (cauHoi.getNguoiDaThich().contains(manguoidung)) {
            // Bỏ like
            cauHoi.getNguoiDaThich().remove(manguoidung);
        } else {
            // Thêm like và bỏ dislike nếu có
            cauHoi.getNguoiDaThich().add(manguoidung);
            cauHoi.getNguoiKhongThich().remove(manguoidung);
            liked = true;
        }

        cauHoi.setLuotthich(cauHoi.getNguoiDaThich().size() - cauHoi.getNguoiKhongThich().size());
        cauHoiService.luu(cauHoi);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked);
        response.put("disliked", false);
        response.put("likes", cauHoi.getNguoiDaThich().size());
        response.put("dislikes", cauHoi.getNguoiKhongThich().size());
        response.put("score", cauHoi.getLuotthich());
        return ResponseEntity.ok(response);
    }

    // Dislike câu hỏi
    @PostMapping("/cau-hoi/{id}/dislike")
    public ResponseEntity<Map<String, Object>> dislikeCauHoi(@PathVariable String id, Authentication auth) {
        String manguoidung = getManguoidung(auth);
        if (manguoidung == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Chưa đăng nhập"));
        }

        Optional<CauHoi> cauHoiOpt = cauHoiService.timTheoId(id);
        if (cauHoiOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        CauHoi cauHoi = cauHoiOpt.get();
        if (cauHoi.getNguoiDaThich() == null) cauHoi.setNguoiDaThich(new ArrayList<>());
        if (cauHoi.getNguoiKhongThich() == null) cauHoi.setNguoiKhongThich(new ArrayList<>());

        boolean disliked = false;
        if (cauHoi.getNguoiKhongThich().contains(manguoidung)) {
            // Bỏ dislike
            cauHoi.getNguoiKhongThich().remove(manguoidung);
        } else {
            // Thêm dislike và bỏ like nếu có
            cauHoi.getNguoiKhongThich().add(manguoidung);
            cauHoi.getNguoiDaThich().remove(manguoidung);
            disliked = true;
        }

        cauHoi.setLuotthich(cauHoi.getNguoiDaThich().size() - cauHoi.getNguoiKhongThich().size());
        cauHoiService.luu(cauHoi);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", false);
        response.put("disliked", disliked);
        response.put("likes", cauHoi.getNguoiDaThich().size());
        response.put("dislikes", cauHoi.getNguoiKhongThich().size());
        response.put("score", cauHoi.getLuotthich());
        return ResponseEntity.ok(response);
    }

    // Lấy trạng thái like câu hỏi
    @GetMapping("/cau-hoi/{id}/status")
    public ResponseEntity<Map<String, Object>> getCauHoiLikeStatus(@PathVariable String id, Authentication auth) {
        Optional<CauHoi> cauHoiOpt = cauHoiService.timTheoId(id);
        if (cauHoiOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        CauHoi cauHoi = cauHoiOpt.get();
        String manguoidung = getManguoidung(auth);

        Map<String, Object> response = new HashMap<>();
        response.put("likes", cauHoi.getNguoiDaThich() != null ? cauHoi.getNguoiDaThich().size() : 0);
        response.put("dislikes", cauHoi.getNguoiKhongThich() != null ? cauHoi.getNguoiKhongThich().size() : 0);
        response.put("score", cauHoi.getLuotthich() != null ? cauHoi.getLuotthich() : 0);
        response.put("liked", manguoidung != null && cauHoi.getNguoiDaThich() != null && cauHoi.getNguoiDaThich().contains(manguoidung));
        response.put("disliked", manguoidung != null && cauHoi.getNguoiKhongThich() != null && cauHoi.getNguoiKhongThich().contains(manguoidung));
        return ResponseEntity.ok(response);
    }

    // Like bình luận
    @PostMapping("/binh-luan/{id}/like")
    public ResponseEntity<Map<String, Object>> likeBinhLuan(@PathVariable String id, Authentication auth) {
        String manguoidung = getManguoidung(auth);
        if (manguoidung == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Chưa đăng nhập"));
        }

        Optional<CauTraLoi> cauTraLoiOpt = cauTraLoiService.timTheoId(id);
        if (cauTraLoiOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        CauTraLoi cauTraLoi = cauTraLoiOpt.get();
        if (cauTraLoi.getNguoiDaThich() == null) cauTraLoi.setNguoiDaThich(new ArrayList<>());
        if (cauTraLoi.getNguoiKhongThich() == null) cauTraLoi.setNguoiKhongThich(new ArrayList<>());

        boolean liked = false;
        if (cauTraLoi.getNguoiDaThich().contains(manguoidung)) {
            cauTraLoi.getNguoiDaThich().remove(manguoidung);
        } else {
            cauTraLoi.getNguoiDaThich().add(manguoidung);
            cauTraLoi.getNguoiKhongThich().remove(manguoidung);
            liked = true;
        }

        cauTraLoi.setLuotthich(cauTraLoi.getNguoiDaThich().size() - cauTraLoi.getNguoiKhongThich().size());
        cauTraLoiService.luu(cauTraLoi);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked);
        response.put("disliked", false);
        response.put("likes", cauTraLoi.getNguoiDaThich().size());
        response.put("dislikes", cauTraLoi.getNguoiKhongThich().size());
        response.put("score", cauTraLoi.getLuotthich());
        return ResponseEntity.ok(response);
    }

    // Dislike bình luận
    @PostMapping("/binh-luan/{id}/dislike")
    public ResponseEntity<Map<String, Object>> dislikeBinhLuan(@PathVariable String id, Authentication auth) {
        String manguoidung = getManguoidung(auth);
        if (manguoidung == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Chưa đăng nhập"));
        }

        Optional<CauTraLoi> cauTraLoiOpt = cauTraLoiService.timTheoId(id);
        if (cauTraLoiOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        CauTraLoi cauTraLoi = cauTraLoiOpt.get();
        if (cauTraLoi.getNguoiDaThich() == null) cauTraLoi.setNguoiDaThich(new ArrayList<>());
        if (cauTraLoi.getNguoiKhongThich() == null) cauTraLoi.setNguoiKhongThich(new ArrayList<>());

        boolean disliked = false;
        if (cauTraLoi.getNguoiKhongThich().contains(manguoidung)) {
            cauTraLoi.getNguoiKhongThich().remove(manguoidung);
        } else {
            cauTraLoi.getNguoiKhongThich().add(manguoidung);
            cauTraLoi.getNguoiDaThich().remove(manguoidung);
            disliked = true;
        }

        cauTraLoi.setLuotthich(cauTraLoi.getNguoiDaThich().size() - cauTraLoi.getNguoiKhongThich().size());
        cauTraLoiService.luu(cauTraLoi);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", false);
        response.put("disliked", disliked);
        response.put("likes", cauTraLoi.getNguoiDaThich().size());
        response.put("dislikes", cauTraLoi.getNguoiKhongThich().size());
        response.put("score", cauTraLoi.getLuotthich());
        return ResponseEntity.ok(response);
    }

    // Lấy trạng thái like bình luận
    @GetMapping("/binh-luan/{id}/status")
    public ResponseEntity<Map<String, Object>> getBinhLuanLikeStatus(@PathVariable String id, Authentication auth) {
        Optional<CauTraLoi> cauTraLoiOpt = cauTraLoiService.timTheoId(id);
        if (cauTraLoiOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        CauTraLoi cauTraLoi = cauTraLoiOpt.get();
        String manguoidung = getManguoidung(auth);

        Map<String, Object> response = new HashMap<>();
        response.put("likes", cauTraLoi.getNguoiDaThich() != null ? cauTraLoi.getNguoiDaThich().size() : 0);
        response.put("dislikes", cauTraLoi.getNguoiKhongThich() != null ? cauTraLoi.getNguoiKhongThich().size() : 0);
        response.put("score", cauTraLoi.getLuotthich() != null ? cauTraLoi.getLuotthich() : 0);
        response.put("liked", manguoidung != null && cauTraLoi.getNguoiDaThich() != null && cauTraLoi.getNguoiDaThich().contains(manguoidung));
        response.put("disliked", manguoidung != null && cauTraLoi.getNguoiKhongThich() != null && cauTraLoi.getNguoiKhongThich().contains(manguoidung));
        return ResponseEntity.ok(response);
    }
}
