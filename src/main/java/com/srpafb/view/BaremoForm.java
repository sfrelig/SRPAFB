package com.srpafb.view;

import com.srpafb.dao.BaremoDAO;
import com.srpafb.dao.CategoriaDAO;
import com.srpafb.dao.PruebaDAO;
import com.srpafb.model.Baremo;
import com.srpafb.model.Categoria;
import com.srpafb.model.Prueba;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class BaremoForm extends JDialog {

    private final BaremoView parent;
    private final Baremo baremo;
    private final BaremoDAO baremoDAO;
    private final PruebaDAO pruebaDAO;
    private final CategoriaDAO categoriaDAO;

    private JComboBox<Prueba> comboPrueba;
    private JComboBox<Categoria> comboCategoria;
    private JComboBox<String> comboSexo;
    private JTextField txtMinValor;
    private JTextField txtMaxValor;
    private JTextField txtPuntaje;

    public BaremoForm(BaremoView parent, Baremo baremo) {
        super(parent, true);
        this.parent = parent;
        this.baremo = baremo;
        this.baremoDAO = new BaremoDAO();
        this.pruebaDAO = new PruebaDAO();
        this.categoriaDAO = new CategoriaDAO();

        initializeUI();
        if (baremo != null) {
            cargarDatos();
        }
    }

    private void initializeUI() {
        setTitle(baremo == null ? "Agregar Baremo" : "Editar Baremo");
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        comboPrueba = new JComboBox<>();
        comboCategoria = new JComboBox<>();
        comboSexo = new JComboBox<>();
        txtMinValor = new JTextField();
        txtMaxValor = new JTextField();
        txtPuntaje = new JTextField();

        // Cargar combos
        List<Prueba> pruebas = pruebaDAO.obtenerTodas();
        for (Prueba p : pruebas) {
            comboPrueba.addItem(p);
        }

        List<Categoria> categorias = categoriaDAO.obtenerTodas();
        for (Categoria c : categorias) {
            comboCategoria.addItem(c);
        }

        comboSexo.addItem("M");
        comboSexo.addItem("F");

        addLabel(panel, "Prueba:", 0, gbc);
        addField(panel, comboPrueba, 0, gbc);
        addLabel(panel, "Categoría:", 1, gbc);
        addField(panel, comboCategoria, 1, gbc);
        addLabel(panel, "Sexo:", 2, gbc);
        addField(panel, comboSexo, 2, gbc);
        addLabel(panel, "Min Valor:", 3, gbc);
        addField(panel, txtMinValor, 3, gbc);
        addLabel(panel, "Max Valor:", 4, gbc);
        addField(panel, txtMaxValor, 4, gbc);
        addLabel(panel, "Puntaje:", 5, gbc);
        addField(panel, txtPuntaje, 5, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnGuardar = new JButton("Guardar");

        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardar();
            }
        });

        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnGuardar);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        add(panel);
    }

    private void cargarDatos() {
        if (baremo != null) {
            // Seleccionar en combos
            for (int i = 0; i < comboPrueba.getItemCount(); i++) {
                if (comboPrueba.getItemAt(i).getId() == baremo.getPruebaId()) {
                    comboPrueba.setSelectedIndex(i);
                    break;
                }
            }
            for (int i = 0; i < comboCategoria.getItemCount(); i++) {
                if (comboCategoria.getItemAt(i).getId() == baremo.getCategoriaId()) {
                    comboCategoria.setSelectedIndex(i);
                    break;
                }
            }
            comboSexo.setSelectedItem(baremo.getGenero());
            txtMinValor.setText(String.valueOf(baremo.getMinValor()));
            txtMaxValor.setText(String.valueOf(baremo.getMaxValor()));
            txtPuntaje.setText(String.valueOf(baremo.getPuntaje()));
        }
    }

    private void guardar() {
        try {
            Baremo b = new Baremo();
            if (baremo != null) {
                b.setId(baremo.getId());
            }
            Prueba p = (Prueba) comboPrueba.getSelectedItem();
            Categoria c = (Categoria) comboCategoria.getSelectedItem();
            String s = (String) comboSexo.getSelectedItem();

            b.setPruebaId(p.getId());
            b.setCategoriaId(c.getId());
            b.setGenero(s);
            b.setMinValor(Double.parseDouble(txtMinValor.getText()));
            b.setMaxValor(Double.parseDouble(txtMaxValor.getText()));
            b.setPuntaje(Double.parseDouble(txtPuntaje.getText()));

            if (baremo == null) {
                baremoDAO.insertar(b);
            } else {
                baremoDAO.actualizar(b);
            }

            parent.refrescarTabla();
            dispose();
            JOptionPane.showMessageDialog(parent, "Registro guardado correctamente.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
        }
    }

    private void addLabel(JPanel panel, String text, int row, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(text), gbc);
    }

    private void addField(JPanel panel, JComponent component, int row, GridBagConstraints gbc) {
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(component, gbc);
    }
}