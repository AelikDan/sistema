package saep.clinica_veterinaria.controller;

import saep.clinica_veterinaria.model.Tutor;
import saep.clinica_veterinaria.repository.ClinicaRepository;
import saep.clinica_veterinaria.service.ClinicaService;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ClinicaController {

    private final ClinicaRepository repository;
    private final ClinicaService service;

    public ClinicaController(ClinicaRepository repository, ClinicaService service) {
        this.repository = repository;
        this.service = service;
    }

    // ==========================================
    // PAGINA INICIAL
    // ==========================================

    @org.springframework.web.bind.annotation.ModelAttribute
    public void carregarDadosUsuario(jakarta.servlet.http.HttpSession session, Model model) {
        if (session != null && session.getAttribute("usuarioNome") != null) {
            model.addAttribute("usuarioNome", session.getAttribute("usuarioNome"));
        }
    }

    @GetMapping({"/", "/inicio", "/home"})
    public String inicio(Model model) {
        try {
            model.addAttribute("contagens", service.contagens());
        } catch (Exception e) {
            model.addAttribute("contagens", java.util.Map.of("tutores", 0, "pets", 0, "agendamentos", 0));
        }
        return "inicio";
    }

    // ==========================================
    // TUTORES
    // ==========================================

    @GetMapping("/tutores")
    public String tutores(@RequestParam(defaultValue = "") String busca, Model model) {
        model.addAttribute("busca", busca);
        model.addAttribute("tutores", repository.listarTutores(busca));
        return "tutores";
    }

    @GetMapping({"/tutores/novo", "/tutores/cadastrar"})
    public String novoTutor() {
        return "tutor-form";
    }

    @PostMapping({"/tutores", "/tutores/novo", "/tutores/cadastrar"})
    public String criarTutor(
            @RequestParam String nome,
            @RequestParam String cpf,
            @RequestParam String telefone,
            @RequestParam String email,
            RedirectAttributes redirect) {

        if (nome.isBlank() || cpf.isBlank() || telefone.isBlank() || email.isBlank()) {
            redirect.addFlashAttribute("erro", "Preencha todos os campos do tutor.");
            return "redirect:/tutores/novo";
        }

        service.criarTutor(nome, cpf, telefone, email);
        redirect.addFlashAttribute("sucesso", "Tutor cadastrado com sucesso!");
        return "redirect:/tutores";
    }

    @GetMapping({"/tutores/{id}/editar", "/tutores/editar/{id}"})
    public String editarTutor(@PathVariable int id, Model model) {
        Optional<Tutor> optTutor = repository.buscarTutor(id);
        if (optTutor.isEmpty()) {
            return "redirect:/tutores";
        }
        model.addAttribute("tutor", optTutor.get());
        return "tutor-editar";
    }

    @GetMapping("/tutores/{id}")
    public String verTutor(@PathVariable int id) {
        return "redirect:/tutores/" + id + "/editar";
    }

    @PostMapping({"/tutores/{id}", "/tutores/{id}/editar", "/tutores/editar/{id}"})
    public String atualizarTutor(
            @PathVariable int id,
            @RequestParam String nome,
            @RequestParam String telefone,
            @RequestParam String email,
            RedirectAttributes redirect) {

        if (nome.isBlank() || telefone.isBlank() || email.isBlank()) {
            redirect.addFlashAttribute("erro", "Preencha todos os campos.");
            return "redirect:/tutores/" + id + "/editar";
        }

        boolean atualizou = repository.atualizarTutor(id, nome.trim(), telefone.trim(), email.trim());
        if (atualizou) {
            redirect.addFlashAttribute("sucesso", "Dados do tutor atualizados com sucesso!");
        } else {
            redirect.addFlashAttribute("erro", "Tutor não encontrado para atualização.");
        }
        return "redirect:/tutores";
    }

    // ==========================================
    // PETS
    // ==========================================

    @GetMapping("/pets")
    public String pets(Model model) {
        model.addAttribute("pets", repository.listarPets());
        return "pets";
    }

    @GetMapping({"/pets/novo", "/pets/cadastrar"})
    public String novoPet(Model model) {
        model.addAttribute("tutores", repository.listarTutoresParaSelecao());
        return "pet-form";
    }

    @GetMapping("/pets/{id}")
    public String verPet(@PathVariable int id) {
        return "redirect:/pets";
    }

    @PostMapping({"/pets", "/pets/novo", "/pets/cadastrar"})
    public String criarPet(
            @RequestParam int tutorId,
            @RequestParam String nome,
            @RequestParam String especie,
            @RequestParam String raca,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataNascimento,
            RedirectAttributes redirect) {

        if (nome.isBlank() || especie.isBlank() || raca.isBlank() || dataNascimento == null) {
            redirect.addFlashAttribute("erro", "Preencha todos os campos do pet.");
            return "redirect:/pets/novo";
        }

        service.criarPet(tutorId, nome, especie, raca, dataNascimento);
        redirect.addFlashAttribute("sucesso", "Pet cadastrado com sucesso!");
        return "redirect:/pets";
    }

    // ==========================================
    // AGENDAMENTOS
    // ==========================================

    @GetMapping("/agendamentos")
    public String agendamentos(Model model) {
        model.addAttribute("agendamentos", repository.listarAgendamentos());
        return "agendamentos";
    }

    @GetMapping({"/agendamentos/{id}", "/agendamentos/{id}/editar", "/agendamentos/editar/{id}"})
    public String redirecionarAgendamento(@PathVariable int id) {
        return "redirect:/agendamentos";
    }

    @PostMapping({"/agendamentos/{id}", "/agendamentos/{id}/editar", "/agendamentos/editar/{id}", "/agendamentos/{id}/atualizar", "/agendamentos/atualizar/{id}"})
    public String atualizarAgendamento(
            @PathVariable int id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataAgendamento,
            RedirectAttributes redirect) {

        if (dataAgendamento == null) {
            redirect.addFlashAttribute("erro", "Informe uma data válida para o agendamento.");
            return "redirect:/agendamentos";
        }

        boolean atualizou = repository.atualizarDataAgendamento(id, dataAgendamento);
        if (atualizou) {
            redirect.addFlashAttribute("sucesso", "Data do agendamento alterada com sucesso!");
        } else {
            redirect.addFlashAttribute("erro", "Agendamento não encontrado.");
        }
        return "redirect:/agendamentos";
    }
}
