package com.srpafb.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * Manejador centralizado de excepciones.
 * Proporciona logging consistente y mensajes de error para el usuario.
 */
public class ExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    private ExceptionHandler() {
        // Clase de utilidad
    }

    /**
     * Maneja una excepción de la aplicación: registra en log y muestra diálogo al usuario.
     */
    public static void handle(AppException ex) {
        logError(ex);
        showUserDialog(ex.getUserMessage(), ex.getErrorType().getDescription());
    }

    /**
     * Maneja una excepción genérica: la convierte a AppException y la maneja.
     */
    public static void handle(Exception ex, AppException.ErrorType errorType, String userMessage) {
        AppException appEx = new AppException(errorType, userMessage, ex);
        handle(appEx);
    }

    /**
     * Maneja una excepción genérica con mensaje de log personalizado.
     */
    public static void handle(Exception ex, AppException.ErrorType errorType, String userMessage, String logMessage) {
        AppException appEx = new AppException(errorType, userMessage, logMessage, ex);
        handle(appEx);
    }

    /**
     * Registra un error en el log con nivel ERROR.
     */
    public static void logError(AppException ex) {
        logger.error("{} - {}", ex.getErrorType().getDescription(), ex.getLogMessage(), ex.getCause());
    }

    /**
     * Registra un error genérico en el log.
     */
    public static void logError(String message, Throwable ex) {
        logger.error(message, ex);
    }

    /**
     * Registra un warning en el log.
     */
    public static void logWarning(String message) {
        logger.warn(message);
    }

    /**
     * Registra información en el log.
     */
    public static void logInfo(String message) {
        logger.info(message);
    }

    /**
     * Registra debug en el log.
     */
    public static void logDebug(String message) {
        logger.debug(message);
    }

    /**
     * Muestra un diálogo de error al usuario.
     */
    public static void showUserDialog(String message, String title) {
        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(
                null,
                message,
                title,
                JOptionPane.ERROR_MESSAGE
            )
        );
    }

    /**
     * Muestra un diálogo de información al usuario.
     */
    public static void showInfoDialog(String message, String title) {
        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(
                null,
                message,
                title,
                JOptionPane.INFORMATION_MESSAGE
            )
        );
    }

    /**
     * Muestra un diálogo de confirmación al usuario.
     */
    public static int showConfirmDialog(String message, String title) {
        return JOptionPane.showConfirmDialog(
            null,
            message,
            title,
            JOptionPane.YES_NO_OPTION
        );
    }
}
