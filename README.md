# Sistema de Registro de Prueba de Aptitud Física Básica (SRPAFB)

## 📌 Descripción General
El Sistema de Registro de Prueba de Aptitud Física Básica (SRPAFB) es una aplicación desarrollada en **Java + MySQL** que permite registrar personas, pruebas físicas y resultados anuales. Implementa el concepto de **baremo**, un conjunto de tablas que asignan puntajes estandarizados según género, edad y rendimiento físico.

Este sistema forma parte del trabajo práctico integrador del **Seminario de Práctica de Informática – Módulo 1: Sistemas de Información**.

---

## 🎯 Objetivos
- Digitalizar y centralizar el registro histórico de pruebas físicas (PAFB).
- Eliminar planillas manuales y reducir errores humanos.
- Automatizar cálculos de puntajes basados en tablas de baremo.
- Facilitar análisis estadísticos y reportes futuros.
- Construir una base sólida para una futura API REST + interfaz web.

---

## 🏗 Estructura del Proyecto

### Árbol de Directorios
```
SRPAFB/
├── db/                          → Scripts SQL para base de datos
│   ├── srpafb.sql              → Script principal de creación
│   ├── alter_persona.sql        → Alteraciones de tabla persona
│   ├── create_auditoria.sql     → Tabla de auditoría
│   └── create_baremo.sql        → Tabla de baremos
├── doc/
│   └── uml/                     → Diagramas UML (en desarrollo)
├── logs/                        → Archivos de log rotativo (Log4j2)
├── src/
│   ├── main/
│   │   ├── java/com/srpafb/
│   │   │   ├── app/
│   │   │   │   └── Main.java              → Punto de entrada
│   │   │   ├── connection/
│   │   │   │   └── MySQLConnection.java  → Conexión con reintentos
│   │   │   ├── model/                    → Entidades del dominio
│   │   │   │   ├── Baremo.java
│   │   │   │   ├── Categoria.java
│   │   │   │   ├── Persona.java
│   │   │   │   ├── Prueba.java
│   │   │   │   ├── Resultado.java
│   │   │   │   └── ResultadoInforme.java
│   │   │   ├── dao/                      → Acceso a datos (DAO)
│   │   │   │   ├── AuditoriaDAO.java
│   │   │   │   ├── BaremoDAO.java
│   │   │   │   ├── CategoriaDAO.java
│   │   │   │   ├── PersonaDAO.java
│   │   │   │   ├── PruebaDAO.java
│   │   │   │   ├── ResultadoDAO.java
│   │   │   │   ├── ResultadoInformeDAO.java
│   │   │   │   └── SexoDAO.java
│   │   │   ├── service/                  → Lógica de negocio
│   │   │   │   └── PuntajeService.java  → Cálculo de puntajes con baremos
│   │   │   ├── exception/                → Manejo centralizado de excepciones
│   │   │   │   ├── AppException.java    → Excepción personalizada
│   │   │   │   └── ExceptionHandler.java → Manejador centralizado
│   │   │   └── view/                     → Interfaz Swing
│   │   │       ├── MenuPrincipal.java   → Menú principal
│   │   │       ├── PersonasView.java    → Visualización de personas
│   │   │       ├── PersonaForm.java     → Formulario de personas
│   │   │       ├── BaremoView.java      → Visualización de baremos
│   │   │       ├── BaremoForm.java      → Formulario de baremos
│   │   │       ├── PruebaForm.java      → Formulario de pruebas
│   │   │       ├── ResultadosView.java  → Visualización de resultados
│   │   │       ├── ResultadoForm.java   → Formulario de resultados
│   │   │       ├── ResultadoEditForm.java → Edición de resultados
│   │   │       ├── ResultadosInformeView.java → Vista de informes
│   │   │       └── DatePicker.java      → Selector de fechas
│   │   └── resources/
│   │       └── log4j2.xml               → Configuración de logging
│   └── test/
│       └── java/                        → Pruebas unitarias
├── target/                      → Artefactos compilados (Maven)
├── pom.xml                      → Configuración de Maven
├── README.md                    → Este archivo
└── MEJORAS_EXCEPCIONES.md       → Documentación de mejoras implementadas
```

### Arquitectura de Capas
```
Capa de Presentación (view)
         ↓ (interactúa)
Capa de Negocio (service)
         ↓ (utiliza)
Capa de Acceso a Datos (dao)
         ↓ (conecta)
Base de Datos MySQL
```

---

## ⚙️ Configuración
### Base de Datos
1. Ejecutar el script de creación:
   ```bash
   mysql -u root -p < db/srpafb.sql
   ```

