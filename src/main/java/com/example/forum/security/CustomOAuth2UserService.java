package com.example.forum.security;

import com.example.forum.model.NguoiDung;
import com.example.forum.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final NguoiDungRepository nguoiDungRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        String email = oAuth2User.getAttribute("email");
        
        // Tìm user trong database để lấy role
        Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findByEmail(email);
        
        String role = "THANHVIEN"; // Default role
        String username = email.split("@")[0];
        
        if (nguoiDungOpt.isPresent()) {
            NguoiDung nguoiDung = nguoiDungOpt.get();
            role = nguoiDung.getVaitro().getMavaitro();
            username = nguoiDung.getTendangnhap();
        }
        
        // Tạo authorities với role từ database
        var authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + role)
        );
        
        // Thêm username vào attributes để sử dụng trong SecurityContext
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("username", username);
        
        log.info("OAuth2 User loaded - Email: {}, Username: {}, Role: {}", email, username, role);
        
        return new DefaultOAuth2User(authorities, attributes, "email");
    }
}
