package com.example.forum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Cautraloi")
public class CauTraLoi {
    @Id
    private String macautraloi;
    
    @Indexed
    private String macauhoi;
    
    @Indexed
    private String manguoidung;
    private String tennguoidung;
    private String anhdaidien;
    
    private String noidung;
    private LocalDateTime ngaytraloi = LocalDateTime.now();
    
    // Ảnh đính kèm trong bình luận
    private List<String> dinhkem;
    
    // Trường mới cho nested comments
    @Indexed
    private String macautraloicha; // ID của bình luận cha (null nếu là bình luận gốc)
    
    private String tenNguoiDuocTraLoi; // Tên người được trả lời (để hiển thị @mention)
    
    @Transient // Không lưu vào DB, chỉ dùng để hiển thị
    private List<CauTraLoi> cacTraLoiCon = new ArrayList<>();
}
