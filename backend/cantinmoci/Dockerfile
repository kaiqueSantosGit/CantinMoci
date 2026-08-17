# Dockerfile do backend CantinMoci — usado pelo Render (Fase 4) para builda e
# rodar a aplicacao em producao, garantindo Java 21 independente da plataforma.
#
# Build em duas etapas (multi-stage):
#   1) "build"   — imagem com JDK completo, so para compilar o projeto com Maven
#   2) runtime   — imagem final, menor, so com JRE + o .jar ja compilado
# Isso mantem a imagem final enxuta (nao carrega o Maven nem o codigo-fonte).

# ---- Etapa 1: build ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copia primeiro so o necessario para baixar as dependencias.
# Assim, se so o codigo mudar (e nao o pom.xml), o Docker reaproveita
# esse passo do cache e o build fica mais rapido.
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Agora copia o codigo-fonte e compila, gerando o .jar em target/
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# ---- Etapa 2: runtime ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copia so o .jar final da etapa de build (nome fixo definido no pom.xml:
# artifactId=cantinmoci, version=0.0.1-SNAPSHOT)
COPY --from=build /app/target/cantinmoci-0.0.1-SNAPSHOT.jar app.jar

# Porta documentada (o valor real vem da variavel PORT em producao)
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
