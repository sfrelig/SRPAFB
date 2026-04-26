package com.srpafb.exception;

/**
 * Excepción personalizada para la aplicación SRPAFB.
 * Proporciona mensajes claros para el usuario y contexto para logging.
 */
public class AppException extends RuntimeException {

    private final String userMessage;
    private final String logMessage;
    private final ErrorType errorType;

    public enum ErrorType {
        DATABASE_CONNECTION("Error de conexión a base de datos"),
        DATABASE_OPERATION("Error en operación de base de datos"),
        INVALID_INPUT("Datos inválidos"),
        BUSINESS_LOGIC("Error en lógica de negocio"),
        FILE_OPERATION("Error en operación de archivo"),
        SYSTEM_ERROR("Error del sistema"),
        VALIDATION_ERROR("Error de validación");

        private final String description;

        ErrorType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public AppException(ErrorType errorType, String userMessage) {
        super(userMessage);
        this.errorType = errorType;
        this.userMessage = userMessage;
        this.logMessage = userMessage;
    }

    public AppException(ErrorType errorType, String userMessage, String logMessage) {
        super(userMessage);
        this.errorType = errorType;
        this.userMessage = userMessage;
        this.logMessage = logMessage;
    }

    public AppException(ErrorType errorType, String userMessage, Throwable cause) {
        super(userMessage, cause);
        this.errorType = errorType;
        this.userMessage = userMessage;
        this.logMessage = userMessage + " - Causa: " + cause.getClass().getSimpleName();
    }

    public AppException(ErrorType errorType, String userMessage, String logMessage, Throwable cause) {
        super(userMessage, cause);
        this.errorType = errorType;
        this.userMessage = userMessage;
        this.logMessage = logMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}
