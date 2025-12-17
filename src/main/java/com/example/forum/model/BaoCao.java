package com.example.forum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Baocao")
public class BaoCao {
    @Id
    private String id;
    
    @Indexed
    private String loai; // "CAUHOI" hoặc "CAUTRALOI"
    
    @Indexed
    private String maDoiTuong; // ID của câu hỏi hoặc câu trả lời bị báo cáo
    
    private String tieuDeDoiTuong; // Tiêu đề câu hỏi (để hiển thị)
    
    @Indexed
    private String maNguoiBaoCao;
    private String tenNguoiBaoCao;
    
    private String lyDo; // Lý do báo cáo
    private String moTa; // Mô tả chi tiết (tùy chọn)
    
    private LocalDateTime ngayBaoCao = LocalDateTime.now();
    
    @Indexed
    private String trangThai = "CHO_XU_LY"; // CHO_XU_LY, DA_XU_LY, TU_CHOI
    
    private String ghiChuAdmin; // Ghi chú của admin khi xử lý
    private LocalDateTime ngayXuLy;
}
