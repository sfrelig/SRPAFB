package com.srpafb.view;

import com.srpafb.dao.PersonaDAO;
import com.srpafb.model.Persona;

import javax.swing.*;
import java.awt.*;

public class PersonaForm extends JFrame {

    public PersonaForm() {
        setTitle("Registrar Persona");
        setSize(350, 300);
        setLayout(new GridLayout(6, 2));
        setLocationRelativeTo(null);

        JTextField txtNombre = new JTextField();
        JTextField txtApellido = new JTextField();
        JTextField txtDni = new JTextField();
        JTextField txtFecha = new JTextField(); // yyyy-mm-dd
        JTextField txtCategoria = new JTextField();

        JButton btnGuardar = new JButton("Guardar");

        add(new JLabel("Nombre:"));
        add(txtNombre);
        add(new JLabel("Apellido:"));
        add(txtApellido);
        add(new JLabel("DNI:"));
        add(txtDni);
        add(new JLabel("Fecha Nac (YYYY-MM-DD):"));
        add(txtFecha);
        add(new JLabel("Categoría ID:"));
        add(txtCategoria);

        add(btnGuardar);

        btnGuardar.addActionListener(e -> {
            Persona p = new Persona();
            p.setNombre(txtNombre.getText());
            p.setApellido(txtApellido.getText());
            p.setDni(txtDni.getText());
            p.setFechaNacimiento(java.time.LocalDate.parse(txtFecha.getText()));
            p.setCategoriaId(Integer.parseInt(txtCategoria.getText()));

            new PersonaDAO().insertar(p);
            JOptionPane.showMessageDialog(null, "Persona registrada");
        });

        setVisible(true);
    }
}