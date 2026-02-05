-- Inserir artistas iniciais
INSERT INTO artistas (nome, created_at) VALUES
('Serj Tankian', CURRENT_TIMESTAMP),
('Mike Shinoda', CURRENT_TIMESTAMP),
('Michel Teló', CURRENT_TIMESTAMP),
('Guns N'' Roses', CURRENT_TIMESTAMP);

-- Inserir álbuns iniciais
-- Álbuns de Serj Tankian
INSERT INTO albuns (artista_id, nome, created_at) VALUES
((SELECT id FROM artistas WHERE nome = 'Serj Tankian'), 'Harakiri', CURRENT_TIMESTAMP),
((SELECT id FROM artistas WHERE nome = 'Serj Tankian'), 'Black Blooms', CURRENT_TIMESTAMP),
((SELECT id FROM artistas WHERE nome = 'Serj Tankian'), 'The Rough Dog', CURRENT_TIMESTAMP);

-- Álbuns de Mike Shinoda
INSERT INTO albuns (artista_id, nome, created_at) VALUES
((SELECT id FROM artistas WHERE nome = 'Mike Shinoda'), 'The Rising Tied', CURRENT_TIMESTAMP),
((SELECT id FROM artistas WHERE nome = 'Mike Shinoda'), 'Post Traumatic', CURRENT_TIMESTAMP),
((SELECT id FROM artistas WHERE nome = 'Mike Shinoda'), 'Post Traumatic EP', CURRENT_TIMESTAMP),
((SELECT id FROM artistas WHERE nome = 'Mike Shinoda'), 'Where’d You Go', CURRENT_TIMESTAMP);

-- Álbuns de Michel Teló
INSERT INTO albuns (artista_id, nome, created_at) VALUES
((SELECT id FROM artistas WHERE nome = 'Michel Teló'), 'Bem Sertanejo', CURRENT_TIMESTAMP),
((SELECT id FROM artistas WHERE nome = 'Michel Teló'), 'Bem Sertanejo - O Show (Ao Vivo)', CURRENT_TIMESTAMP),
((SELECT id FROM artistas WHERE nome = 'Michel Teló'), 'Bem Sertanejo - (1ª Temporada) - EP', CURRENT_TIMESTAMP);

-- Álbuns de Guns N’ Roses
INSERT INTO albuns (artista_id, nome, created_at) VALUES
((SELECT id FROM artistas WHERE nome = 'Guns N'' Roses'), 'Use Your Illusion I', CURRENT_TIMESTAMP),
((SELECT id FROM artistas WHERE nome = 'Guns N'' Roses'), 'Use Your Illusion II', CURRENT_TIMESTAMP),
((SELECT id FROM artistas WHERE nome = 'Guns N'' Roses'), 'Greatest Hits', CURRENT_TIMESTAMP);

-- Usuário administrador inicial
-- Email: admin@example.com
-- Senha: 123456
INSERT INTO usuarios (nome, email, senha, created_at)
VALUES (
  'Admin',
  'admin@example.com',
  '$2b$12$oMoeGu4Lfs4KA6LmhEPw1eGsIMVVnDdMK0lmX1hhM2c3WvtkJZ7Le',
  CURRENT_TIMESTAMP
);

