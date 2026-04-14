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

    public ResultadoForm() {

        setTitle("Registro de Resultados PAFB");
        setSize(400, 300);
        setLayout(new GridLayout(5, 2));
        setLocationRelativeTo(null);

        comboPersona = new JComboBox<>();
        comboPrueba = new JComboBox<>();
        txtValor = new JTextField();
        txtFecha = new JTextField();

        cargarPersonas();
        cargarPruebas();

        add(new JLabel("Persona:"));
        add(comboPersona);

        add(new JLabel("Prueba:"));
        add(comboPrueba);

        add(new JLabel("Valor:"));
        add(txtValor);

        add(new JLabel("Fecha (YYYY-MM-DD):"));
        add(txtFecha);

        JButton btnGuardar = new JButton("Guardar");
        add(btnGuardar);

        btnGuardar.addActionListener(e -> guardar());

        setVisible(true);
    }

    private void cargarPersonas() {
        PersonaDAO dao = new PersonaDAO();
        List<Persona> lista = dao.obtenerTodas();

        for (Persona p : lista) {
            comboPersona.addItem(p);
        }
    }

    private void cargarPruebas() {
        PruebaDAO dao = new PruebaDAO();
        List<Prueba> lista = dao.obtenerTodas();

        for (Prueba p : lista) {
            comboPrueba.addItem(p);
        }
    }

    private void guardar() {
        try {
            Resultado r = new Resultado();

            r.setPersona((Persona) comboPersona.getSelectedItem());
            r.setPrueba((Prueba) comboPrueba.getSelectedItem());
            r.setValor(Double.parseDouble(txtValor.getText()));
            r.setFecha(LocalDate.parse(txtFecha.getText()));

            new ResultadoDAO().insertar(r);

            JOptionPane.showMessageDialog(this, "Resultado guardado");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}