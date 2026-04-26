package com.srpafb.view;

import com.srpafb.exception.AppException;
import com.srpafb.exception.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipal extends JFrame {

    private static final Logger logger = LoggerFactory.getLogger(MenuPrincipal.class);

    public MenuPrincipal() {
        try {
            initializeUI();
            ExceptionHandler.logInfo("Menú principal inicializado correctamente");
        } catch (AppException ex) {
            ExceptionHandler.handle(ex);
        } catch (Exception ex) {
            ExceptionHandler.handle(ex, AppException.ErrorType.SYSTEM_ERROR, 
                "Error al inicializar el menú principal", "Error en MenuPrincipal constructor");
        }
    }

    private void initializeUI() {
        setTitle("SRPAFB - Sistema de Registro de Pruebas de Aptitud Física Básica");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 320);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel title = new JLabel("SRPAFB", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        JLabel subtitle = new JLabel("Registro de Pruebas de Aptitud Física Básica", SwingConstants.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JPanel header = new JPanel(new GridLayout(2, 1));
        header.add(title);
        header.add(subtitle);
        mainPanel.add(header, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        JButton btnPersonas = new JButton("Gestionar Personas");
        JButton btnPruebas = new JButton("Gestionar Pruebas");
        JButton btnBaremo = new JButton("Gestionar Baremo");
        JButton btnResultados = new JButton("Cargar Resultados");
        JButton btnInforme = new JButton("Ver Informe de Resultados");
        buttonPanel.add(btnPersonas);
        buttonPanel.add(btnPruebas);
        buttonPanel.add(btnBaremo);
        buttonPanel.add(btnResultados);
        buttonPanel.add(btnInforme);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        JLabel footer = new JLabel("Seleccione una opción para continuar", SwingConstants.CENTER);
        footer.setFont(new Font("SansSerif", Font.PLAIN, 12));
        mainPanel.add(footer, BorderLayout.SOUTH);

        btnPersonas.addActionListener(e -> abrirPersonasView());
        btnPruebas.addActionListener(e -> abrirPruebaForm());
        btnBaremo.addActionListener(e -> abrirBaremoView());
        btnResultados.addActionListener(e -> abrirResultadoForm());
        btnInforme.addActionListener(e -> abrirResultadosInformeView());

        add(mainPanel);
        setVisible(true);
    }

    private void abrirPersonasView() {
        try {
            new PersonasView();
            ExceptionHandler.logInfo("Vista de Personas abierta");
        } catch (AppException ex) {
            ExceptionHandler.handle(ex);
        } catch (Exception ex) {
            ExceptionHandler.handle(ex, AppException.ErrorType.SYSTEM_ERROR,
                "No se puede abrir la vista de personas");
        }
    }

    private void abrirPruebaForm() {
        try {
            new PruebaForm();
            ExceptionHandler.logInfo("Formulario de Pruebas abierto");
        } catch (AppException ex) {
            ExceptionHandler.handle(ex);
        } catch (Exception ex) {
            ExceptionHandler.handle(ex, AppException.ErrorType.SYSTEM_ERROR,
                "No se puede abrir el formulario de pruebas");
        }
    }

    private void abrirBaremoView() {
        try {
            new BaremoView();
            ExceptionHandler.logInfo("Vista de Baremo abierta");
        } catch (AppException ex) {
            ExceptionHandler.handle(ex);
        } catch (Exception ex) {
            ExceptionHandler.handle(ex, AppException.ErrorType.SYSTEM_ERROR,
                "No se puede abrir la vista de baremo");
        }
    }

    private void abrirResultadoForm() {
        try {
            new ResultadoForm();
            ExceptionHandler.logInfo("Formulario de Resultados abierto");
        } catch (AppException ex) {
            ExceptionHandler.handle(ex);
        } catch (Exception ex) {
            ExceptionHandler.handle(ex, AppException.ErrorType.SYSTEM_ERROR,
                "No se puede abrir el formulario de resultados");
        }
    }

    private void abrirResultadosView() {
        try {
            new ResultadosView();
            ExceptionHandler.logInfo("Vista de Resultados abierta");
        } catch (AppException ex) {
            ExceptionHandler.handle(ex);
        } catch (Exception ex) {
            ExceptionHandler.handle(ex, AppException.ErrorType.SYSTEM_ERROR,
                "No se puede abrir la vista de resultados");
        }
    }

    private void abrirResultadosInformeView() {
        try {
            new ResultadosInformeView();
            ExceptionHandler.logInfo("Vista de Informe de Resultados abierta");
        } catch (AppException ex) {
            ExceptionHandler.handle(ex);
        } catch (Exception ex) {
            ExceptionHandler.handle(ex, AppException.ErrorType.SYSTEM_ERROR,
                "No se puede abrir el informe de resultados");
        }
    }
}