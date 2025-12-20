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
@Document(collection = "ResetToken")
public class ResetToken {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String token;
    
    @Indexed
    private String email;
    
    private LocalDateTime ngayTao = LocalDateTime.now();
    
    // Token hết hạn sau 30 phút
    private LocalDateTime ngayHetHan = LocalDateTime.now().plusMinutes(30);
    
    private boolean daSuDung = false;
    
    public boolean isHetHan() {
        return LocalDateTime.now().isAfter(ngayHetHan);
    }
}
