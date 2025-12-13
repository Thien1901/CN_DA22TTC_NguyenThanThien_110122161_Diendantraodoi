package com.example.forum.controller;

import com.example.forum.service.CauHoiService;
import com.example.forum.service.CauTraLoiService;
import com.example.forum.service.NguoiDungService;
import com.example.forum.model.CauHoi;
import com.example.forum.model.NguoiDung;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminApiController {
    
    private final CauHoiService cauHoiService;
    private final CauTraLoiService cauTraLoiService;
    private final NguoiDungService nguoiDungService;
    
    @GetMapping("/thong-ke")
    public ResponseEntity<Map<String, Object>> getThongKe(
            @RequestParam(required = false) Integer days,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {
        
        Map<String, Object> result = new HashMap<>();
        
        LocalDate startDate;
        LocalDate endDate;
        
        // Xử lý tham số ngày
        if (fromDate != null && toDate != null) {
            startDate = LocalDate.parse(fromDate);
            endDate = LocalDate.parse(toDate);
        } else {
            int numDays = (days != null) ? days : 7;
            endDate = LocalDate.now();
            startDate = endDate.minusDays(numDays - 1);
        }
        
        List<String> labels = new ArrayList<>();
        List<Integer> cauHoiData = new ArrayList<>();
        List<Integer> cauTraLoiData = new ArrayList<>();
        List<Integer> nguoiDungData = new ArrayList<>();
        
        // Lấy tất cả dữ liệu
        List<CauHoi> allCauHoi = cauHoiService.layTatCa();
        List<NguoiDung> allNguoiDung = nguoiDungService.layTatCa();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        
        // Duyệt qua từng ngày trong khoảng
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            LocalDate date = currentDate;
            labels.add(date.format(formatter));
            
            // Đếm câu hỏi trong ngày
            long cauHoiCount = allCauHoi.stream()
                .filter(ch -> ch.getNgaydang() != null && ch.getNgaydang().toLocalDate().equals(date))
                .count();
            cauHoiData.add((int) cauHoiCount);
            
            // Đếm người dùng mới trong ngày
            long nguoiDungCount = allNguoiDung.stream()
                .filter(nd -> nd.getNgaytao() != null && nd.getNgaytao().toLocalDate().equals(date))
                .count();
            nguoiDungData.add((int) nguoiDungCount);
            
            // Câu trả lời - giả lập dựa trên câu hỏi (có thể cập nhật sau)
            cauTraLoiData.add((int) (cauHoiCount * 2));
            
            currentDate = currentDate.plusDays(1);
        }
        
        result.put("labels", labels);
        result.put("cauHoi", cauHoiData);
        result.put("cauTraLoi", cauTraLoiData);
        result.put("nguoiDung", nguoiDungData);
        
        // Tổng hợp
        result.put("tongCauHoi", allCauHoi.size());
        result.put("tongNguoiDung", allNguoiDung.size());
        result.put("tongLuotXem", allCauHoi.stream().mapToInt(CauHoi::getLuotxem).sum());
        
        return ResponseEntity.ok(result);
    }
}
