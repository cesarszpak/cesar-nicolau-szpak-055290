# Projeto Full Stack – Gestão de Artistas e Álbuns

## Stack Utilizada
- **Java 21 + Spring Boot 3**
- **PostgreSQL**
- **MinIO (API compatível com S3)**
- **React + TypeScript**
- **Docker & Docker Compose**

---

## Arquitetura
- API REST versionada (`/api/v1`)
- Autenticação e autorização via **JWT**
- Front-end desacoplado do back-end
- Comunicação entre camadas via **REST** e **WebSocket**
- Armazenamento de imagens de capas de álbuns utilizando **MinIO**
- Acesso às imagens por meio de **URLs pré-assinadas (presigned URLs)** com tempo de expiração

---

## Decisões Técnicas
- **Spring Boot** foi escolhido pela maturidade do ecossistema, ampla adoção no mercado e facilidade de integração com segurança, WebSocket, JPA, migrations e documentação via OpenAPI.
- **React com TypeScript** foi adotado para garantir tipagem estática, melhor previsibilidade do código, maior manutenibilidade e flexibilidade na componentização da interface.
- O front-end segue uma arquitetura baseada em **Services, Stores e Facades**, utilizando **BehaviorSubject** para gerenciamento de estado, promovendo baixo acoplamento entre a camada de apresentação e a lógica de negócio.
- **MinIO** foi utilizado como solução de armazenamento de objetos compatível com S3, executando localmente via Docker e simulando um cenário real de cloud storage.
- **Docker Compose** é responsável por orquestrar todos os serviços da aplicação (API, banco de dados, MinIO e front-end), garantindo um ambiente padronizado, reproduzível e de fácil execução.

---
