package com.srpafb.view;

import com.srpafb.dao.PersonaDAO;
import com.srpafb.exception.AppException;
import com.srpafb.exception.ExceptionHandler;
import com.srpafb.model.Persona;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class PersonasView extends JFrame {

    private static final Logger logger = LoggerFactory.getLogger(PersonasView.class);
    
    private final PersonaDAO personaDAO;
    private DefaultTableModel tableModel;
    private JTable table;

    public PersonasView() {
        personaDAO = new PersonaDAO();

        try {
            initializeUI();
            cargarPersonas();
            ExceptionHandler.logInfo("Vista de Personas inicializada correctamente");
        } catch (AppException ex) {
            ExceptionHandler.handle(ex);
            dispose();
        } catch (Exception ex) {
            ExceptionHandler.handle(ex, AppException.ErrorType.SYSTEM_ERROR,
                "Error al inicializar la vista de personas");
            dispose();
        }
    }

    private void initializeUI() {
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

        btnAgregar.addActionListener(e -> abrirFormulario());
        btnEliminar.addActionListener(e -> eliminarPersonaSeleccionada());
        btnRefrescar.addActionListener(e -> cargarPersonas());

        add(root);
        setVisible(true);
    }

    private void abrirFormulario() {
        try {
            new PersonaForm();
            ExceptionHandler.logInfo("Formulario de Persona abierto");
        } catch (AppException ex) {
            ExceptionHandler.handle(ex);
        } catch (Exception ex) {
            ExceptionHandler.handle(ex, AppException.ErrorType.SYSTEM_ERROR,
                "No se puede abrir el formulario de persona");
        }
    }

    private void cargarPersonas() {
        try {
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
            
            ExceptionHandler.logInfo("Se cargaron " + personas.size() + " personas exitosamente");
        } catch (AppException ex) {
            ExceptionHandler.handle(ex);
        } catch (Exception ex) {
            ExceptionHandler.handle(ex, AppException.ErrorType.DATABASE_OPERATION,
                "No se pudieron cargar las personas de la base de datos");
        }
    }

    private int calcularEdad(LocalDate nacimiento) {
        if (nacimiento == null) {
            return 0;
        }
        return Period.between(nacimiento, LocalDate.now()).getYears();
    }

    private void eliminarPersonaSeleccionada() {
        try {
            int fila = table.getSelectedRow();
            if (fila < 0) {
                ExceptionHandler.showUserDialog(
                    "Seleccione una persona para eliminar.",
                    "Eliminar Persona"
                );
                return;
            }

            int modeloFila = table.convertRowIndexToModel(fila);
            int id = (int) tableModel.getValueAt(modeloFila, 0);
            String nombrePersona = (String) tableModel.getValueAt(modeloFila, 2);
            
            int opcion = ExceptionHandler.showConfirmDialog(
                "¿Está seguro de eliminar a " + nombrePersona + "?\nEsta acción no se puede deshacer.",
                "Confirmar eliminación"
            );

            if (opcion == JOptionPane.YES_OPTION) {
                try {
                    personaDAO.eliminar(id);
                    ExceptionHandler.showInfoDialog(
                        "Persona " + nombrePersona + " eliminada correctamente.",
                        "Eliminar Persona"
                    );
                    ExceptionHandler.logInfo("Persona ID: " + id + " eliminada exitosamente");
                    cargarPersonas();
                } catch (AppException ex) {
                    ExceptionHandler.handle(ex);
                } catch (Exception ex) {
                    ExceptionHandler.handle(ex, AppException.ErrorType.DATABASE_OPERATION,
                        "No se pudo eliminar la persona. Verifique si existen resultados asociados.\nDetalles: " + ex.getMessage());
                    ExceptionHandler.logError("Error al eliminar persona ID: " + id, ex);
                }
            }
        } catch (Exception ex) {
            ExceptionHandler.handle(ex, AppException.ErrorType.SYSTEM_ERROR,
                "Error al procesar la eliminación de la persona");
        }
    }
}
