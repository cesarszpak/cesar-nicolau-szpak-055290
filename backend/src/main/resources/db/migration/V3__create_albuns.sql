CREATE TABLE albuns (
    id BIGSERIAL PRIMARY KEY,
    artista_id BIGINT NOT NULL,
    nome VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_albuns_artistas
        FOREIGN KEY (artista_id) REFERENCES artistas(id)
);
