package com.example.forum.config;

import com.example.forum.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final CustomUserDetailsService userDetailsService;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/thong-bao/api/**")
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/dang-nhap", "/dang-ky", "/cau-hoi/**", "/chuyen-nganh/**", "/tim-kiem", "/ho-so/**", "/css/**", "/js/**", "/images/**", "/uploads/**", "/thong-bao/api/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/dang-nhap")
                .loginProcessingUrl("/dang-nhap")
                .defaultSuccessUrl("/", true)
                .failureUrl("/dang-nhap?error=true")
                .usernameParameter("tendangnhap")
                .passwordParameter("matkhau")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/dang-xuat")
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .userDetailsService(userDetailsService);
        
        return http.build();
    }
}
