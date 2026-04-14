package com.srpafb.view;

import javax.swing.*;

public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {
        setTitle("SRPAFB - Sistema de Registro de Pruebas de Aptitud Física Basica");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton btnPersonas = new JButton("Gestionar Personas");
        JButton btnPruebas = new JButton("Gestionar Pruebas");
        JButton btnResultados = new JButton("Cargar Resultados");

        JPanel panel = new JPanel();
        panel.add(btnPersonas);
        panel.add(btnPruebas);
        panel.add(btnResultados);

        btnPersonas.addActionListener(e -> new PersonaForm());
        btnPruebas.addActionListener(e -> new PruebaForm());
        btnResultados.addActionListener(e -> new ResultadoForm());

        add(panel);
        setVisible(true);
    }
}