package com.srpafb.view;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {
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

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        JButton btnPersonas = new JButton("Gestionar Personas");
        JButton btnPruebas = new JButton("Gestionar Pruebas");
        JButton btnResultados = new JButton("Cargar Resultados");
        JButton btnInforme = new JButton("Ver Resultados / Informe PDF");
        buttonPanel.add(btnPersonas);
        buttonPanel.add(btnPruebas);
        buttonPanel.add(btnResultados);
        buttonPanel.add(btnInforme);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        JLabel footer = new JLabel("Seleccione una opción para continuar", SwingConstants.CENTER);
        footer.setFont(new Font("SansSerif", Font.PLAIN, 12));
        mainPanel.add(footer, BorderLayout.SOUTH);

        btnPersonas.addActionListener(e -> new PersonasView());
        btnPruebas.addActionListener(e -> new PruebaForm());
        btnResultados.addActionListener(e -> new ResultadoForm());
        btnInforme.addActionListener(e -> new ResultadosView());

        add(mainPanel);
        setVisible(true);
    }
}