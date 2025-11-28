package com.example.forum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Thongbao")
public class ThongBao {
    @Id
    private String id;
    
    private String manguoidung; // Người nhận thông báo
    private String tieude;
    private String noidung;
    private String link; // Link đến câu hỏi/bình luận
    private String loai; // REPLY, LIKE, SYSTEM
    private boolean dadoc; // Đã đọc chưa
    private LocalDateTime ngaytao;
    
    // Thông tin người gửi (người trả lời)
    private String manguoigui;
    private String tennguoigui;
    private String avatarnguoigui;
}
