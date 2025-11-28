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
}
