CREATE TABLE regional (
    id SERIAL PRIMARY KEY,
    external_id INTEGER,
    nome VARCHAR(200) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_regional_external_id ON regional (external_id);
