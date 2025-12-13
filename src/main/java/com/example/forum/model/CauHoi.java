package com.example.forum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Cauhoi")
public class CauHoi {
    @Id
    private String id;
    
    private String tieude;
    private String noidung;
    
    @Indexed
    private String manguoidung;
    private String tennguoidung;
    
    private LocalDateTime ngaydang = LocalDateTime.now();
    private LocalDateTime ngaycapnhat;
    private int luotxem = 0;
    private Integer luotthich = 0;
    private List<String> nguoiDaThich = new ArrayList<>(); // Danh sách manguoidung đã vote up
    private List<String> nguoiKhongThich = new ArrayList<>(); // Danh sách manguoidung đã vote down
    private boolean daduocduyet = false;
    private int soluongbinhluan = 0;
    
    private List<String> dinhkem = new ArrayList<>();
    private ChuDe chude;
}
