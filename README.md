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
