package com.example.forum.config;

import com.example.forum.model.NguoiDung;
import com.example.forum.model.VaiTro;
import com.example.forum.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        // Tạo tài khoản Admin mặc định nếu chưa có
        if (!nguoiDungRepository.existsByTendangnhap("admin")) {
            NguoiDung admin = new NguoiDung();
            admin.setTendangnhap("admin");
            admin.setMatkhauhash(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@forum.com");
            admin.setHoten("Quản trị viên");
            admin.setTrangthai("hoatdong");
            admin.setNgaytao(LocalDateTime.now());
            admin.setVaitro(new VaiTro("ADMIN", "Quản trị viên"));
            nguoiDungRepository.save(admin);
            log.info("Tạo tài khoản admin mặc định: admin/admin123");
        }
        
        // Tạo tài khoản Admin thứ 2
        if (!nguoiDungRepository.existsByTendangnhap("admin2")) {
            NguoiDung admin2 = new NguoiDung();
            admin2.setTendangnhap("admin2");
            admin2.setMatkhauhash(passwordEncoder.encode("admin456"));
            admin2.setEmail("admin2@forum.com");
            admin2.setHoten("Quản trị viên 2");
            admin2.setTrangthai("hoatdong");
            admin2.setNgaytao(LocalDateTime.now());
            admin2.setVaitro(new VaiTro("ADMIN", "Quản trị viên"));
            nguoiDungRepository.save(admin2);
            log.info("Tạo tài khoản admin2: admin2/admin456");
        }
    }
}
