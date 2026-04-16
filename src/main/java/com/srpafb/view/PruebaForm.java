package com.srpafb.view;

import com.srpafb.dao.PruebaDAO;
import com.srpafb.model.Prueba;

import javax.swing.*;
import java.awt.*;

public class PruebaForm extends JFrame {

    public PruebaForm() {
        setTitle("Registrar Prueba");
        setSize(380, 230);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtNombre = new JTextField();
        JComboBox<String> comboUnidad = new JComboBox<>(new String[]{"m", "seg", "rep"});
        comboUnidad.setToolTipText("Seleccione la unidad de medida para la prueba.");

        JButton btnGuardar = new JButton("Guardar");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        panel.add(new JLabel("Nombre:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(txtNombre, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        panel.add(new JLabel("Unidad:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(comboUnidad, gbc);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.add(btnGuardar);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        panel.add(footer, gbc);

        btnGuardar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                if (nombre.isEmpty()) {
                    throw new IllegalArgumentException("El nombre de la prueba es obligatorio.");
                }

                Prueba p = new Prueba();
                p.setNombre(nombre);
                p.setUnidad((String) comboUnidad.getSelectedItem());

                new PruebaDAO().insertar(p);
                JOptionPane.showMessageDialog(this, "Prueba registrada correctamente.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
            }
        });

        add(panel);
        setVisible(true);
    }
}