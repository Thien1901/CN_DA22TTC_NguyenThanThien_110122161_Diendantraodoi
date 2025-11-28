package com.example.forum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DinhKem {
    private String tenfile;
    private String duongdan;
    private String loaifile;
    private Long kichthuoc;
}
