# Etapa 1: Build (Compila o código usando o Maven)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Executa o build gerando o .jar e ignorando os testes
RUN mvn clean package -DskipTests

# Etapa 2: Run (Roda a aplicação em uma imagem mais leve)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copia o .jar gerado na etapa anterior
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# Comando para iniciar o Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]