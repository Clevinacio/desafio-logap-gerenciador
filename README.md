# Tarefa 1: Criação de uma aplicação Web

O setor de vendas de uma empresa precisa de uma ferramenta para gerenciar pedidos de clientes e acompanhar as vendas de forma eficiente. Seu desafio será desenvolver uma aplicação web que atenda a essa necessidade.

## Requisitos Obrigatórios:

A aplicação deve incluir:
- **Autenticação e Controle de Acesso:**
    - Implementação de login seguro utilizando JWT
    - Diferentes perfis de usuário com permissões específicas:
        - **Administrador**: acesso total ao sistema e gestão de usuários
        - **Cliente**: pode visualizar a lista de produtos e realizar pedidos
        - **Vendedor**: gerencia o estoque e aprova ou rejeita pedidos
- **Gestão de Pedidos:**
        - Os pedidos devem ser acompanhados pelo sistema e conter um status atualizado (ex: "Em andamento", "Finalizado", "Cancelado").
        - Todas as vendas devem ser registradas e armazenadas no banco de dados.
- **Visão Gerencial:**
        - A aplicação deve exibir relatórios e/ou gráficos dinâmicos contendo:
            - Resumo das vendas (total de pedidos, valor faturado, produtos vendidos).
            - Pedidos pendentes
            - Clientes mais ativos
- **Qualidade e Manutenibilidade:**
    - O projeto deve seguir boas práticas de organização de código e estruturação do repositório.
    - Deve conter testes unitários e testes de integração para garantir a confiabilidade da aplicação.

# Tarefa 2: CI/CD

Após a criação da aplicação, será necessário configurar um processo automatizado de Integração Contínua e Entrega Contínua (CI/CD) para disponibilizá-la na internet.

## Requisitos Obrigatórios:
- Criar uma **pipeline de CI/CD** utilizando ferramentas como **GitHub Actions, GitLab CI, Jenkins ou similares**.
- A pipeline deve conter **pelo menos três etapas principais**:
    - **Build**: Compilação e empacotamento da aplicação.
    - **Dockerização**: Criação de um Dockerfile funcional e, opcionalmente, um docker-compose.
    - **Deploy**: Publicação automática da aplicação em um ambiente de nuvem (ex: AWS, Azure, Vercel, Render, Heroku).

## Diferenciais (Extras - Não Obrigatórios, Mas Valorizados):
- Adicionar etapas para execução automatizada de testes dentro da pipeline.
- Incluir ferramentas de análise de qualidade de código
- Implementar verificação de segurança antes do deploy
- Configuração de diferentes pipelines para desenvolvimento, homologação e produção
- Configuração de logs estruturados e alertas automatizados em caso de falha na execução da pipeline 
- Implementação de métricas e dashboards para monitoramento da aplicação

# Entrega e Avaliação

1. A modelagem do banco de dados, organização do código, estrutura do repositório e interface gráfica serão avaliados.
2. A aplicação deve estar acessível online, e as instruções de acesso devem ser fornecidas.
3. O candidato deve enviar o link do repositório contendo o código-fonte da solução.
