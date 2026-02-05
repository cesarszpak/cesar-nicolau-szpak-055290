# PROJETO PRÁTICO - IMPLEMENTAÇÃO FULL STACK SÊNIOR - JAVA + ANGULAR/REACT

API RESTful Full Stack para gerenciamento de artistas e seus álbuns desenvolvida em Java (Spring Boot) com frontend em React/TypeScript. A aplicação oferece autenticação JWT, upload de capas de álbuns via MinIO (S3), sincronização de dados externos e notificações em tempo real via WebSocket.

## Índice

- [Funcionalidades](#funcionalidades)
- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Requisitos](#requisitos)
- [Instalação](#instalação)
  - [Opção 1: Execução com Docker (Recomendado)](#opção-1-execução-com-docker-recomendado)
  - [Opção 2: Instalação Manual](#opção-2-instalação-manual)
- [Configuração](#configuração)
- [Execução](#execução)
- [Documentação da API](#documentação-da-api)
- [Endpoints](#endpoints)
- [Autenticação](#autenticação)
- [Segurança](#segurança)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Testes](#testes)
- [Dados de Exemplo](#dados-de-exemplo)
- [Requisitos Implementados](#requisitos-implementados)
- [Decisões Técnicas](#decisões-técnicas)
- [Licença](#licença)
- [Autor](#autor)

## Funcionalidades

### Backend - Gerenciamento de Dados

**Autenticação e Segurança**
- Autenticação via JWT com expiração a cada 5 minutos
- Renovação automática de tokens
- Proteção contra CORS (apenas domínio autorizado)
- Criptografia de senhas com bcrypt

**Gestão de Artistas**
- CRUD completo de artistas
- Consulta por nome com ordenação alfabética (asc/desc)
- Listagem paginada de artistas
- Atributo de quantidade de álbuns associados

**Gestão de Álbuns**
- CRUD completo de álbuns
- Associação com artistas
- Paginação com suporte a ordenação
- Consultas parametrizadas por artista

**Upload de Imagens**
- Upload de uma ou mais capas de álbum
- Armazenamento em MinIO (API S3 compatível)
- Links pré-assinados com expiração de 30 minutos
- Recuperação automática de imagens via proxy

**Recursos Avançados**
- Health Checks (UP/DOWN) e Probes (liveness/readiness)
- WebSocket para notificações de novos álbuns
- Rate limiting (máximo 10 requisições por minuto por usuário)
- Sincronização com API externa de regionais da Polícia Civil
- Versionamento de endpoints
- Documentação automática com Swagger/OpenAPI
- Migrações automáticas com Flyway

### Frontend - Interface de Usuário

**Autenticação**
- Tela de login com validação
- Renovação automática de token
- Proteção de rotas privadas
- Redirecionamento automático ao deslogar

**Listagem de Artistas**
- Cards responsivos com nome e quantidade de álbuns
- Campo de busca por nome
- Ordenação alfabética (crescente/decrescente)
- Paginação
- Design responsivo mobile-first

**Detalhamento do Artista**
- Exibição completa de informações
- Listagem de álbuns associados
- Visualização de capas de álbuns
- Mensagem quando não há álbuns

**Gerenciamento de Álbuns**
- Listagem paginada com capas
- Criação de novos álbuns
- Edição de informações
- Exclusão com confirmação
- Upload de capas de álbum
- Exibição responsiva de imagens

**Notificações**
- Notificações em tempo real de novos álbuns via WebSocket
- Sistema de toast para feedback visual
- Mensagens de erro e sucesso em português

## Tecnologias

### Backend

**Framework e Linguagem**
- Java 21 LTS
- Spring Boot 3.5.9
- Spring Security para autenticação e autorização
- Spring Data JPA para acesso a dados
- Spring WebSocket para comunicação em tempo real

**Banco de Dados e Persistência**
- PostgreSQL 16 como banco de dados relacional
- Flyway para migrações de banco de dados
- Hibernate como ORM

**Armazenamento de Arquivos**
- MinIO: Servidor S3 compatível para armazenamento de imagens

**Segurança**
- JWT (JSON Web Tokens) para autenticação stateless
- Spring Security para autorização
- CORS configurável

**Documentação**
- SpringDoc OpenAPI 2.0 (Swagger)
- Documentação interativa dos endpoints

**Testes**
- JUnit 5 como framework de testes
- Mockito para mock de dependências
- Spring Boot Test para testes integrados

### Frontend

**Framework e Linguagem**
- React 18 com TypeScript
- Vite como build tool
- React Router v6 para roteamento com lazy loading

**Estilização**
- Tailwind CSS para design responsivo
- CSS customizado para ajustes específicos

**Requisições HTTP**
- Axios para chamadas à API
- Interceptadores para autenticação automática

**Estado e Comunicação**
- RxJS com BehaviorSubject para gerenciamento de estado
- Padrão Facade para abstração de chamadas à API
- WebSocket nativo para notificações em tempo real

**Componentes de UI**
- SweetAlert2 para modais de confirmação
- Toast customizado para notificações

### DevOps e Containerização

**Orquestração**
- Docker para containerização de serviços
- Docker Compose para orquestração local

**Imagens Base**
- node:20-alpine para build do frontend
- maven:3.9.6-eclipse-temurin-21 para build do backend
- nginx:alpine para servir frontend em produção
- eclipse-temurin:21-jre para execução do backend
- postgres:16 para banco de dados
- minio/minio:latest para armazenamento

## Arquitetura

### Visão Geral

A aplicação segue uma arquitetura em camadas com separação clara de responsabilidades:

```
Frontend (React/TypeScript)
          |
   Nginx (Proxy Reverso)
          |
Backend API (Spring Boot)
    |         |         |
 Services   Security   WebSocket
    |         |         |
   JPA        JWT     Notificações
    |
PostgreSQL + MinIO
```

### Backend - Estrutura de Camadas

**Controller Layer (Controladores HTTP)**
- Responsáveis por receber requisições HTTP
- Validação de entrada
- Mapeamento para serviços de negócio
- Devolução de respostas formatadas

**Service Layer (Serviços de Negócio)**
- Implementação de regras de negócio
- Orquestração de operações complexas
- Transações de banco de dados
- Notificações e integrações

**Repository Layer (Acesso a Dados)**
- Interfaces JPA para operações de banco de dados
- Queries customizadas quando necessário
- Abstração do banco de dados

**Domain Models (Entidades)**
- Usuario: Usuário da aplicação
- Artista: Artista ou banda
- Album: Álbum de um artista
- CapaAlbum: Capa de um álbum (imagem)
- RefreshToken: Token de renovação JWT
- Regional: Regional da Polícia Civil

### Frontend - Padrões Implementados

**Padrão Facade**
- AuthFacade: Abstração de autenticação
- Simplifica consumo de serviços complexos
- Gerencia estado com BehaviorSubject

**RxJS e BehaviorSubject**
- Gerenciamento de estado reativo
- Observable para notificações de autenticação
- Melhor separação de responsabilidades

**Repository Pattern**
- Abstração do banco de dados via JPA
- Facilita testes e mudanças de banco de dados

**JWT Token Refresh**
- Tokens curtos (5 minutos)
- Refresh tokens para renovação
- Renovação automática no frontend

## Requisitos

### Para Execução com Docker (Recomendado)

**Software**
- Docker: Download e instalação
- Docker Compose: Download e instalação
- Git: Para clonar o repositório

### Para Execução Manual

**Software**
- Java 21 JDK
- Maven 3.9.6 ou superior
- Node.js 20 ou superior
- PostgreSQL 16 ou superior
- MinIO
- Git

**Portas Necessárias**
- 3000: Frontend (Nginx)
- 8080: Backend API
- 5432: PostgreSQL
- 9000-9001: MinIO (S3 + Console)

## Instalação

### Opção 1: Execução com Docker (Recomendado)

A forma mais rápida e segura de executar o projeto é usando Docker e Docker Compose.

#### Passos

**1. Clonar o Repositório**

```bash
git clone https://github.com/cesarszpak/cesar-nicolau-szpak-055290.git
cd cesar-nicolau-szpak-055290
```

**2. Iniciar os Serviços**

```bash
docker compose up -d
```

Este comando irá:
- Criar rede interna entre containers
- Iniciar banco de dados PostgreSQL
- Iniciar serviço MinIO
- Executar migrações Flyway automaticamente
- Compilar e iniciar API Spring Boot
- Compilar e iniciar Frontend React
- Expor Frontend na porta 3000
- Expor API na porta 8080

**3. Verificar Status dos Containers**

```bash
docker compose ps
```

Todos os containers devem estar com STATUS "Up".

**4. Acessar a Aplicação**

- Frontend: http://localhost:3000
- API Swagger: http://localhost:8080/swagger-ui/index.html
- MinIO Console: http://localhost:9001 (usuario: minioadmin, senha: minioadmin)

**5. Dados de Acesso Padrão**

Após a inicialização, o banco é populado com dados de exemplo:

```
Usuario de Teste:
Email: admin@example.com
Senha: 123456
```

#### Comandos Úteis Docker

```bash
# Executar testes
docker compose run --rm api-test

# Ver logs da API
docker compose logs -f api

# Ver logs do frontend
docker compose logs -f frontend

# Ver logs do banco de dados
docker compose logs -f db

# Parar os serviços
docker compose down

# Parar e remover volumes (dados do banco)
docker compose down -v

# Rebuild após mudanças no código
docker compose build --no-cache
docker compose up -d

# Executar comandos dentro do container da API
docker compose exec api bash
```

### Opção 2: Instalação Manual

Se preferir executar localmente sem Docker:

#### 1. Clonar o Repositório

```bash
git clone https://github.com/seu-usuario/artistas-api.git
cd artistas-api
```

#### 2. Configurar Backend

**2.1 Instalar Dependências Maven**

```bash
cd backend
mvn clean install
```

**2.2 Configurar PostgreSQL**

Certifique-se de que PostgreSQL está instalado e rodando:

```sql
CREATE DATABASE artistas;
CREATE USER artistas WITH PASSWORD 'artistas';
GRANT ALL PRIVILEGES ON DATABASE artistas TO artistas;
```

**2.3 Configurar MinIO (Local)**

Baixe MinIO e execute:

```bash
minio server /data
```

MinIO estará disponível em http://localhost:9000

**2.4 Executar Backend**

```bash
cd backend
mvn spring-boot:run
```

A API estará disponível em http://localhost:8080

#### 3. Configurar Frontend

**3.1 Instalar Dependências Node**

```bash
cd frontend
npm install
```

**3.2 Executar em Modo Desenvolvimento**

```bash
npm run dev
```

Frontend estará disponível em http://localhost:5173

**3.3 Build para Produção**

```bash
npm run build
```

Arquivos compilados estarão em dist/

## Configuração

### Variáveis de Ambiente Backend

O backend utiliza application.yaml com valores pré-configurados. Para produção, configure em application.yaml ou via variáveis de ambiente:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/artistas
    username: artistas
    password: artistas
  
  jpa:
    hibernate:
      ddl-auto: validate
  
  flyway:
    enabled: true
    validate-on-migrate: true

jwt:
  secret: sua-chave-secreta-com-minimo-256-bits
  issuer: artistas-api
  expiration: 300000  # 5 minutos em milissegundos

minio:
  endpoint: http://localhost:9000
  accessKey: minioadmin
  secretKey: minioadmin
  bucketName: capas

frontend:
  url: http://localhost:3000
```

### Variáveis de Ambiente Frontend

Configure em frontend/.env:

```
VITE_API_URL=http://localhost:8080
VITE_API_VERSION=v1
```

### Migrações Flyway

As migrações são executadas automaticamente na inicialização:

```
V1__init.sql
V2__create_usuarios.sql
V3__create_albuns.sql
V4__insert_initial_data.sql
V5__create_refresh_tokens.sql
V6__create_capas_album.sql
V7__create_regionais_table.sql
```

## Execução

### Com Docker (Recomendado)

```bash
# Iniciar tudo
docker compose up -d

# Parar tudo
docker compose down
```

### Manualmente

**Terminal 1 - Backend**

```bash
cd backend
mvn spring-boot:run
```

**Terminal 2 - Frontend**

```bash
cd frontend
npm run dev
```

**Terminal 3 - MinIO (se não usar Docker)**

```bash
minio server /data
```

## Documentação da API

### Swagger/OpenAPI

A documentação interativa está disponível em:

```
http://localhost:8080/swagger-ui/index.html
```

## Endpoints

### Autenticação (Públicos)

**POST /api/usuarios**
- Criar novo usuário (registro)
- Body: `{ "email": "string", "senha": "string", "nome": "string" }`
- Response: `{ "id": "number", "email": "string", "nome": "string" }`

**POST /api/usuarios/login**
- Autenticar usuário
- Body: `{ "email": "string", "senha": "string" }`
- Response: `{ "token": "JWT", "refreshToken": "string" }`

**POST /api/usuarios/refresh**
- Renovar token JWT
- Body: `{ "refreshToken": "string" }`
- Response: `{ "token": "JWT", "refreshToken": "string" }`

### Artistas (Requer Autenticação)

**GET /api/artistas**
- Listar artistas com paginação
- Query Params:
  - page: número da página (padrão: 0)
  - size: tamanho da página (padrão: 10)
  - nome: filtrar por nome (opcional)
  - sort: campo para ordenação (padrão: nome,asc)
- Response: `{ "content": [...], "totalElements": "number", "totalPages": "number" }`

**GET /api/artistas/:id**
- Obter artista específico
- Response: `{ "id": "number", "nome": "string", "albumCount": "number", "albuns": [...] }`

**POST /api/artistas**
- Criar novo artista
- Body: `{ "nome": "string" }`
- Response: `{ "id": "number", "nome": "string" }`

**PUT /api/artistas/:id**
- Atualizar artista
- Body: `{ "nome": "string" }`
- Response: `{ "id": "number", "nome": "string" }`

**DELETE /api/artistas/:id**
- Deletar artista
- Response: Sem conteúdo (204)

### Álbuns (Requer Autenticação)

**GET /api/albuns**
- Listar álbuns com paginação
- Query Params:
  - page: número da página (padrão: 0)
  - size: tamanho da página (padrão: 10)
  - artistaId: filtrar por artista (opcional)
  - sort: campo para ordenação (padrão: nome,asc)
- Response: `{ "content": [...], "totalElements": "number" }`

**GET /api/albuns/:id**
- Obter álbum específico
- Response: `{ "id": "number", "nome": "string", "artistaId": "number", "artistaNome": "string", "capas": [...] }`

**POST /api/albuns**
- Criar novo álbum
- Body: `{ "nome": "string", "artistaId": "number" }`
- Response: `{ "id": "number", "nome": "string", "artistaId": "number" }`

**PUT /api/albuns/:id**
- Atualizar álbum
- Body: `{ "nome": "string", "artistaId": "number" }`
- Response: `{ "id": "number", "nome": "string" }`

**DELETE /api/albuns/:id**
- Deletar álbum
- Response: Sem conteúdo (204)

### Capas de Álbum (Requer Autenticação)

**GET /api/capas/albumId/:albumId**
- Listar capas de um álbum
- Response: `[{ "id": "number", "nomeArquivo": "string", "url": "string", "tamanho": "number" }]`

**POST /api/capas/:albumId**
- Upload de uma ou mais capas
- Content-Type: multipart/form-data
- Form: files: File[]
- Response: `[{ "id": "number", "nomeArquivo": "string", "url": "string" }]`

**GET /api/capas/:id/conteudo**
- Obter conteúdo da capa (imagem)
- Response: Arquivo binário

**DELETE /api/capas/:id**
- Deletar capa
- Response: Sem conteúdo (204)

### Regionais (Requer Autenticação)

**GET /api/regionais**
- Listar regionais sincronizadas
- Query Params:
  - ativo: filtrar por status (true/false, opcional)
- Response: `[{ "id": "number", "nome": "string", "ativo": "boolean" }]`

**POST /admin/regionais/sincronizar**
- Sincronizar regionais com API externa (Admin)
- Response: `{ "sincronizados": "number", "inativos": "number" }`

### Health Check

**GET /actuator/health**
- Verificar saúde da aplicação
- Response: `{ "status": "UP" }`

**GET /actuator/health/liveness**
- Liveness probe para Kubernetes
- Response: `{ "status": "UP" }`

**GET /actuator/health/readiness**
- Readiness probe para Kubernetes
- Response: `{ "status": "UP" }`

## Autenticação

### Fluxo de Autenticação

1. **Registro de Novo Usuário**

```bash
curl -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "email": "novo@usuario.com",
    "senha": "senha123",
    "nome": "Novo Usuario"
  }'
```

2. **Login**

```bash
curl -X POST http://localhost:8080/api/usuarios/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "novo@usuario.com",
    "senha": "senha123"
  }'
```

Resposta:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "abc123def456..."
}
```

3. **Usar Token em Requisições**

```bash
curl -X GET http://localhost:8080/api/artistas \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

4. **Renovar Token (quando expirar)**

```bash
curl -X POST http://localhost:8080/api/usuarios/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "abc123def456..."
  }'
```

### Validade do Token

- Token JWT: 5 minutos
- Refresh Token: 24 horas
- Frontend renova automaticamente antes de expirar

## Segurança

### Medidas Implementadas

**Autenticação e Autorização**
- JWT com assinatura HS256
- Refresh tokens armazenados em banco de dados
- Validação de propriedade de recursos
- CORS restrito ao domínio configurado

**Criptografia**
- Senhas armazenadas com bcrypt (cost >= 10)
- Tokens assinados e validados
- Links pré-assinados do MinIO com expiração

**Rate Limiting**
- Máximo 10 requisições por minuto por usuário
- Controle de requisições de notificação
- Resposta 429 Too Many Requests

**Validação**
- Validação de entrada em todos os endpoints
- Tratamento centralizado de erros
- Logs estruturados

**CORS**
- Apenas domínio configurado pode acessar
- Padrão: http://localhost:3000
- Customizável via variável de ambiente

### Boas Práticas de Segurança

Para produção:

1. Use HTTPS em vez de HTTP
2. Gere chave JWT com pelo menos 256 bits
3. Configure senha forte para MinIO
4. Defina senha forte para PostgreSQL
5. Restrinja CORS apenas aos domínios necessários
6. Implemente logging e monitoring
7. Use secrets manager para credenciais
8. Configure firewall adequadamente
9. Mantenha dependências atualizadas
10. Habilite HTTPS em MinIO

## Estrutura do Projeto

```
artistas-api/
├── backend/                        # API Java Spring Boot
│   ├── src/
│   │   ├── main/java/br/com/seuorg/artistas_api/
│   │   │   ├── controller/         # Controladores REST
│   │   │   ├── service/            # Serviços de negócio
│   │   │   ├── domain/             # Entidades JPA
│   │   │   ├── repository/         # Repositórios JPA
│   │   │   ├── config/             # Configurações
│   │   │   ├── security/           # Segurança
│   │   │   ├── websocket/          # WebSocket
│   │   │   ├── exception/          # Tratamento de erros
│   │   │   └── ArtistasApiApplication.java
│   │   └── resources/
│   │       ├── application.yaml    # Configurações
│   │       └── db/migration/       # Scripts Flyway
│   ├── pom.xml
│   ├── Dockerfile
├── frontend/                        # Aplicação React TypeScript
│   ├── src/
│   │   ├── components/             # Componentes React
│   │   ├── pages/                  # Páginas da aplicação
│   │   ├── services/               # Serviços de API
│   │   ├── facades/                # Padrão Facade
│   │   ├── App.tsx
│   │   ├── main.tsx
│   │   └── index.css
│   ├── package.json
│   ├── Dockerfile
│   └── nginx.conf
├── docker-compose.yml
├── README.md                        # Este arquivo
└── [Outros documentos de referência]
```

## Testes

### Backend - Testes Unitários e Integrados

#### Executar Todos os Testes

```bash
# Com Docker
docker compose run --rm api-test

# Manualmente
cd backend
mvn test
```

#### Cobertura de Testes

```bash
# Com relatório HTML
cd backend
mvn test jacoco:report
# Abrir target/site/jacoco/index.html
```

#### Testes Implementados

- AlbumServiceTest: Testes de negócio de álbuns
- AlbumServiceNotificationTest: Testes de notificação
- AlbumServiceNotificationRateLimitTest: Testes de rate limit
- ArtistaServiceTest: Testes de artistas
- CapaAlbumServiceTest: Testes de capas
- UsuarioServiceTest: Testes de autenticação
- HealthPublicTest: Testes de health check
- E mais 20+ testes de controladores e integração

**Total: 30 testes** com cobertura de casos de sucesso e erro

### Teste de Integração Completa

**1. Testar Autenticação**

```bash
# Registrar novo usuário
curl -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{"email":"teste@test.com","senha":"senha123","nome":"Teste"}'

# Fazer login
curl -X POST http://localhost:8080/api/usuarios/login \
  -H "Content-Type: application/json" \
  -d '{"email":"teste@test.com","senha":"senha123"}'
```

**2. Testar CRUD de Artista**

```bash
TOKEN="seu_token_aqui"

# Criar artista
curl -X POST http://localhost:8080/api/artistas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"nome":"Novo Artista"}'

# Listar artistas
curl -X GET http://localhost:8080/api/artistas \
  -H "Authorization: Bearer $TOKEN"

# Obter artista específico
curl -X GET http://localhost:8080/api/artistas/1 \
  -H "Authorization: Bearer $TOKEN"
```

**3. Testar Upload de Capa**

```bash
TOKEN="seu_token_aqui"

# Upload de imagem
curl -X POST http://localhost:8080/api/capas/1 \
  -H "Authorization: Bearer $TOKEN" \
  -F "files=@/caminho/para/imagem.jpg"
```

**4. Testar Frontend**

- Acesse http://localhost:3000
- Faça login com credenciais de teste
- Navegue pela interface
- Teste criação, edição e exclusão
- Verifique notificações de novos álbuns

## Dados de Exemplo

### Artistas Pré-Carregados

Após inicializar, os seguintes artistas e álbuns estarão disponíveis:

```
Serj Tankian
- Harakiri
- Black Blooms
- The Rough Dog

Mike Shinoda
- The Rising Tied
- Post Traumatic
- Post Traumatic EP
- Where'd You Go

Michel Teló
- Bem Sertanejo
- Bem Sertanejo - O Show (Ao Vivo)
- Bem Sertanejo - (1ª Temporada) - EP

Guns N' Roses
- Use Your Illusion I
- Use Your Illusion II
- Greatest Hits
```

## Requisitos Implementados

### Backend

Segurança
- CORS restringido ao domínio autorizado
- Autenticação JWT com expiração de 5 minutos
- Renovação de tokens via refresh token
- Proteção contra acesso não autorizado

CRUD e Operações
- Verbos POST, PUT, GET, DELETE
- Paginação em listagem de álbuns
- Consultas parametrizadas (filtro por artista)
- Ordenação alfabética ascendente/descendente
- Exposição de álbuns por artista

Upload de Imagens
- Upload de uma ou mais capas de álbum
- Armazenamento em MinIO (S3)
- Links pré-assinados com expiração de 30 minutos
- Recuperação de imagens via proxy

Infraestrutura
- Versionamento de endpoints
- Flyway Migrations para criação de tabelas
- OpenAPI/Swagger com documentação interativa
- Docker Compose com todos os serviços

Recursos Sênior
- Health Checks (UP/DOWN) e Probes (liveness/readiness)
- Testes unitários (30 testes total)
- WebSocket com notificações de novos álbuns
- Rate limiting (10 req/min por usuário)
- Sincronização com API externa de regionais
- Padrão Repository com JPA

### Frontend

Interface Básica
- Tela de login com autenticação
- Listagem de artistas com paginação
- Campo de busca por nome
- Ordenação ascendente/descendente
- Cards responsivos com nome e nº de álbuns

Detalhamento
- Tela de artista com álbuns associados
- Exibição de informações completas
- Mensagem quando não há álbuns
- Visualização de capas de álbuns

Gestão
- Formulário de cadastro de artista
- Formulário de cadastro de álbum
- Edição de registros
- Exclusão com confirmação
- Upload de capas de álbum

Arquitetura e Estado
- Componentização modular
- Padrão Facade para abstração
- RxJS com BehaviorSubject
- TypeScript para type safety
- Lazy Loading de rotas
- Design responsivo com Tailwind
- Paginação funcional

Recursos Avançados
- Notificações em tempo real (WebSocket)
- Renovação automática de token
- Mensagens em português
- Testes unitários

Requisitos de Qualidade
- Clean Code e estrutura modular
- Commits organizado e descritivos
- Documentação clara no README
- Histórico de commits bem estruturado
- Instruções detalhadas de execução
- Arquitetura documentada
- Testes implementados
- Docker Compose funcional

## Decisões Técnicas

### Por que Java + Spring Boot?

- Ecossistema robusto e produção-ready
- Spring Security com suporte nativo a JWT
- Spring Data JPA para abstração de BD
- Spring WebSocket para comunicação em tempo real
- Ampla comunidade e documentação

### Por que React + TypeScript?

- Componentes reutilizáveis e modulares
- TypeScript para type safety
- Rich ecosystem de bibliotecas
- Excelente performance e user experience
- Fácil integração com APIs REST

### Por que MinIO?

- S3-compatível, facilita migração futura
- Fácil de usar em ambiente local
- Escalável para produção
- Suporte a presigned URLs

### Por que PostgreSQL?

- Banco relacional robusto
- Suporte a JSONB para dados complexos
- Excelente performance e reliability
- Padrão em produção

### Por que Docker Compose?

- Facilita onboarding de novos desenvolvedores
- Ambiente idêntico entre dev e prod
- Orquestração simples para pequena escala
- Escalável para Kubernetes se necessário

## Licença

Este projeto está licenciado sob a licença MIT.

## Autor

Este projeto foi desenvolvido por [Cesar Nicolau Szpak](https://github.com/cesarszpak/cesar-nicolau-szpak-055290.git). N° Inscrição: 16424. Vaga: Engenheiro da Computação - Sênior.