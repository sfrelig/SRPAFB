package com.srpafb.view;

import com.srpafb.dao.ResultadoDAO;
import com.srpafb.model.Resultado;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ResultadoEditForm extends JDialog {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public ResultadoEditForm(JFrame parent, Resultado resultado, Runnable onSaved) {
        super(parent, "Editar Resultado", true);
        setSize(420, 260);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblPersona = new JLabel(resultado.getPersona() != null ? resultado.getPersona().toString() : "-");
        JLabel lblPrueba = new JLabel(resultado.getPrueba() != null ? resultado.getPrueba().getNombre() : "-");
        JTextField txtValor = new JTextField(String.valueOf(resultado.getValor()));
        DatePicker datePicker = new DatePicker(resultado.getFecha());

        addLabel(panel, "Persona:", 0, gbc);
        addField(panel, lblPersona, 0, gbc);
        addLabel(panel, "Prueba:", 1, gbc);
        addField(panel, lblPrueba, 1, gbc);
        addLabel(panel, "Valor:", 2, gbc);
        addField(panel, txtValor, 2, gbc);
        addLabel(panel, "Fecha:", 3, gbc);
        addField(panel, datePicker, 3, gbc);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        footer.add(btnCancelar);
        footer.add(btnGuardar);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(footer, gbc);

        btnGuardar.addActionListener(e -> {
            try {
                double valor = parseValor(txtValor.getText().trim());
                LocalDate fecha = datePicker.getDate();
                resultado.setValor(valor);
                resultado.setFecha(fecha);
                new ResultadoDAO().actualizar(resultado);
                JOptionPane.showMessageDialog(this, "Resultado actualizado correctamente.");
                onSaved.run();
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dispose());

        add(panel);
        setVisible(true);
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

    private double parseValor(String texto) {
        if (texto.contains(":")) {
            String[] partes = texto.split(":");
            int horas = 0;
            int minutos;
            int segundos;
            if (partes.length == 3) {
                horas = Integer.parseInt(partes[0].trim());
                minutos = Integer.parseInt(partes[1].trim());
                segundos = Integer.parseInt(partes[2].trim());
            } else {
                minutos = Integer.parseInt(partes[0].trim());
                segundos = Integer.parseInt(partes[1].trim());
            }
            if (horas < 0 || minutos < 0 || segundos < 0 || segundos >= 60 || minutos >= 60) {
                throw new IllegalArgumentException("Tiempo inválido. Use hh:mm:ss o mm:ss.");
            }
            long total = horas * 3600L + minutos * 60L + segundos;
            if (total > 10800) {
                throw new IllegalArgumentException("El tiempo no puede superar las 3 horas.");
            }
            return total;
        }
        return Double.parseDouble(texto.replace(',', '.'));
    }
}
