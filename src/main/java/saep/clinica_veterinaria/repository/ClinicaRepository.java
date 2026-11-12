package saep.clinica_veterinaria.repository;

import saep.clinica_veterinaria.model.Agendamento;
import saep.clinica_veterinaria.model.Pet;
import saep.clinica_veterinaria.model.Tutor;
import saep.clinica_veterinaria.model.Usuario;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ClinicaRepository {

    private final JdbcTemplate jdbc;

    public ClinicaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ==========================================
    // USUARIOS
    // ==========================================

    public Optional<Usuario> buscarUsuario(String email) {
        String sql = "SELECT * FROM usuarios WHERE lower(email) = lower(?)";
        List<Usuario> lista = jdbc.query(sql, (rs, rowNum) -> {
            Usuario u = new Usuario();
            u.setId(rs.getInt("id"));
            u.setNome(rs.getString("nome"));
            u.setEmail(rs.getString("email"));
            try {
                u.setSenha(rs.getString("senha"));
            } catch (Exception e) {
                u.setSenha(rs.getString("senha_hash"));
            }
            u.setPerfil(rs.getString("perfil"));
            return u;
        }, email);

        if (lista.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(lista.get(0));
    }

    // ==========================================
    // TUTORES
    // ==========================================

    public List<Tutor> listarTutores(String termo) {
        String busca = "%" + termo.trim().toLowerCase() + "%";
        String sql = """
                SELECT id, nome, telefone, email
                FROM tutores
                WHERE lower(nome) LIKE ? OR lower(email) LIKE ? OR lower(telefone) LIKE ?
                ORDER BY nome
                """;
        return jdbc.query(sql, (rs, rowNum) -> {
            Tutor t = new Tutor();
            t.setId(rs.getInt("id"));
            t.setNome(rs.getString("nome"));
            t.setTelefone(rs.getString("telefone"));
            t.setEmail(rs.getString("email"));
            return t;
        }, busca, busca, busca);
    }

    public Optional<Tutor> buscarTutor(int id) {
        String sql = "SELECT id, nome, telefone, email FROM tutores WHERE id = ?";
        List<Tutor> lista = jdbc.query(sql, (rs, rowNum) -> {
            Tutor t = new Tutor();
            t.setId(rs.getInt("id"));
            t.setNome(rs.getString("nome"));
            t.setTelefone(rs.getString("telefone"));
            t.setEmail(rs.getString("email"));
            return t;
        }, id);

        if (lista.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(lista.get(0));
    }

    public void inserirTutor(String nome, String cpfCriptografado, String telefone, String email) {
        String sql = "INSERT INTO tutores (nome, cpf_criptografado, telefone, email) VALUES (?, ?, ?, ?)";
        jdbc.update(sql, nome, cpfCriptografado, telefone, email);
    }

    public boolean atualizarTutor(int id, String nome, String telefone, String email) {
        String sql = "UPDATE tutores SET nome = ?, telefone = ?, email = ? WHERE id = ?";
        int linhas = jdbc.update(sql, nome, telefone, email, id);
        return linhas == 1;
    }

    public List<Tutor> listarTutoresParaSelecao() {
        String sql = "SELECT id, nome, telefone, email FROM tutores ORDER BY nome";
        return jdbc.query(sql, (rs, rowNum) -> {
            Tutor t = new Tutor();
            t.setId(rs.getInt("id"));
            t.setNome(rs.getString("nome"));
            t.setTelefone(rs.getString("telefone"));
            t.setEmail(rs.getString("email"));
            return t;
        });
    }

    // ==========================================
    // PETS
    // ==========================================

    public List<Pet> listarPets() {
        String sql = """
                SELECT p.id, p.nome, p.especie, p.raca, p.data_nascimento, t.nome AS tutor_nome
                FROM pets p
                JOIN tutores t ON t.id = p.tutor_id
                ORDER BY p.nome
                """;
        return jdbc.query(sql, (rs, rowNum) -> {
            Pet p = new Pet();
            p.setId(rs.getInt("id"));
            p.setNome(rs.getString("nome"));
            p.setEspecie(rs.getString("especie"));
            p.setRaca(rs.getString("raca"));
            p.setDataNascimento(rs.getObject("data_nascimento", java.time.LocalDate.class));
            p.setTutorNome(rs.getString("tutor_nome"));
            return p;
        });
    }

    public void inserirPet(int tutorId, String nome, String especie, String raca, java.time.LocalDate nascimento) {
        String sql = "INSERT INTO pets (tutor_id, nome, especie, raca, data_nascimento) VALUES (?, ?, ?, ?, ?)";
        jdbc.update(sql, tutorId, nome, especie, raca, Date.valueOf(nascimento));
    }

    // ==========================================
    // AGENDAMENTOS
    // ==========================================

    public List<Agendamento> listarAgendamentos() {
        String sql = """
                SELECT a.id, a.data_agendamento, a.hora_agendamento, a.servico,
                       a.status, a.observacao, t.nome AS tutor_nome,
                       t.telefone AS tutor_telefone, t.email AS tutor_email,
                       p.nome AS pet_nome, p.especie AS pet_especie, p.raca AS pet_raca
                FROM agendamentos a
                JOIN tutores t ON t.id = a.tutor_id
                JOIN pets p ON p.id = a.pet_id
                ORDER BY a.data_agendamento, a.hora_agendamento
                """;
        return jdbc.query(sql, (rs, rowNum) -> {
            Agendamento ag = new Agendamento();
            ag.setId(rs.getInt("id"));
            ag.setData(rs.getObject("data_agendamento", java.time.LocalDate.class));
            ag.setHorario(rs.getObject("hora_agendamento", java.time.LocalTime.class));
            ag.setServico(rs.getString("servico"));
            ag.setStatus(rs.getString("status"));
            ag.setObservacao(rs.getString("observacao"));
            ag.setTutorNome(rs.getString("tutor_nome"));
            ag.setTutorTelefone(rs.getString("tutor_telefone"));
            ag.setTutorEmail(rs.getString("tutor_email"));
            ag.setPetNome(rs.getString("pet_nome"));
            ag.setPetEspecie(rs.getString("pet_especie"));
            ag.setPetRaca(rs.getString("pet_raca"));
            return ag;
        });
    }

    public boolean atualizarDataAgendamento(int id, java.time.LocalDate data) {
        String sql = "UPDATE agendamentos SET data_agendamento = ? WHERE id = ?";
        int linhas = jdbc.update(sql, Date.valueOf(data), id);
        return linhas == 1;
    }

    // ==========================================
    // CONTAGENS (TELA INICIAL)
    // ==========================================

    public Map<String, Integer> contarRegistros() {
        Map<String, Integer> mapa = new java.util.HashMap<>();
        mapa.put("tutores", 0);
        mapa.put("pets", 0);
        mapa.put("agendamentos", 0);
        try {
            String sql = """
                    SELECT
                        (SELECT COUNT(*) FROM tutores) AS tutores,
                        (SELECT COUNT(*) FROM pets) AS pets,
                        (SELECT COUNT(*) FROM agendamentos) AS agendamentos
                    """;
            return jdbc.queryForObject(sql, (rs, rowNum) -> {
                Map<String, Integer> m = new java.util.HashMap<>();
                m.put("tutores", rs.getInt("tutores"));
                m.put("pets", rs.getInt("pets"));
                m.put("agendamentos", rs.getInt("agendamentos"));
                return m;
            });
        } catch (Exception e) {
            return mapa;
        }
    }
}
