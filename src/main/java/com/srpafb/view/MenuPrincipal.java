package view;

import javax.swing.*;

public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {
        setTitle("SIGAEF - Sistema de Gestión de Pruebas Físicas");
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

        btnPersonas.addActionListener(e -> new personaForm());
        btnPruebas.addActionListener(e -> new pruebaForm());
        btnResultados.addActionListener(e -> new resultadoForm());

        add(panel);
        setVisible(true);
    }
}