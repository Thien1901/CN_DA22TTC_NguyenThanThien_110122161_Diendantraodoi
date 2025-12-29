package com.example.forum.model.khaosat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embedded document - Câu hỏi trong phiếu khảo sát
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CauHoiKhaoSat {
    private String macauhoi;        // VD: "CH01", "CH02"
    private String noidung;         // Nội dung câu hỏi
    private String loai;            // "THANG_DIEM" (1-5) hoặc "VAN_BAN" (text)
    private Integer diemToiThieu;   // Mặc định 1
    private Integer diemToiDa;      // Mặc định 5
    private Boolean batBuoc;        // Bắt buộc trả lời?
}