2. Configurar credenciales en [MySQLConnection.java](src/main/java/com/srpafb/connection/MySQLConnection.java):
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/srpafb";
   private static final String USER = "root";
   private static final String PASSWORD = "tu_contraseña";
   ```

### Logging
- Configuración en [log4j2.xml](src/main/resources/log4j2.xml)
- Logs se guardan en `logs/` con rotación diaria
- Máximo 10MB por archivo, histórico de 30 días

---

## 📦 Dependencias Principales
- **Java 21** (compilación y ejecución)
- **MySQL Connector/J 8.0.32** (conexión a base de datos)
- **SLF4J 2.0.11** (fachada de logging)
- **Log4j2 2.23.0** (implementación de logging con rotación de archivos)
- **Swing** (interfaz gráfica nativa de Java)

Ver [pom.xml](pom.xml) para lista completa de dependencias.

---

## 🧪 Funcionalidades Implementadas
✅ Registro de personas (con categoría, género y fecha de nacimiento)
✅ Registro de pruebas físicas (flexibilidad, resistencia, velocidad, etc.)
✅ Carga de resultados anuales con métricas
✅ Cálculo automático de puntaje según baremo (prueba/género/edad)
✅ Interfaz Swing completamente operativa para gestión de datos
✅ Manejo integral de excepciones con logging (SLF4J + Log4j2)
✅ Reintentos automáticos en conexión a base de datos
✅ Validaciones en capas (modelo, DAO, negocio)
✅ Auditoría de operaciones (tabla de auditoría)
✅ Mensajes de error descriptivos para usuario y logs técnicos

---

## 📊 Documentación
- [README.md](README.md) - Este archivo
- [MEJORAS_EXCEPCIONES.md](MEJORAS_EXCEPCIONES.md) - Detalle de mejoras en manejo de excepciones
- [db/srpafb.sql](db/srpafb.sql) - Script de base de datos con DDL completo
- `/doc/uml/` - Diagramas UML (en desarrollo)

Para detalles de la implementación, consulta el código fuente en `src/main/java/com/srpafb/`

---

## 🔧 Troubleshooting

### Problema: "Connection refused" al iniciar
**Solución:**
- Verificar que MySQL está en ejecución: `mysql -u root -p`
- Verificar credenciales en [MySQLConnection.java](src/main/java/com/srpafb/connection/MySQLConnection.java)
- Revisar logs en `logs/srpafb-YYYY-MM-DD-N.log`

### Problema: "Class not found" al ejecutar JAR
**Solución:**
- Usar el JAR con dependencias: `target/sistema-pafb-1.0-SNAPSHOT-jar-with-dependencies.jar`
- O compilar con: `mvn clean package -DskipTests`

### Problema: No se crea el archivo de log
**Solución:**
- Verificar que la carpeta `logs/` existe
- Revisar permisos de escritura en el directorio
- Verificar configuración en [log4j2.xml](src/main/resources/log4j2.xml)

### Problema: Errores de validación en formularios
**Solución:**
- Revisar el mensaje de error en el diálogo
- Consultar logs en `logs/` para detalles técnicos
- Validar que los datos cumplan los requisitos (DNI único, campos obligatorios, etc.)

---

## 🔨 Compilación y Build

### Con Maven
```bash
# Compilar proyecto
mvn clean compile

# Empaquetar JAR simple
mvn clean package

# Empaquetar JAR con dependencias
mvn clean package -DskipTests

# Compilar y ejecutar directamente
mvn exec:java -Dexec.mainClass="com.srpafb.app.Main"
```

### Artifacts
- **Simple**: `target/sistema-pafb-1.0-SNAPSHOT.jar`
- **Con dependencias**: `target/sistema-pafb-1.0-SNAPSHOT-jar-with-dependencies.jar`

---

## ▶️ Ejecución del Proyecto

### Opción 1: IDE (IntelliJ IDEA)
1. Importar proyecto como **Maven Project**
2. Ejecutar [Main.java](src/main/java/com/srpafb/app/Main.java) con clic derecho → Run
3. La interfaz Swing se abrirá automáticamente

### Opción 2: Desde línea de comandos
```bash
# Requiere compilación previa
java -cp target/classes com.srpafb.app.Main

# O usar JAR con dependencias
java -jar target/sistema-pafb-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### Opción 3: Maven
```bash
mvn clean package -DskipTests
java -jar target/sistema-pafb-1.0-SNAPSHOT-jar-with-dependencies.jar
```

---

## 📋 Requisitos Previos
- Java 21 instalado
- MySQL Server en ejecución (puerto 3306)
- Base de datos `srpafb` creada (ejecutar `db/srpafb.sql`)
- Maven 3.8+ (opcional si usas IDE)

---

## 👨‍💻 Autor
**Sergio G. Frelig**  
Legajo **VINF09693**  
Seminario de Práctica de Informática – Módulo 1  
Profesora: **Ana Carolina Ferreyra**  
Universidad Siglo 21

---

## 📄 Licencia
Proyecto académico – uso educativo.
