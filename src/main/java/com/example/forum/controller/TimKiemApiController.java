package com.example.forum.controller;

import com.example.forum.model.CauHoi;
import com.example.forum.service.CauHoiService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tim-kiem")
@RequiredArgsConstructor
public class TimKiemApiController {
    
    private final CauHoiService cauHoiService;
    
    @GetMapping("/goi-y")
    public ResponseEntity<List<Map<String, Object>>> goiY(@RequestParam(required = false) String q) {
        List<CauHoi> cauHois;
        
        if (q == null || q.trim().isEmpty()) {
            // Nếu không có từ khóa, trả về câu hỏi mới nhất
            cauHois = cauHoiService.layMoiNhat(8);
        } else {
            // Tìm kiếm theo từ khóa
            Page<CauHoi> page = cauHoiService.timKiem(q, 0, 8);
            cauHois = page.getContent();
        }
        
        List<Map<String, Object>> results = cauHois.stream()
            .map(cauHoi -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id", cauHoi.getId());
                item.put("tieude", cauHoi.getTieude());
                item.put("tennguoidung", cauHoi.getTennguoidung());
                item.put("chuDe", cauHoi.getChude() != null ? cauHoi.getChude().getTenchude() : "");
                item.put("luotxem", cauHoi.getLuotxem());
                // Lấy chữ cái đầu của tên người dùng
                String chuCaiDau = "";
                if (cauHoi.getTennguoidung() != null && !cauHoi.getTennguoidung().isEmpty()) {
                    String[] parts = cauHoi.getTennguoidung().split(" ");
                    if (parts.length > 0) {
                        chuCaiDau = parts[parts.length - 1].substring(0, 1).toUpperCase();
                    }
                }
                item.put("chuCaiDau", chuCaiDau);
                return item;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(results);
    }
}
