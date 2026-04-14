package view;

import dao.ResultadoDAO;
import model.Resultado;

import javax.swing.*;
import java.awt.*;

public class resultadoForm extends JFrame {

    public resultadoForm() {
        setTitle("Cargar Resultado");
        setSize(300, 250);
        setLayout(new GridLayout(5, 2));
        setLocationRelativeTo(null);

        JTextField txtPersona = new JTextField();
        JTextField txtPrueba = new JTextField();
        JTextField txtValor = new JTextField();
        JTextField txtAnio = new JTextField();

        JButton btnGuardar = new JButton("Guardar");

        add(new JLabel("ID Persona:"));
        add(txtPersona);
        add(new JLabel("ID Prueba:"));
        add(txtPrueba);
        add(new JLabel("Valor:"));
        add(txtValor);
        add(new JLabel("Año:"));
        add(txtAnio);

        add(btnGuardar);

        btnGuardar.addActionListener(e -> {
            Resultado r = new Resultado();
            persona p = new persona();
            p.setId(Integer.parseInt(txtPersona.getText()));

            r.setPersona(p);
            r.setPruebaId(Integer.parseInt(txtPrueba.getText()));
            r.setValor(Double.parseDouble(txtValor.getText()));
            r.setAño(Integer.parseInt(txtAnio.getText()));

            new ResultadoDAO().insertar(r);

            JOptionPane.showMessageDialog(null, "Resultado cargado");
        });

        setVisible(true);
    }
}