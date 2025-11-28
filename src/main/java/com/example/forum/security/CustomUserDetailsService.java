package com.example.forum.security;

import com.example.forum.model.NguoiDung;
import com.example.forum.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final NguoiDungRepository nguoiDungRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        NguoiDung nguoiDung = nguoiDungRepository.findByTendangnhap(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng: " + username));
        
        if ("bikhoa".equals(nguoiDung.getTrangthai())) {
            throw new UsernameNotFoundException("Tài khoản đã bị khóa");
        }
        
        return new User(
                nguoiDung.getTendangnhap(),
                nguoiDung.getMatkhauhash(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + nguoiDung.getVaitro().getMavaitro()))
        );
    }
}
