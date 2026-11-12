package saep.clinica_veterinaria.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Agendamento {

    private int id;
    private LocalDate data;
    private LocalTime horario;
    private String servico;
    private String status;
    private String observacao;
    private String tutorNome;
    private String tutorTelefone;
    private String tutorEmail;
    private String petNome;
    private String petEspecie;
    private String petRaca;

    public Agendamento() {
    }

    public Agendamento(int id, LocalDate data, LocalTime horario, String servico, String status,
                       String observacao, String tutorNome, String tutorTelefone, String tutorEmail,
                       String petNome, String petEspecie, String petRaca) {
        this.id = id;
        this.data = data;
        this.horario = horario;
        this.servico = servico;
        this.status = status;
        this.observacao = observacao;
        this.tutorNome = tutorNome;
        this.tutorTelefone = tutorTelefone;
        this.tutorEmail = tutorEmail;
        this.petNome = petNome;
        this.petEspecie = petEspecie;
        this.petRaca = petRaca;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
        this.horario = horario;
    }

    public String getServico() {
        return servico;
    }

    public void setServico(String servico) {
        this.servico = servico;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getTutorNome() {
        return tutorNome;
    }

    public void setTutorNome(String tutorNome) {
        this.tutorNome = tutorNome;
    }

    public String getTutorTelefone() {
        return tutorTelefone;
    }

    public void setTutorTelefone(String tutorTelefone) {
        this.tutorTelefone = tutorTelefone;
    }

    public String getTutorEmail() {
        return tutorEmail;
    }

    public void setTutorEmail(String tutorEmail) {
        this.tutorEmail = tutorEmail;
    }

    public String getPetNome() {
        return petNome;
    }

    public void setPetNome(String petNome) {
        this.petNome = petNome;
    }

    public String getPetEspecie() {
        return petEspecie;
    }

    public void setPetEspecie(String petEspecie) {
        this.petEspecie = petEspecie;
    }

    public String getPetRaca() {
        return petRaca;
    }

    public void setPetRaca(String petRaca) {
        this.petRaca = petRaca;
    }
}
