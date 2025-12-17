package com.example.forum.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
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
    private String maNguoiNhan;
    private String tieuDe;
    private String noiDung;
    private String loai; // BAO_CAO_MOI, TRA_LOI_MOI, REPLY
    private String duongDan;
    private boolean daDoc = false;
    private LocalDateTime ngayTao = LocalDateTime.now();
    
    // Thông tin người gửi (để hiển thị trong notification)
    private String tenNguoiGui;
    private String avatarNguoiGui;
    
    // Getter alias cho JavaScript compatibility (lowercase để JSON serialize đúng)
    public String getLink() { return duongDan; }
    public boolean getDadoc() { return daDoc; }
    public LocalDateTime getNgaytao() { return ngayTao; }
    public String getTennguoigui() { return tenNguoiGui; }
    public String getAvatarnguoigui() { return avatarNguoiGui; }
}
