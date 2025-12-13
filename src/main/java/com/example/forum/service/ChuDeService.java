package com.example.forum.service;

import com.example.forum.model.ChuDeEntity;
import com.example.forum.repository.ChuDeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ChuDeService {
    
    private final ChuDeRepository chuDeRepository;
    
    // Khởi tạo dữ liệu mẫu khi ứng dụng chạy
    @PostConstruct
    public void initData() {
        // Xóa và tạo lại dữ liệu để đảm bảo ID đúng
        chuDeRepository.deleteAll();
        chuDeRepository.save(new ChuDeEntity(null, "1", "Lập trình", "Kỹ thuật lập trình, OOP, C/C++, Java, Python...", "bi-code-slash", 1));
        chuDeRepository.save(new ChuDeEntity(null, "2", "Cơ sở dữ liệu", "SQL, NoSQL, thiết kế CSDL, query...", "bi-database", 2));
        chuDeRepository.save(new ChuDeEntity(null, "3", "Mạng & Hệ thống", "Mạng máy tính, hệ điều hành, Linux, bảo mật...", "bi-hdd-network", 3));
        chuDeRepository.save(new ChuDeEntity(null, "4", "Web & Mobile", "HTML/CSS, JavaScript, React, Android, iOS...", "bi-phone", 4));
        chuDeRepository.save(new ChuDeEntity(null, "5", "Phần mềm", "Phân tích thiết kế, UML, quản lý dự án...", "bi-gear", 5));
        chuDeRepository.save(new ChuDeEntity(null, "6", "AI & Dữ liệu", "Machine Learning, xử lý ảnh, khai phá dữ liệu...", "bi-robot", 6));
        chuDeRepository.save(new ChuDeEntity(null, "7", "Thuật toán", "CTDL, giải thuật, đồ thị, tối ưu...", "bi-diagram-3", 7));
        chuDeRepository.save(new ChuDeEntity(null, "8", "Khác", "Câu hỏi chung, hướng nghiệp, kinh nghiệm...", "bi-question-circle", 8));
    }
    
    public List<ChuDeEntity> layTatCa() {
        return chuDeRepository.findAllByOrderByThutuAsc();
    }
    
    public Optional<ChuDeEntity> timTheoMa(String machude) {
        return chuDeRepository.findByMachude(machude);
    }
    
    public Optional<ChuDeEntity> timTheoId(@NonNull String id) {
        return chuDeRepository.findById(id);
    }
    
    public ChuDeEntity luu(@NonNull ChuDeEntity chuDe) {
        return chuDeRepository.save(chuDe);
    }
    
    public void xoa(@NonNull String id) {
        chuDeRepository.deleteById(id);
    }
    
    public long dem() {
        return chuDeRepository.count();
    }
}
