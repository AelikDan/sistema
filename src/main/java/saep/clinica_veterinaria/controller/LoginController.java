package saep.clinica_veterinaria.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import saep.clinica_veterinaria.model.Usuario;
import saep.clinica_veterinaria.service.ClinicaService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final ClinicaService service;

    public LoginController(ClinicaService service) {
        this.service = service;
    }

    @GetMapping({"/login", "/entrar"})
    public String login(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("usuarioEmail") != null) {
            return "redirect:/";
        }
        return "login";
    }

    @PostMapping({"/login", "/entrar"})
    public String autenticar(
            @RequestParam String email,
            @RequestParam String senha,
            HttpServletRequest request,
            Model model) {

        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            model.addAttribute("erro", "Preencha o e-mail e a senha.");
            model.addAttribute("email", email);
            return "login";
        }

        try {
            boolean autenticado = service.autenticar(email.trim(), senha);
            if (!autenticado) {
                model.addAttribute("erro", "E-mail ou senha inválidos.");
                model.addAttribute("email", email);
                return "login";
            }

            // Sessão simples e direta no estilo iniciante
            HttpSession sessao = request.getSession();
            Usuario usuario = service.usuario(email.trim());
            sessao.setAttribute("usuarioEmail", usuario.getEmail());
            sessao.setAttribute("usuarioNome", usuario.getNome());
            sessao.setMaxInactiveInterval(30 * 60);

            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao autenticar: " + e.getMessage());
            model.addAttribute("email", email);
            return "login";
        }
    }

    @GetMapping({"/logout", "/sair"})
    public String sairGet(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        if (sessao != null) {
            sessao.invalidate();
        }
        return "redirect:/login";
    }

    @PostMapping({"/logout", "/sair"})
    public String sairPost(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        if (sessao != null) {
            sessao.invalidate();
        }
        return "redirect:/login";
    }
}
