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
@Document(collection = "Nguoidung")
public class NguoiDung {
    @Id
    private String manguoidung;
    
    @Indexed(unique = true)
    private String tendangnhap;
    
    private String matkhauhash;
    
    @Indexed(unique = true)
    private String email;
    
    private String hoten;
    private String anhdaidien;
    private String gioithieu;
    private String trangthai = "hoatdong"; // hoatdong, bikhoa
    private LocalDateTime ngaytao = LocalDateTime.now();
    private LocalDateTime lanhoatdongcuoi;
    private VaiTro vaitro;
}
