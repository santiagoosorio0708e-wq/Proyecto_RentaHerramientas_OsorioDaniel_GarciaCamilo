# ============================================================================
# Dockerfile de la Plataforma de Renta de Herramientas
#
# Build en dos etapas:
#   1) "builder" compila el proyecto con Maven y genera el .jar
#   2) la imagen final solo lleva el JRE y el .jar ya construido
#
# Hacerlo en dos etapas evita que Maven, el codigo fuente y el repositorio
# ~/.m2 queden dentro de la imagen final. El resultado pesa mucho menos.
# ============================================================================

# ----------------------------- Etapa 1: build -----------------------------
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Primero solo el pom: si no cambian las dependencias, Docker reutiliza
# esta capa en cache y no vuelve a descargarlas en cada build.
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# -------------------------- Etapa 2: ejecucion ----------------------------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Usuario sin privilegios: si alguien logra entrar al contenedor, no es root.
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=builder /build/target/*.jar app.jar

RUN chown -R appuser:appgroup /app
USER appuser

EXPOSE 8080

# Healthcheck para que Docker sepa si la aplicacion realmente responde
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/v1/catalogo || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
