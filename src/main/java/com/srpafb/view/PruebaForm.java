package view;

import dao.PruebaDAO;
import model.Prueba;

import javax.swing.*;
import java.awt.*;

public class pruebaForm extends JFrame {

    public pruebaForm() {
        setTitle("Registrar Prueba");
        setSize(300, 200);
        setLayout(new GridLayout(3, 2));
        setLocationRelativeTo(null);

        JTextField txtNombre = new JTextField();
        JTextField txtUnidad = new JTextField();

        JButton btnGuardar = new JButton("Guardar");

        add(new JLabel("Nombre:"));
        add(txtNombre);
        add(new JLabel("Unidad (m, seg, rep):"));
        add(txtUnidad);

        add(btnGuardar);

        btnGuardar.addActionListener(e -> {
            Prueba p = new Prueba();
            p.setNombre(txtNombre.getText());
            p.setUnidad(txtUnidad.getText());

            new PruebaDAO().insertar(p);
            JOptionPane.showMessageDialog(null, "Prueba registrada");
        });

        setVisible(true);
    }
}