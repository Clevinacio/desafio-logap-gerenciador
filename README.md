# 🏢 Sistema Gerenciador de Pedidos - LogAp Challenge

Um sistema completo de gerenciamento de pedidos e vendas desenvolvido para empresas que precisam controlar seu fluxo de vendas, estoque e relacionamento com clientes de forma eficiente e organizada.

## 🎯 Sobre o Projeto

Este sistema foi desenvolvido como resposta ao desafio técnico da LogAp, implementando uma solução completa para gerenciamento de pedidos com as seguintes características principais:

- **Sistema de autenticação seguro** com JWT
- **Controle de acesso baseado em roles** (Administrador, Cliente, Vendedor)
- **Gestão completa de produtos e estoque**
- **Sistema de pedidos com workflow de aprovação**
- **Dashboard gerencial com métricas e relatórios**
- **Interface moderna e responsiva**

## 🚀 Tecnologias Utilizadas

### Backend
- **Java 21** - Linguagem de programação principal
- **Spring Boot 3.5.0** - Framework principal
- **Spring Security** - Autenticação e autorização
- **Spring Data JPA** - Persistência de dados
- **PostgreSQL** - Banco de dados principal
- **Flyway** - Versionamento de banco de dados
- **JWT (jsonwebtoken)** - Autenticação stateless
- **Maven** - Gerenciamento de dependências
- **Lombok** - Redução de boilerplate
- **JUnit 5** - Testes unitários
- **Testcontainers** - Testes de integração
- **Logback** - Sistema de logs estruturados

### Frontend
- **React 19** - Biblioteca para interface de usuário
- **TypeScript** - Tipagem estática
- **Vite** - Build tool e dev server
- **Tailwind CSS 4.1.8** - Framework de estilos
- **React Router Dom** - Roteamento
- **Radix UI** - Componentes acessíveis
- **Axios** - Cliente HTTP
- **Recharts** - Gráficos e visualizações
- **React Hot Toast** - Notificações
- **Lucide React** - Ícones

### DevOps & Infrastructure
- **Docker** - Containerização
- **Docker Compose** - Orquestração local
- **GitHub Actions** - CI/CD
- **Azure Static Web Apps** - Deploy do frontend
- **Azure Application Serivce** - Deploy do backend
- **SonarCloud** - Análise de qualidade de código

## 🏗️ Arquitetura do Sistema

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Backend       │    │   Database      │
│   (React/TS)    │◄──►│ (Spring Boot)   │◄──►│  (PostgreSQL)   │
│   Port: 5173    │    │   Port: 8080    │    │   Port: 5432    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Estrutura de Pastas

```
desafio-logap-gerenciador/
├── backend/                    # API Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/logap/teste/gerenciadorbackend/
│   │   │   │   ├── configuration/      # Configurações (Security, CORS, etc.)
│   │   │   │   ├── controller/         # Controllers REST
│   │   │   │   ├── dto/               # DTOs de Request/Response
│   │   │   │   ├── exception/         # Tratamento de exceções
│   │   │   │   ├── model/             # Entidades JPA
│   │   │   │   ├── repository/        # Repositórios Spring Data
│   │   │   │   └── service/           # Lógica de negócio
│   │   │   └── resources/
│   │   │       ├── db/migration/      # Scripts Flyway
│   │   │       └── application*.properties
│   │   └── test/                      # Testes unitários e integração
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                   # Interface React
│   ├── src/
│   │   ├── components/        # Componentes reutilizáveis
│   │   ├── pages/            # Páginas da aplicação
│   │   ├── services/         # Clientes API
│   │   ├── types/            # Definições TypeScript
│   │   ├── contexts/         # Context API
│   │   └── hooks/            # Custom hooks
│   ├── package.json
│   └── vite.config.ts
├── .github/workflows/         # Pipelines CI/CD
└── docker-compose.yml         # Orquestração local
```

## 🗄️ Estrutura do Banco de Dados

### Diagrama ER

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│    usuarios     │    │     pedidos     │    │    produtos     │
├─────────────────┤    ├─────────────────┤    ├─────────────────┤
│ id (PK)         │◄──┐│ id (PK)         │    │ id (PK)         │
│ nome            │   ││ cliente_id (FK) │    │ nome            │
│ email (UNIQUE)  │   ││ status          │    │ descricao       │
│ senha           │   ││ valor_total     │    │ preco           │
│ perfil          │   ││ data_criacao    │    │ qtd_estoque     │
│ data_criacao    │   │└─────────────────┘    │ data_criacao    │
└─────────────────┘   │                       │ data_atualizacao│
                      │                       └─────────────────┘
                      │                              ▲
                      │ ┌─────────────────┐          │
                      │ │  pedido_itens   │          │
                      │ ├─────────────────┤          │
                      │ │ id (PK)         │          │
                      └─│ pedido_id (FK)  │          │
                        │ produto_id (FK) │──────────┘
                        │ quantidade      │
                        │ preco_unitario  │
                        └─────────────────┘
