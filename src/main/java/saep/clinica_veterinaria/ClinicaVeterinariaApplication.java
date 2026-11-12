package saep.clinica_veterinaria;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class ClinicaVeterinariaApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(ClinicaVeterinariaApplication.class, args);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                HttpSession session = request.getSession(false);
                if (session == null || session.getAttribute("usuarioEmail") == null) {
                    response.sendRedirect("/login");
                    return false;
                }
                return true;
            }
        })
        .addPathPatterns("/**")
        .excludePathPatterns("/login", "/logout", "/entrar", "/sair", "/css/**", "/js/**", "/images/**", "/error", "/favicon.ico");
    }
}
