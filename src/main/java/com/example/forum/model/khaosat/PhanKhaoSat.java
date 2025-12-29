package com.example.forum.model.khaosat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhanKhaoSat {
    private String tieuDe;
    private String moTa;
    private List<CauHoiKhaoSat> danhSachCauHoi;
}
