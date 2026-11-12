-- Script de Criação e Carga Inicial do Banco de Dados
CREATE DATABASE saep_db

CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(128) NOT NULL,
    email VARCHAR(128) NOT NULL,
    senha VARCHAR(128) NOT NULL,
    perfil VARCHAR(128) NOT NULL CHECK (perfil IN ('admin', 'veterinario', 'recepcionista')),
    criado_em TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tutores (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(128) NOT NULL,
    cpf_criptografado VARCHAR(128) NOT NULL,
    telefone VARCHAR(128) NOT NULL,
    email VARCHAR(128) NOT NULL,
    criado_em TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pets (
    id SERIAL PRIMARY KEY,
    tutor_id INTEGER NOT NULL REFERENCES tutores(id) ON DELETE CASCADE,
    nome VARCHAR(128) NOT NULL,
    especie VARCHAR(128) NOT NULL,
    raca VARCHAR(128) NOT NULL,
    data_nascimento DATE NOT NULL,
    cadastrado_em TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE agendamentos (
    id SERIAL PRIMARY KEY,
    tutor_id INTEGER NOT NULL REFERENCES tutores(id),
    pet_id INTEGER NOT NULL REFERENCES pets(id),
    usuario_id INTEGER NOT NULL REFERENCES usuarios(id),
    data_agendamento DATE NOT NULL,
    hora_agendamento TIME NOT NULL,
    servico VARCHAR(128) NOT NULL,
    status VARCHAR(128) NOT NULL DEFAULT 'AGENDADO' CHECK (status IN ('AGENDADO', 'CONCLUIDO', 'CANCELADO')),
    observacao TEXT,
    criado_em TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- CARGA INICIAL DE DADOS
-- Apenas o CPF é armazenado de forma criptografada; a senha não é criptografada.
INSERT INTO usuarios (id, nome, email, senha, perfil) VALUES
(1, 'Dra. Camila Alencar', 'camila.vet@vetcare.com', '123456', 'veterinario'),
(2, 'Lucas Fernandes', 'lucas.recepcao@vetcare.com', '123456', 'recepcionista'),
(3, 'Mariana Rocha', 'gestao@vetcare.com', '123456', 'admin');

INSERT INTO tutores (id, nome, cpf_criptografado, telefone, email) VALUES
(1, 'Carlos Eduardo Silva', 'u6P8bE2VdG8rS1k9xL4q+w==', '(11) 98765-4321', 'carlos.silva@email.com'),
(2, 'Beatriz Souza Martins', 'z8K9jF3WdH9sT2l0yM5r+x==', '(11) 97654-3210', 'beatriz.martins@email.com'),
(3, 'Fernando Henrique Lima', 'w7L0kG4XeI0tU3m1zN6s+y==', '(19) 99123-4567', 'fernando.lima@email.com');

INSERT INTO pets (id, tutor_id, nome, especie, raca, data_nascimento) VALUES
(1, 1, 'Thor', 'Canina', 'Golden Retriever', '2021-05-10'),
(2, 2, 'Mia', 'Felina', 'Siamês', '2022-08-15'),
(3, 3, 'Bob', 'Canina', 'Bulldog Francês', '2020-02-20');

INSERT INTO agendamentos (tutor_id, pet_id, usuario_id, data_agendamento, hora_agendamento, servico, status, observacao) VALUES
(1, 1, 1, '2024-11-15', '09:30:00', 'Consulta Geral e Vacinação Anual', 'CONCLUIDO', 'Animal dócil e com carteira atualizada'),
(2, 2, 1, '2025-06-20', '14:00:00', 'Profilaxia Dentária e Tartarectomia', 'CONCLUIDO', 'Exames pré-operatórios normais'),
(3, 3, 2, '2026-03-10', '10:30:00', 'Check-up Preventivo e Exame de Sangue', 'AGENDADO', 'Paciente alérgico a dipirona');

-- Ajusta as sequências para os próximos inserts não colidirem
SELECT setval('usuarios_id_seq', (SELECT MAX(id) FROM usuarios));
SELECT setval('tutores_id_seq', (SELECT MAX(id) FROM tutores));
SELECT setval('pets_id_seq', (SELECT MAX(id) FROM pets));
SELECT setval('agendamentos_id_seq', (SELECT MAX(id) FROM agendamentos));