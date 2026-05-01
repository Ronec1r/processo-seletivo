CREATE TABLE solicitante (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cpf_cnpj VARCHAR(18) UNIQUE NOT NULL
);

CREATE TABLE categoria (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

CREATE TABLE solicitacao (
    id BIGSERIAL PRIMARY KEY,
    solicitante_id BIGINT NOT NULL,
    categoria_id BIGINT NOT NULL,
    descricao TEXT NOT NULL,
    valor DECIMAL(10, 2) NOT NULL,
    data_solicitacao TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_solicitante FOREIGN KEY (solicitante_id) REFERENCES solicitante (id),
    CONSTRAINT fk_categoria FOREIGN KEY (categoria_id) REFERENCES categoria (id)
);