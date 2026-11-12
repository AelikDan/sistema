package saep.clinica_veterinaria.service;

import saep.clinica_veterinaria.model.Usuario;
import saep.clinica_veterinaria.repository.ClinicaRepository;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class ClinicaService {

    private final ClinicaRepository repository;
    private final CpfEncryptionService cpfEncryption;
    private final PasswordService passwordService;

    public ClinicaService(
            ClinicaRepository repository,
            CpfEncryptionService cpfEncryption,
            PasswordService passwordService) {
        this.repository = repository;
        this.cpfEncryption = cpfEncryption;
        this.passwordService = passwordService;
    }

    public boolean autenticar(String email, String senha) {
        Optional<Usuario> optUsuario = repository.buscarUsuario(email);
        if (optUsuario.isEmpty()) {
            return false;
        }
        Usuario usuario = optUsuario.get();
        return passwordService.confere(senha, usuario.getSenha());
    }

    public Usuario usuario(String email) {
        return repository.buscarUsuario(email).orElseThrow();
    }

    public void criarTutor(String nome, String cpf, String telefone, String email) {
        String cpfCriptografado = cpfEncryption.criptografar(cpf.trim());
        repository.inserirTutor(nome.trim(), cpfCriptografado, telefone.trim(), email.trim());
    }

    public void criarPet(int tutorId, String nome, String especie, String raca, LocalDate nascimento) {
        repository.inserirPet(tutorId, nome.trim(), especie.trim(), raca.trim(), nascimento);
    }

    public Map<String, Integer> contagens() {
        return repository.contarRegistros();
    }
}
