package com.srpafb.connection;

import com.srpafb.exception.AppException;
import com.srpafb.exception.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestiona la conexión a la base de datos MySQL.
 * Proporciona logs detallados para debugging y un mejor manejo centralizado de excepciones.
 */
public class MySQLConnection {

    private static final Logger logger = LoggerFactory.getLogger(MySQLConnection.class);
    
    private static final String URL = "jdbc:mysql://localhost:3307/srpafb?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "290319";
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;

    private MySQLConnection() {
        // Clase de utilidad
    }

    /**
     * Obtiene una conexión a la base de datos con reintentos automáticos.
     * @return Connection a la base de datos
     * @throws AppException si no se puede conectar después de los reintentos
     */
    public static Connection getConnection() {
        SQLException lastException = null;
        
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                logger.debug("Intento {} de {} para conectar a la base de datos: {}", attempt, MAX_RETRIES, URL);
                Connection connection = DriverManager.getConnection(URL, USER, PASS);
                logger.info("Conexión a base de datos establecida exitosamente");
                return connection;
            } catch (SQLException e) {
                lastException = e;
                logger.warn("Fallo en intento {} de conectar a MySQL: {}", attempt, e.getMessage());
                
                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        logger.error("Interrumpido durante espera de reintento", ie);
                    }
                }
            }
        }

        // En este punto, todos los intentos fallaron
        String logMessage = String.format(
            "Falló la conexión a la base de datos después de %d intentos. URL: %s, Usuario: %s, Causa: %s",
            MAX_RETRIES, URL, USER, lastException != null ? lastException.getMessage() : "desconocida"
        );
        
        logger.error(logMessage, lastException);
        
        throw new AppException(
            AppException.ErrorType.DATABASE_CONNECTION,
            "No se puede conectar a la base de datos. Verifique que el servicio MySQL esté activo y los datos de conexión sean correctos.",
            logMessage,
            lastException
        );
    }
}