package com.srpafb.view;

import com.srpafb.dao.BaremoDAO;
import com.srpafb.dao.CategoriaDAO;
import com.srpafb.dao.PruebaDAO;
import com.srpafb.exception.AppException;
import com.srpafb.exception.ExceptionHandler;
import com.srpafb.model.Baremo;
import com.srpafb.model.Categoria;
import com.srpafb.model.Prueba;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class BaremoView extends JFrame {

    private static final Logger logger = LoggerFactory.getLogger(BaremoView.class);

    private final BaremoDAO baremoDAO;
    private final PruebaDAO pruebaDAO;
    private final CategoriaDAO categoriaDAO;
    private DefaultTableModel tableModel;
    private JTable table;

    public BaremoView() {
        baremoDAO = new BaremoDAO();
        pruebaDAO = new PruebaDAO();
        categoriaDAO = new CategoriaDAO();

        try {
            initializeUI();
            cargarBaremos();
            ExceptionHandler.logInfo("Vista de Baremo inicializada correctamente");
        } catch (AppException ex) {
            ExceptionHandler.handle(ex);
            dispose();
        } catch (Exception ex) {
            ExceptionHandler.handle(ex, AppException.ErrorType.SYSTEM_ERROR,
                "Error al inicializar la vista de baremo");
            dispose();
        }
    }

    private void initializeUI() {
        setTitle("Gestión de Baremo");
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAgregar = new JButton("Agregar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnCerrar = new JButton("Cerrar");

        btnAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirFormulario(null);
            }
        });

        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int id = (int) tableModel.getValueAt(selectedRow, 0);
                    Baremo b = baremoDAO.obtenerTodos().stream().filter(baremo -> baremo.getId() == id).findFirst().orElse(null);
                    if (b != null) {
                        abrirFormulario(b);
                    }
                } else {
                    JOptionPane.showMessageDialog(BaremoView.this, "Seleccione un registro para editar.");
                }
            }
        });

        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int id = (int) tableModel.getValueAt(selectedRow, 0);
                    int confirm = JOptionPane.showConfirmDialog(BaremoView.this,
                        "¿Está seguro de eliminar este registro?", "Confirmar eliminación",
                        JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            baremoDAO.eliminar(id);
                            cargarBaremos();
                            JOptionPane.showMessageDialog(BaremoView.this, "Registro eliminado correctamente.");
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(BaremoView.this, "Error al eliminar: " + ex.getMessage());
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(BaremoView.this, "Seleccione un registro para eliminar.");
                }
            }
        });

        btnCerrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        buttonPanel.add(btnAgregar);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnCerrar);

        // Tabla
        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Prueba");
        tableModel.addColumn("Categoría");
        tableModel.addColumn("Sexo");
        tableModel.addColumn("Min Valor");
        tableModel.addColumn("Max Valor");
        tableModel.addColumn("Puntaje");

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);

        root.add(buttonPanel, BorderLayout.NORTH);
        root.add(scrollPane, BorderLayout.CENTER);

        add(root);
        setVisible(true);
    }

    private void cargarBaremos() {
        tableModel.setRowCount(0);
        List<Baremo> lista = baremoDAO.obtenerTodos();
        for (Baremo b : lista) {
            tableModel.addRow(new Object[]{
                b.getId(),
                b.getPruebaNombre(),
                b.getCategoriaNombre(),
                b.getGenero(),
                b.getMinValor(),
                b.getMaxValor(),
                b.getPuntaje()
            });
        }
    }

    private void abrirFormulario(Baremo baremo) {
        BaremoForm form = new BaremoForm(this, baremo);
        form.setVisible(true);
    }

    public void refrescarTabla() {
        cargarBaremos();
    }
}