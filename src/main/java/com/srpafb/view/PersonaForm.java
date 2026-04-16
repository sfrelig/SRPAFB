package com.srpafb.view;

import com.srpafb.dao.CategoriaDAO;
import com.srpafb.dao.PersonaDAO;
import com.srpafb.dao.SexoDAO;
import com.srpafb.model.Categoria;
import com.srpafb.model.Persona;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class PersonaForm extends JFrame {

    public PersonaForm() {
        setTitle("Registrar Persona");
        setSize(520, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtNombre = new JTextField();
        JTextField txtApellido = new JTextField();
        JTextField txtGrado = new JTextField();
        JTextField txtDni = new JTextField();
        JTextField txtFecha = new JTextField();
        txtFecha.setToolTipText("Formato: YYYY-MM-DD");
        JTextField txtPeso = new JTextField();
        JTextField txtTalla = new JTextField();
        JLabel lblImc = new JLabel("-");
        JLabel lblEdad = new JLabel("-");
        JLabel lblCategoria = new JLabel("-");
        JComboBox<String> comboSexo = new JComboBox<>();
        comboSexo.setToolTipText("Seleccione el sexo de la persona.");

        List<String> sexos = new SexoDAO().obtenerTodos();
        if (sexos.isEmpty()) {
            sexos = new ArrayList<>();
            sexos.add("Masculino");
            sexos.add("Femenino");
            sexos.add("Otro");
        }
        for (String sexo : sexos) {
            comboSexo.addItem(sexo);
        }

        addLabel(panel, "Nombre:", 0, gbc);
        addField(panel, txtNombre, 0, gbc);
        addLabel(panel, "Apellido:", 1, gbc);
        addField(panel, txtApellido, 1, gbc);
        addLabel(panel, "Grado:", 2, gbc);
        addField(panel, txtGrado, 2, gbc);
        addLabel(panel, "DNI:", 3, gbc);
        addField(panel, txtDni, 3, gbc);
        addLabel(panel, "Sexo:", 4, gbc);
        addField(panel, comboSexo, 4, gbc);
        addLabel(panel, "Fecha Nac (YYYY-MM-DD):", 5, gbc);
        addField(panel, txtFecha, 5, gbc);
        addLabel(panel, "Peso (kg):", 6, gbc);
        addField(panel, txtPeso, 6, gbc);
        addLabel(panel, "Talla (m):", 7, gbc);
        addField(panel, txtTalla, 7, gbc);
        addLabel(panel, "IMC:", 8, gbc);
        addField(panel, lblImc, 8, gbc);
        addLabel(panel, "Edad:", 9, gbc);
        addField(panel, lblEdad, 9, gbc);
        addLabel(panel, "Categoría:", 10, gbc);
        addField(panel, lblCategoria, 10, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnGuardar = new JButton("Guardar");
        buttonPanel.add(btnLimpiar);
        buttonPanel.add(btnGuardar);

        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        DocumentListener calculoListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizarCalculos(txtFecha, txtPeso, txtTalla, lblEdad, lblImc, lblCategoria);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizarCalculos(txtFecha, txtPeso, txtTalla, lblEdad, lblImc, lblCategoria);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                actualizarCalculos(txtFecha, txtPeso, txtTalla, lblEdad, lblImc, lblCategoria);
            }
        };

        txtFecha.getDocument().addDocumentListener(calculoListener);
        txtPeso.getDocument().addDocumentListener(calculoListener);
        txtTalla.getDocument().addDocumentListener(calculoListener);

        btnGuardar.addActionListener(e -> {
            try {
                validarTexto(txtNombre.getText(), "Nombre");
                validarTexto(txtApellido.getText(), "Apellido");
                validarTexto(txtGrado.getText(), "Grado");
                validarTexto(txtDni.getText(), "DNI");
                validarTexto(txtFecha.getText(), "Fecha de nacimiento");
                validarTexto(txtPeso.getText(), "Peso");
                validarTexto(txtTalla.getText(), "Talla");

                String dni = txtDni.getText().trim();
                PersonaDAO personaDAO = new PersonaDAO();
                if (personaDAO.obtenerPorDni(dni) != null) {
                    throw new IllegalArgumentException("Ya existe una persona con ese DNI.");
                }

                LocalDate fechaNacimiento = LocalDate.parse(txtFecha.getText().trim());
                int edad = Persona.calcularEdad(fechaNacimiento);
                if (edad <= 0) {
                    throw new IllegalArgumentException("La fecha de nacimiento no es válida.");
                }

                double peso = Double.parseDouble(txtPeso.getText().trim().replace(',', '.'));
                double talla = Double.parseDouble(txtTalla.getText().trim().replace(',', '.'));
                if (peso <= 0 || talla <= 0) {
                    throw new IllegalArgumentException("Peso y talla deben ser mayores que cero.");
                }

                double imc = Persona.calcularImc(peso, talla);
                String categoriaNombre = Persona.calcularCategoria(edad);
                Categoria categoria = new CategoriaDAO().insertarSiNoExiste(categoriaNombre);
                if (categoria == null) {
                    throw new IllegalArgumentException("No se pudo determinar la categoría automática.");
                }

                Persona p = new Persona();
                p.setNombre(txtNombre.getText().trim());
                p.setApellido(txtApellido.getText().trim());
                p.setGrado(txtGrado.getText().trim());
                p.setDni(dni);
                p.setSexo((String) comboSexo.getSelectedItem());
                p.setFechaNacimiento(fechaNacimiento);
                p.setPeso(peso);
                p.setTalla(talla);
                p.setImc(Math.round(imc * 100.0) / 100.0);
                p.setCategoria(categoria);
                p.setCategoriaId(categoria.getId());

                new PersonaDAO().insertar(p);

                lblEdad.setText(String.valueOf(edad));
                lblImc.setText(String.format("%.2f", p.getImc()));
                lblCategoria.setText(categoriaNombre);

                JOptionPane.showMessageDialog(this,
                        "Persona registrada correctamente.\nEdad: " + edad + "\nIMC: " + String.format("%.2f", p.getImc()) + "\nCategoría: " + categoriaNombre);
                btnLimpiar.doClick();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnLimpiar.addActionListener(e -> {
            txtNombre.setText("");
            txtApellido.setText("");
            txtGrado.setText("");
            txtDni.setText("");
            txtFecha.setText("");
            txtPeso.setText("");
            txtTalla.setText("");
            comboSexo.setSelectedIndex(0);
            lblEdad.setText("-");
            lblImc.setText("-");
            lblCategoria.setText("-");
        });

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

    private void actualizarCalculos(JTextField txtFecha, JTextField txtPeso, JTextField txtTalla,
                                    JLabel lblEdad, JLabel lblImc, JLabel lblCategoria) {
        try {
            LocalDate fechaNacimiento = LocalDate.parse(txtFecha.getText().trim());
            int edad = Persona.calcularEdad(fechaNacimiento);
            lblEdad.setText(String.valueOf(edad));
            double peso = Double.parseDouble(txtPeso.getText().trim().replace(',', '.'));
            double talla = Double.parseDouble(txtTalla.getText().trim().replace(',', '.'));
            double imc = Persona.calcularImc(peso, talla);
            lblImc.setText(imc > 0 ? String.format("%.2f", imc) : "-");
            lblCategoria.setText(Persona.calcularCategoria(edad));
        } catch (Exception ex) {
            lblEdad.setText("-");
            lblImc.setText("-");
            lblCategoria.setText("-");
        }
    }

    private void validarTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " es obligatorio.");
        }
    }
}