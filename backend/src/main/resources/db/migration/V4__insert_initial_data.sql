-- Inserir tipos iniciais
-- Inserir artistas iniciais
INSERT INTO artistas (nome, created_at) VALUES
('Serj Tankian', CURRENT_TIMESTAMP),
('Mike Shinoda', CURRENT_TIMESTAMP),
('Michel Teló', CURRENT_TIMESTAMP),
('Guns N'' Roses', CURRENT_TIMESTAMP);

-- Inserir álbuns iniciais
-- Álbuns de Serj Tankian
INSERT INTO albuns (artista_id, nome, created_at) VALUES
(1, 'Harakiri', CURRENT_TIMESTAMP),
(1, 'Black Blooms', CURRENT_TIMESTAMP),
(1, 'The Rough Dog', CURRENT_TIMESTAMP);

-- Álbuns de Mike Shinoda
INSERT INTO albuns (artista_id, nome, created_at) VALUES
(2, 'The Rising Tied', CURRENT_TIMESTAMP),
(2, 'Post Traumatic', CURRENT_TIMESTAMP),
(2, 'Post Traumatic EP', CURRENT_TIMESTAMP),
(2, 'Where’d You Go', CURRENT_TIMESTAMP);

-- Álbuns de Michel Teló
INSERT INTO albuns (artista_id, nome, created_at) VALUES
(3, 'Bem Sertanejo', CURRENT_TIMESTAMP),
(3, 'Bem Sertanejo - O Show (Ao Vivo)', CURRENT_TIMESTAMP),
(3, 'Bem Sertanejo - (1ª Temporada) - EP', CURRENT_TIMESTAMP);

-- Álbuns de Guns N’ Roses
INSERT INTO albuns (artista_id, nome, created_at) VALUES
(4, 'Use Your Illusion I', CURRENT_TIMESTAMP),
(4, 'Use Your Illusion II', CURRENT_TIMESTAMP),
(4, 'Greatest Hits', CURRENT_TIMESTAMP);

