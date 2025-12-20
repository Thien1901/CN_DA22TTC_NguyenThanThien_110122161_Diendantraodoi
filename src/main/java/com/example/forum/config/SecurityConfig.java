package com.example.forum.config;

import com.example.forum.security.CustomUserDetailsService;
import com.example.forum.security.CustomOAuth2UserService;
import com.example.forum.security.OAuth2LoginSuccessHandler;
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
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/thong-bao/**", "/bao-cao/**", "/api/**")
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/dang-nhap", "/dang-ky", "/quen-mat-khau", "/dat-lai-mat-khau", "/cau-hoi/**", "/chuyen-nganh/**", "/chu-de/**", "/tim-kiem", "/ho-so/**", "/css/**", "/js/**", "/images/**", "/uploads/**", "/thong-bao/**", "/bao-cao/**", "/api/**", "/tat-ca-cau-hoi", "/oauth2/**", "/login/oauth2/**").permitAll()
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
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/dang-nhap")
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                )
                .successHandler(oAuth2LoginSuccessHandler)
                .failureUrl("/dang-nhap?error=oauth2")
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
