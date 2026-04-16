package com.srpafb.view;

import com.srpafb.dao.PersonaDAO;
import com.srpafb.model.Persona;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class PersonasView extends JFrame {

    private final PersonaDAO personaDAO;
    private final DefaultTableModel tableModel;
    private final JTable table;

    public PersonasView() {
        personaDAO = new PersonaDAO();

        setTitle("Gestión de Personas");
        setSize(980, 420);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        tableModel = new DefaultTableModel(new Object[]{"ID", "Grado", "Apellido y Nombre", "DNI", "Sexo", "Edad", "Categoría", "Peso (kg)", "Talla (m)", "IMC"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);
        table.setRowHeight(24);

        JScrollPane scrollPane = new JScrollPane(table);
        root.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnAgregar = new JButton("Agregar Persona");
        JButton btnEliminar = new JButton("Eliminar Persona");
        JButton btnRefrescar = new JButton("Refrescar");
        buttonPanel.add(btnAgregar);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnRefrescar);
        root.add(buttonPanel, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> new PersonaForm());
        btnEliminar.addActionListener(e -> eliminarPersonaSeleccionada());
        btnRefrescar.addActionListener(e -> cargarPersonas());

        add(root);
        cargarPersonas();
        setVisible(true);
    }

    private void cargarPersonas() {
        List<Persona> personas = personaDAO.obtenerTodas();
        tableModel.setRowCount(0);

        for (Persona p : personas) {
            tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getGrado() != null ? p.getGrado() : "-",
                    p.getApellido() + " " + p.getNombre(),
                    p.getDni() != null ? p.getDni() : "-",
                    p.getSexo() != null ? p.getSexo() : "-",
                    p.getEdad(),
                    p.getCategoria() != null ? p.getCategoria().getNombre() : p.getCategoriaNombre(),
                    p.getPeso() > 0 ? String.format("%.2f", p.getPeso()) : "-",
                    p.getTalla() > 0 ? String.format("%.2f", p.getTalla()) : "-",
                    p.getImc() > 0 ? String.format("%.2f", p.getImc()) : "-"
            });
        }
    }

    private int calcularEdad(LocalDate nacimiento) {
        if (nacimiento == null) {
            return 0;
        }
        return Period.between(nacimiento, LocalDate.now()).getYears();
    }

    private void eliminarPersonaSeleccionada() {
        int fila = table.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una persona para eliminar.", "Eliminar", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modeloFila = table.convertRowIndexToModel(fila);
        int id = (int) tableModel.getValueAt(modeloFila, 0);
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar esta persona?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            try {
                personaDAO.eliminar(id);
                JOptionPane.showMessageDialog(this, "Persona eliminada correctamente.", "Eliminar", JOptionPane.INFORMATION_MESSAGE);
                cargarPersonas();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "No se pudo eliminar la persona. Verifique si existen resultados asociados o errores en la base de datos.\n" + ex.getMessage(),
                        "Error al eliminar", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
