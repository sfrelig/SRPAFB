# Mejora del Manejo de Excepciones - SRPAFB

## Resumen de Cambios

Se ha implementado un sistema comprehensivo de manejo de excepciones y logging para mejorar la robustez y mantenibilidad de la aplicación SRPAFB.

### 1. **Nuevas Dependencias Agregadas**
- **SLF4J 2.0.11**: API de logging estándar en Java
- **Log4j2 2.23.0**: Implementación completa de logging con rotación de archivos

### 2. **Nuevas Clases Creadas**

#### `com.srpafb.exception.AppException`
Excepción personalizada que proporciona:
- Tipos de error categorizados (DATABASE_CONNECTION, DATABASE_OPERATION, INVALID_INPUT, etc.)
- Mensajes claros para el usuario
- Mensajes detallados para logs
- Soporte para causas encadenadas

#### `com.srpafb.exception.ExceptionHandler`
Manejador centralizado que ofrece:
- **Métodos de logging**: logError(), logWarning(), logInfo(), logDebug()
- **Métodos de diálogo**: showUserDialog(), showInfoDialog(), showConfirmDialog()
- **Manejo de excepciones**: handle() para AppException y excepciones genéricas

#### `log4j2.xml`
Archivo de configuración que proporciona:
- **Rotación diaria** de archivos de log
- **Límite de 10MB** por archivo
- **Historico de 30 días** de logs
- **Appenders separados** para logs generales y errores
- **Niveles apropiados** para cada componente (debug para app, warn para librerías externas)

### 3. **Archivos Actualizados**

#### `Main.java`
**Mejoras:**
- Logging al iniciar la aplicación
- Manejo seguro de Look and Feel del sistema
- Captura de excepciones críticas con mensaje amigable al usuario
- Logging de eventos exitosos

#### `MySQLConnection.java`
**Mejoras:**
- **Reintentos automáticos** (3 intentos con espera progresiva)
- Logging detallado de intentos de conexión
- Mensajes de error específicos para el usuario vs internos para logs
- Detección de problemas de configuración
- AppException en lugar de RuntimeException

#### `MenuPrincipal.java`
**Mejoras:**
- Manejo seguro de inicialización de UI
- Try-catch alrededor de cada vista
- Logging de operaciones exitosas
- Diálogos de error descriptivos para el usuario

#### `PersonasView.java`
**Mejoras:**
- Manejo centralizado de excepciones en cargarPersonas()
- Logging del número de personas cargadas
- Mensajes descriptivos en eliminación
- Validación de selección de fila
- Logging de operaciones exitosas y fallos

#### `PersonaDAO.java`
**Mejoras Completas:**
- Logging en cada operación (insertar, eliminar, obtener)
- Validaciones con mensajes claros (DNI obligatorio, DNI duplicado)
- **Detección de clave foránea**: Mensaje específico cuando hay resultados asociados
- Extracción de construcción de Persona en método `construirPersonaDesdeResultSet()`
- AppException en lugar de RuntimeException
- Logging en nivel DEBUG para consultas
- Logging en nivel INFO para operaciones exitosas

### 4. **Patrones de Manejo de Excepciones**

#### Patrón 1: Validación
```java
if (p.getDni() == null || p.getDni().trim().isEmpty()) {
    throw new AppException(
        AppException.ErrorType.VALIDATION_ERROR,
        "El DNI es obligatorio.",
        "Intento de insertar persona sin DNI"
    );
}
```

#### Patrón 2: Operación SQL
```java
try {
    // operación
} catch (AppException ex) {
    throw ex;
} catch (SQLException e) {
    String logMsg = "Error SQL al insertar persona - DNI: " + p.getDni();
    logger.error(logMsg, e);
    throw new AppException(
        AppException.ErrorType.DATABASE_OPERATION,
        "No se pudo guardar la persona. Verifique los datos.",
        logMsg,
        e
    );
}
```

#### Patrón 3: Manejo en Vista
```java
try {
    personaDAO.eliminar(id);
    ExceptionHandler.logInfo("Persona ID: " + id + " eliminada exitosamente");
} catch (AppException ex) {
    ExceptionHandler.handle(ex);
} catch (Exception ex) {
    ExceptionHandler.handle(ex, AppException.ErrorType.DATABASE_OPERATION,
        "No se pudo eliminar la persona.");
}
```

### 5. **Archivos de Log Generados**

Los logs se guardan en la carpeta `./logs/`:
- **srpafb.log**: Log completo de la aplicación
- **srpafb-error.log**: Solo errores

Ejemplo de entrada de log:
```
2026-04-20 10:30:45.123 [INFO ] PersonaDAO - Persona insertada exitosamente - DNI: 12345678, Nombre: Juan García
2026-04-20 10:31:22.456 [ERROR] ExceptionHandler - Database Connection - Falló la conexión a la base de datos
```

### 6. **Beneficios Implementados**

✅ **Para el Usuario:**
- Mensajes claros y comprensibles
- Diálogos informativos con información específica
- La aplicación no se bloquea con excepciones inesperadas

✅ **Para el Desarrollador:**
- Logs completos en archivos separados
- Trazas detalladas de errores con stack trace
- Categorización clara de errores
- Mensajes de log vs mensajes de usuario

✅ **Para el Mantenimiento:**
- Fácil debugging con archivos de log
- Historico de 30 días disponible
- Rotación automática de archivos
- Patrón consistente en toda la aplicación

### 7. **Próximas Mejoras Recomendadas**

1. Aplicar el mismo patrón a los demás DAOs (PruebaDAO, ResultadoDAO, etc.)
2. Mejorar ResultadoForm y ResultadosView con manejo de excepciones
3. Agregar validaciones más complejas en PersonaForm
4. Implementar logging en service layer (PuntajeService)

## Compilación y Ejecución

```bash
# Compilar
mvn clean compile

# Ejecutar
mvn exec:java -Dexec.mainClass="com.srpafb.app.Main"
```

Los logs estarán disponibles en `./logs/srpafb.log` y `./logs/srpafb-error.log`.
