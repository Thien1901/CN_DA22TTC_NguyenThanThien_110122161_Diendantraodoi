package com.example.forum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.base-url}")
    private String baseUrl;

    public void guiEmailDatLaiMatKhau(String toEmail, String hoTen, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Đặt lại mật khẩu - Diễn đàn CNTT");
            
            String resetLink = baseUrl + "/dat-lai-mat-khau?token=" + token;
            
            String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: linear-gradient(135deg, #6366f1, #8b5cf6); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                        .content { background: #f8fafc; padding: 30px; border: 1px solid #e2e8f0; }
                        .button { display: inline-block; background: #6366f1; color: white !important; padding: 14px 30px; text-decoration: none; border-radius: 8px; font-weight: 600; margin: 20px 0; }
                        .button:hover { background: #4f46e5; }
                        .footer { text-align: center; padding: 20px; color: #64748b; font-size: 14px; }
                        .warning { background: #fef3c7; border: 1px solid #f59e0b; padding: 15px; border-radius: 8px; margin-top: 20px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🔐 Đặt lại mật khẩu</h1>
                        </div>
                        <div class="content">
                            <p>Xin chào <strong>%s</strong>,</p>
                            <p>Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn tại <strong>Diễn đàn CNTT</strong>.</p>
                            <p>Nhấn vào nút bên dưới để đặt lại mật khẩu:</p>
                            <p style="text-align: center;">
                                <a href="%s" class="button">Đặt lại mật khẩu</a>
                            </p>
                            <p>Hoặc copy đường link sau vào trình duyệt:</p>
                            <p style="word-break: break-all; background: #e2e8f0; padding: 10px; border-radius: 5px;">%s</p>
                            <div class="warning">
                                <strong>⚠️ Lưu ý:</strong>
                                <ul style="margin: 10px 0 0 0; padding-left: 20px;">
                                    <li>Link này chỉ có hiệu lực trong <strong>30 phút</strong></li>
                                    <li>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này</li>
                                </ul>
                            </div>
                        </div>
                        <div class="footer">
                            <p>© 2025 Diễn đàn CNTT - Trao đổi kiến thức chuyên ngành</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(hoTen, resetLink, resetLink);
            
            helper.setText(htmlContent, true);
            mailSender.send(message);
            
            log.info("Đã gửi email đặt lại mật khẩu đến: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Lỗi gửi email đến {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Không thể gửi email. Vui lòng thử lại sau.");
        }
    }
}
