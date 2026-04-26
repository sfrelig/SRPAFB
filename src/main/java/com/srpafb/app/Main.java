package com.srpafb.app;

import com.srpafb.exception.ExceptionHandler;
import com.srpafb.view.MenuPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        ExceptionHandler.logInfo("Iniciando aplicación SRPAFB");

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                ExceptionHandler.logDebug("Look and Feel del sistema aplicado correctamente");
            } catch (UnsupportedLookAndFeelException e) {
                ExceptionHandler.logWarning("No se pudo aplicar el Look and Feel del sistema: " + e.getMessage() + ". Se usará el Look and Feel predeterminado.");
            } catch (Exception e) {
                ExceptionHandler.logError("Error al intentar aplicar Look and Feel", e);
            }

            try {
                new MenuPrincipal();
                ExceptionHandler.logInfo("Menú principal cargado exitosamente");
            } catch (Exception e) {
                ExceptionHandler.logError("Error crítico al cargar el menú principal", e);
                ExceptionHandler.showUserDialog(
                    "Error al inicializar la aplicación: " + e.getMessage(),
                    "Error de Inicialización"
                );
                System.exit(1);
            }
        });
    }
}