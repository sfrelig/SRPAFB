package com.srpafb.view;

import com.srpafb.dao.*;
import com.srpafb.model.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class ResultadoForm extends JFrame {

    private JComboBox<Persona> comboPersona;
    private JComboBox<Prueba> comboPrueba;
    private JTextField txtValor;
    private JTextField txtFecha;
    private JLabel lblValor;
    private JLabel lblMensaje;

    public ResultadoForm() {
        setTitle("Registro de Resultados PAFB");
        setSize(480, 320);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        comboPersona = new JComboBox<>();
        comboPrueba = new JComboBox<>();
        txtValor = new JTextField();
        txtFecha = new JTextField();
        txtFecha.setToolTipText("Formato: YYYY-MM-DD");
        txtValor.setToolTipText("Ingrese un valor numérico.");
        lblValor = new JLabel("Valor:");
        lblMensaje = new JLabel("Complete los datos de la prueba y presione Guardar.");

        cargarPersonas();
        cargarPruebas();
        actualizarTextoValor();
        comboPrueba.addActionListener(e -> actualizarTextoValor());

        addLabel(panel, "Persona:", 0, gbc);
        addField(panel, comboPersona, 0, gbc);
        addLabel(panel, "Prueba:", 1, gbc);
        addField(panel, comboPrueba, 1, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(lblValor, gbc);
        addField(panel, txtValor, 2, gbc);
        addLabel(panel, "Fecha (YYYY-MM-DD):", 3, gbc);
        addField(panel, txtFecha, 3, gbc);

        lblMensaje.setFont(lblMensaje.getFont().deriveFont(Font.PLAIN, 12f));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(lblMensaje, gbc);

        JButton btnGuardar = new JButton("Guardar");
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(btnGuardar, gbc);

        btnGuardar.addActionListener(e -> guardar());

        add(panel);
        setVisible(true);
    }

    private void actualizarTextoValor() {
        Prueba prueba = (Prueba) comboPrueba.getSelectedItem();
        String texto = "Valor:";
        String mensaje = "Ingrese el valor de la prueba.";
        if (prueba != null && prueba.getNombre() != null) {
            String nombre = prueba.getNombre().toLowerCase();
            if (nombre.contains("carrera") || nombre.contains("3200")) {
                texto = "Tiempo (segundos):";
                mensaje = "Ingrese el tiempo que tardó en recorrer 3200 metros, en segundos.";
            } else if (nombre.contains("flex") || nombre.contains("abdominal") || nombre.contains("barra")) {
                texto = "Repeticiones:";
                mensaje = "Ingrese la cantidad de repeticiones realizadas.";
            }
        }
        lblValor.setText(texto);
        txtValor.setToolTipText(mensaje);
        lblMensaje.setText(mensaje);
    }

    private void addLabel(JPanel panel, String text, int row, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(text), gbc);
    }

    private void addField(JPanel panel, JComponent field, int row, GridBagConstraints gbc) {
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private void cargarPersonas() {
        PersonaDAO dao = new PersonaDAO();
        List<Persona> lista = dao.obtenerTodas();
        if (lista.isEmpty()) {
            comboPersona.addItem(new Persona());
        } else {
            for (Persona p : lista) {
                comboPersona.addItem(p);
            }
        }
    }

    private void cargarPruebas() {
        PruebaDAO dao = new PruebaDAO();
        List<Prueba> lista = dao.obtenerTodas();
        if (lista.isEmpty()) {
            comboPrueba.addItem(new Prueba());
        } else {
            for (Prueba p : lista) {
                comboPrueba.addItem(p);
            }
        }
    }

    private double parseValor(Prueba prueba, String texto) {
        if (prueba != null && prueba.getNombre() != null) {
            String nombre = prueba.getNombre().toLowerCase();
            if (nombre.contains("carrera") || nombre.contains("3200")) {
                return parseTiempoSegundos(texto);
            }
        }
        return Double.parseDouble(texto.replace(',', '.'));
    }

    private double parseTiempoSegundos(String texto) {
        String input = texto.trim();
        if (input.isEmpty()) {
            throw new IllegalArgumentException("El tiempo de carrera es obligatorio.");
        }
        String[] partes = input.split(":");
        if (partes.length < 2 || partes.length > 3) {
            throw new IllegalArgumentException("Formato de tiempo inválido. Use mm:ss o hh:mm:ss.");
        }
        int horas = 0;
        int minutos;
        int segundos;
        try {
            if (partes.length == 3) {
                horas = Integer.parseInt(partes[0].trim());
                minutos = Integer.parseInt(partes[1].trim());
                segundos = Integer.parseInt(partes[2].trim());
            } else {
                minutos = Integer.parseInt(partes[0].trim());
                segundos = Integer.parseInt(partes[1].trim());
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Los valores de tiempo deben ser numéricos.");
        }
        if (horas < 0 || minutos < 0 || segundos < 0 || segundos >= 60 || minutos >= 60) {
            throw new IllegalArgumentException("Tiempo inválido. Minutos y segundos deben estar entre 0 y 59.");
        }
        long totalSegundos = horas * 3600L + minutos * 60L + segundos;
        if (totalSegundos > 10800) {
            throw new IllegalArgumentException("El tiempo de carrera no puede superar las 3 horas.");
        }
        return totalSegundos;
    }

    private void guardar() {
        try {
            if (comboPersona.getSelectedItem() == null || comboPrueba.getSelectedItem() == null) {
                throw new IllegalArgumentException("Seleccione persona y prueba.");
            }
            String valorTexto = txtValor.getText().trim();
            String fechaTexto = txtFecha.getText().trim();
            if (valorTexto.isEmpty() || fechaTexto.isEmpty()) {
                throw new IllegalArgumentException("Valor y fecha son obligatorios.");
            }

            Resultado r = new Resultado();
            r.setPersona((Persona) comboPersona.getSelectedItem());
            r.setPrueba((Prueba) comboPrueba.getSelectedItem());
            r.setValor(parseValor((Prueba) comboPrueba.getSelectedItem(), valorTexto));
            r.setFecha(LocalDate.parse(fechaTexto));

            new ResultadoDAO().insertar(r);
            JOptionPane.showMessageDialog(this, "Resultado registrado correctamente.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        }
    }
}