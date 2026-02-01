-- Cria a tabela de capas do álbum
CREATE TABLE IF NOT EXISTS capas_album (
    id BIGSERIAL PRIMARY KEY,
    album_id BIGINT NOT NULL,
    chave VARCHAR(1024) NOT NULL,
    nome_arquivo VARCHAR(255),
    content_type VARCHAR(255),
    tamanho BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_capas_album_albuns
        FOREIGN KEY (album_id) REFERENCES albuns(id) ON DELETE CASCADE
);

-- Índice para consulta por album
CREATE INDEX IF NOT EXISTS idx_capas_album_album_id ON capas_album(album_id);
