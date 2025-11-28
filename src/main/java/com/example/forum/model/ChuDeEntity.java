package com.example.forum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Chude")
public class ChuDeEntity {
    @Id
    private String id;
    
    private String machude;
    private String tenchude;
    private String mota;
    private String icon; // Bootstrap icon class
    private int thutu; // Thứ tự hiển thị
}
