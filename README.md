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

## 🏗 Arquitectura del Sistema
El proyecto está organizado en capas:
com.srpafb
├── model → Entidades del dominio
├── dao → Acceso a datos con MySQL
├── service → Lógica de negocio (baremos y validaciones)
├── view → Interfaz Swing para carga de datos
└── app → Clase Main

---

## 💾 Base de Datos
El script necesario se encuentra en:
db/srpafb.sql

Incluye:
- creación de base `srpafb`
- tablas: persona, categoria, prueba, resultado, baremo
- claves foráneas
- inserts básicos

---

## 🧪 Funcionalidades Implementadas
- Registro de personas (con categoría y fecha de nacimiento)
- Registro de pruebas físicas
- Carga de resultados anuales
- Cálculo automático de puntaje según baremo (prueba/género)
- Importación desde archivo Excel (futuro módulo)
- Interfaz Swing operativa para carga de datos

---

## 📊 Documentación
En `/docs` se encuentran:

- Informe académico completo (PDF)
- Diagramas UML:
  - Diagrama de Clases
  - Diagrama de Secuencia – Carga de Resultado
- Presentación breve para exposición oral

---

## ▶️ Ejecución del Proyecto
1. Clonar el repositorio  
2. Importar el proyecto como **Maven Project** en IntelliJ  
3. Crear base de datos ejecutando `db/srpafb.sql`  
4. Configurar credenciales en `MySQLConnection.java`  
5. Ejecutar `com.srpafb.app.Main`

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
