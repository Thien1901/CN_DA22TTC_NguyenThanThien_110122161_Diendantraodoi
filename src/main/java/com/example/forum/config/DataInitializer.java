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
    }
}
