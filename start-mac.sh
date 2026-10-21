#!/bin/bash
# ==============================================================================
# Script de Inicio Rápido y Optimización para macOS (Apple Silicon M1/M2/M3/M4 e Intel)
# Plataforma de Renta de Herramientas y Equipos de Construcción
# ==============================================================================

set -e

# Colores de salida para terminal macOS (Zsh / Bash)
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # Sin color

echo -e "${BLUE}========================================================================${NC}"
echo -e "${BLUE}  🏗️  PLATAFORMA DE RENTA DE HERRAMIENTAS - ARRANQUE OPTIMIZADO PARA MAC ${NC}"
echo -e "${BLUE}========================================================================${NC}"

# 1. Comprobación de JDK
echo -e "\n${YELLOW}🔍 [1/4] Verificando entorno Java en macOS...${NC}"
if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Java no está instalado en este Mac.${NC}"
    echo -e "Instálelo fácilmente con Homebrew ejecutando:"
    echo -e "   brew install openjdk@21"
    echo -e "   sudo ln -sfn /opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-21.jdk"
    exit 1
fi

JAVA_VER=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
echo -e "${GREEN}✓ Java detectado: versión ${JAVA_VER} LTS${NC}"

# 2. Asegurar permisos de ejecución en mvnw
echo -e "\n${YELLOW}⚙️  [2/4] Configurando permisos de Maven Wrapper en macOS...${NC}"
chmod +x ./mvnw
echo -e "${GREEN}✓ Permisos de ejecución aplicados correctamente a ./mvnw${NC}"

# 3. Configuración de Variables de Entorno por defecto (SQLite portable y autónomo)
export SERVER_PORT="${SERVER_PORT:-8080}"
export DB_URL="${DB_URL:-jdbc:sqlite:renta_herramientas.db}"
export DB_DRIVER="${DB_DRIVER:-org.sqlite.JDBC}"
export DB_USERNAME="${DB_USERNAME:-}"
export DB_PASSWORD="${DB_PASSWORD:-}"
export JWT_SECRET="${JWT_SECRET:-404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}"

echo -e "\n${YELLOW}🚀 [3/4] Variables de configuración activas:${NC}"
echo -e "   - Puerto HTTP: ${SERVER_PORT}"
echo -e "   - Motor Base de Datos: ${GREEN}SQLite 3 (renta_herramientas.db)${NC}"
echo -e "   - Portabilidad: ${GREEN}100% Nativo en macOS (Apple Silicon M1/M2/M3/M4 e Intel) - Sin MySQL ni Docker${NC}"

# 4. Abrir automáticamente en Safari / Chrome en background tras arranque
(
    echo -e "\n${BLUE}⏳ Esperando a que el servidor Spring Boot esté listo...${NC}"
    while ! curl -s "http://localhost:${SERVER_PORT}/api/v1/catalogo" > /dev/null 2>&1; do
        sleep 1.5
    done
    echo -e "${GREEN}🎉 Servidor online! Abriendo RentaTools en su navegador de macOS...${NC}"
    open "http://localhost:${SERVER_PORT}/api/v1/index.html" || true
) &

# 5. Iniciar la aplicación con Spring Boot
echo -e "\n${YELLOW}▶️  [4/4] Levantando servidor Spring Boot 3.4 con Java 21...${NC}"
./mvnw spring-boot:run