```

### Tabelas Principais

#### `usuarios`
- Armazena todos os usuários do sistema (Admin, Cliente, Vendedor)
- Implementa controle de acesso baseado em roles
- Senhas criptografadas com BCrypt

#### `produtos`
- Catálogo de produtos disponíveis
- Controle de estoque integrado
- Histórico de preços preservado

#### `pedidos`
- Registra todos os pedidos do sistema
- Workflow: EM_ANDAMENTO → FINALIZADO/CANCELADO
- Cálculo automático de valor total

#### `pedido_itens`
- Tabela associativa produto-pedido
- Preserva preço histórico no momento da compra
- Permite múltiplos produtos por pedido

## ✨ Funcionalidades

### 🔐 Sistema de Autenticação
- Login seguro com JWT
- Refresh token automático
- Logout com invalidação de sessão

### 👥 Gestão de Usuários (Admin)
- CRUD completo de usuários
- Alteração de roles/perfis
- Proteção contra auto-alteração de admin
- Auditoria de ações

### 📦 Gestão de Produtos (Admin/Vendedor)
- Cadastro e edição de produtos
- Controle de estoque em tempo real

### 🛒 Sistema de Pedidos
- **Cliente**: Criação de pedidos, visualização de histórico
- **Vendedor**: Aprovação/rejeição de pedidos, gestão de status
- **Admin**: Visão completa de todos os pedidos
- Cálculo automático de totais

### 📊 Dashboard Gerencial
- Faturamento total em tempo real
- Métricas de vendas e pedidos
- Top 5 produtos mais vendidos
- Clientes mais ativos
- Gráficos interativos com Recharts

### 🔍 Relatórios e Analytics
- Resumo executivo de vendas
- Análise de performance de produtos
- Métricas de satisfação do cliente
- Exportação de dados (futuro)

## 🛠️ Instalação e Execução

### Pré-requisitos

- **Java 21+**
- **Node.js 18+**
- **Docker & Docker Compose**
- **PostgreSQL 16** (opcional, pode usar Docker)

### 1. Clone o Repositório

```bash
git clone https://github.com/seu-usuario/desafio-logap-gerenciador.git
cd desafio-logap-gerenciador
```

### 2. Execução do backend com Docker (Recomendado)
 
```bash
# Crie o arquivo .env na raiz do projeto
cp .env.example .env

# Configure as variáveis de ambiente no .env:
DB_NAME=gerenciador
DB_USER=postgres
DB_PASS=123456
DB_URL=jdbc:postgresql://db:5432/gerenciador
SPRING_PROFILE=local

# Execute o ambiente completo
docker-compose up -d
```

### 3. Execução Manual

#### Backend
```bash
cd backend

# Configure o PostgreSQL e atualize application-local.properties
# Ou use H2 para desenvolvimento rápido

# Execute as migrações
./mvnw flyway:migrate

# Execute a aplicação
./mvnw spring-boot:run

# Ou compile e execute o JAR
./mvnw clean package
java -jar target/gerenciadorbackend-0.0.1-SNAPSHOT.jar
```

#### Frontend
```bash
cd frontend

# Instale as dependências
npm install

# Execute em modo desenvolvimento
npm run dev

# Para build de produção
npm run build
npm run preview
```

### 4. Usuário Administrador Padrão

Após a primeira execução, um usuário administrador será criado automaticamente:

```
Email: admin@gerenciador.com
Senha: admin123
```

## 🧪 Testes

### Backend

```bash
cd backend

# Executa todos os testes
./mvnw test

# Testes com relatório de cobertura
./mvnw verify

# Testes de integração com Testcontainers
./mvnw test -Dtest="**/*IntegrationTest"

# Relatório estará em: target/site/jacoco/index.html
```

### Tipos de Teste Implementados

- **Testes Unitários**: Services, Controllers, Utils
- **Testes de Integração**: Endpoints REST completos
- **Testes de Repository**: Consultas JPA customizadas
- **Testes de Segurança**: Autenticação e autorização

## 🔄 CI/CD

### GitHub Actions Workflows

#### 1. Backend CI (`backend-ci.yml`)
```yaml
Trigger: Push/PR para main, alterações em backend/
Steps:
  - Checkout código
  - Setup Java 21
  - Cache Maven
  - Testes unitários e integração
  - Análise SonarCloud
  - Build Docker image
  - Security scan
```

#### 2. Frontend CI (`frontend-ci.yml`)
```yaml
Trigger: Push/PR para main, alterações em frontend/
Steps:
  - Checkout código
  - Setup Node.js 18
  - Cache npm
  - Install dependencies
  - Lint check
  - Type check
  - Build production
  - Testes E2E (futuro)
```

#### 3. Deploy Azure (`azure-static-web-apps.yml`)
```yaml
Trigger: Push para main (frontend)
Steps:
  - Build React app
  - Deploy to Azure Static Web Apps
  - Integration tests
```

### Ferramentas de Qualidade

#### SonarCloud
- **Quality Gate**: Passa
- **Coverage**: >80%
- **Duplicação**: <3%
- **Maintainability**: A
- **Reliability**: A
- **Security**: A

## 🌐 Deploy

### Ambientes Disponíveis

#### Produção
- **Frontend**: [Azure Static Web Apps](https://calm-pond-04ff1b10f.6.azurestaticapps.net/)
- **Backend**: [Azure App Service](https://gerenciador-backend-b2etf5h9bbdreebb.brazilsouth-01.azurewebsites.net/gerenciador/api/v1)(Link da API para uso com ferramenta de requisições)

#### Staging
- Mesmo pipeline, branch `develop`
- Ambiente para testes de aceitação

### Variáveis de Ambiente

#### Backend
```properties
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/gerenciador
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password

# Security
JWT_SECRET=your-very-secure-secret-key-here
JWT_EXPIRATION=86400000

# Admin User
ADMIN_NAME=Administrador
ADMIN_EMAIL=admin@gerenciador.com
ADMIN_PASSWORD=admin123

# Profile
SPRING_PROFILES_ACTIVE=production
```

#### Frontend
```env
VITE_API_BASE_URL=http://localhost:8080 (Para ambiente local)