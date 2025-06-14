# ================= STAGE 1: Build =================
# Usa uma imagem base com Maven e JDK 21 para compilar a aplicação
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia o pom.xml e baixa as dependências.
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline

# Copia o restante do código-fonte
COPY src ./src

# Compila a aplicação e gera o arquivo .jar, pulando os testes
RUN --mount=type=cache,target=/root/.m2 \
    mvn package -DskipTests

# ================= STAGE 2: Run =================
# Usa uma imagem contendo apenas o Java Runtime Environment (JRE)
FROM eclipse-temurin:21-jre

# Define o diretório de trabalho
WORKDIR /app

# Copia apenas o arquivo .jar gerado no estágio anterior para a imagem final
COPY --from=builder /app/target/*.jar app.jar

# Expõe a porta que a aplicação Spring Boot usa
EXPOSE 8080

# Comando para executar a aplicação quando o contêiner iniciar
ENTRYPOINT ["java", "-jar", "app.jar"]