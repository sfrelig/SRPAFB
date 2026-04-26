package com.srpafb.view;

import com.srpafb.dao.ResultadoInformeDAO;
import com.srpafb.exception.AppException;
import com.srpafb.exception.ExceptionHandler;
import com.srpafb.model.ResultadoInforme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ResultadosInformeView extends JFrame {

    private static final Logger logger = LoggerFactory.getLogger(ResultadosInformeView.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    private final ResultadoInformeDAO informeDAO = new ResultadoInformeDAO();
    private DefaultTableModel tableModel;
    private JTable table;

    public ResultadosInformeView() {
        try {
            initializeUI();
            cargarInforme();
            ExceptionHandler.logInfo("Vista de Informe de Resultados inicializada correctamente");
        } catch (AppException ex) {
            ExceptionHandler.handle(ex);
            dispose();
        } catch (Exception ex) {
            ExceptionHandler.handle(ex, AppException.ErrorType.SYSTEM_ERROR,
                "Error al inicializar el informe de resultados");
            dispose();
        }
    }

    private void initializeUI() {
        setTitle("Informe de Resultados PAFB");
        setSize(1400, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Título
        JLabel titleLabel = new JLabel("INFORME DE RESULTADOS DE PRUEBAS DE APTITUD FÍSICA BÁSICA", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Tabla
        String[] columnNames = {
            "Grado", "Apellido y Nombre", "DNI", "Fecha Nacimiento", "Peso (kg)", "Talla (m)", "IMC",
            "Sexo", "Edad", "Categoría",
            "Flexiones Cant.", "Flexiones Pje.", "Abdominales Cant.", "Abdominales Pje.",
            "Barras Cant.", "Barras Pje.", "Carrera Tiempo", "Carrera Pje.",
            "Promedio", "Aprueba"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 19) { // Aprueba
                    return Boolean.class;
                }
                return String.class;
            }
        };

        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);

        // Ajustar anchos de columnas
        table.getColumnModel().getColumn(0).setPreferredWidth(80);  // Grado
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Apellido y Nombre
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // DNI
        table.getColumnModel().getColumn(3).setPreferredWidth(120); // Fecha
        table.getColumnModel().getColumn(4).setPreferredWidth(80);  // Peso
        table.getColumnModel().getColumn(5).setPreferredWidth(80);  // Talla
        table.getColumnModel().getColumn(6).setPreferredWidth(60);  // IMC
        table.getColumnModel().getColumn(7).setPreferredWidth(50);  // Sexo
        table.getColumnModel().getColumn(8).setPreferredWidth(50);  // Edad
        table.getColumnModel().getColumn(9).setPreferredWidth(100); // Categoría
        table.getColumnModel().getColumn(10).setPreferredWidth(100); // Flexiones Cant
        table.getColumnModel().getColumn(11).setPreferredWidth(100); // Flexiones Pje
        table.getColumnModel().getColumn(12).setPreferredWidth(120); // Abdominales Cant
        table.getColumnModel().getColumn(13).setPreferredWidth(120); // Abdominales Pje
        table.getColumnModel().getColumn(14).setPreferredWidth(80);  // Barras Cant
        table.getColumnModel().getColumn(15).setPreferredWidth(80);  // Barras Pje
        table.getColumnModel().getColumn(16).setPreferredWidth(100); // Carrera Tiempo
        table.getColumnModel().getColumn(17).setPreferredWidth(100); // Carrera Pje
        table.getColumnModel().getColumn(18).setPreferredWidth(80);  // Promedio
        table.getColumnModel().getColumn(19).setPreferredWidth(80);  // Aprueba

        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnExportarPDF = new JButton("Exportar PDF");
        JButton btnCerrar = new JButton("Cerrar");

        buttonPanel.add(btnActualizar);
        buttonPanel.add(btnExportarPDF);
        buttonPanel.add(btnCerrar);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Eventos
        btnActualizar.addActionListener(e -> cargarInforme());
        btnExportarPDF.addActionListener(e -> exportarPDF());
        btnCerrar.addActionListener(e -> dispose());

        add(mainPanel);
        setVisible(true);
    }

    private void cargarInforme() {
        try {
            tableModel.setRowCount(0);
            List<ResultadoInforme> informes = informeDAO.obtenerInformeResultados();

            for (ResultadoInforme informe : informes) {
                Object[] row = {
                    informe.getGrado() != null ? informe.getGrado() : "",
                    informe.getApellidoNombre(),
                    informe.getDni(),
                    informe.getFechaNacimiento() != null ? informe.getFechaNacimiento().format(DATE_FORMATTER) : "",
                    DECIMAL_FORMAT.format(informe.getPeso()),
                    DECIMAL_FORMAT.format(informe.getTalla()),
                    DECIMAL_FORMAT.format(informe.getImc()),
                    informe.getSexo(),
                    String.valueOf(informe.getEdad()),
                    informe.getCategoria() != null ? informe.getCategoria() : "",
                    String.valueOf(informe.getFlexionesCantidad()),
                    DECIMAL_FORMAT.format(informe.getFlexionesPuntaje()),
                    String.valueOf(informe.getAbdominalesCantidad()),
                    DECIMAL_FORMAT.format(informe.getAbdominalesPuntaje()),
                    String.valueOf(informe.getBarrasCantidad()),
                    DECIMAL_FORMAT.format(informe.getBarrasPuntaje()),
                    formatCarreraTiempo(informe.getCarreraTiempo()),
                    DECIMAL_FORMAT.format(informe.getCarreraPuntaje()),
                    DECIMAL_FORMAT.format(informe.getPromedio()),
                    informe.isAprueba()
                };
                tableModel.addRow(row);
            }

            ExceptionHandler.logInfo("Cargado informe con " + informes.size() + " registros");

        } catch (AppException ex) {
            ExceptionHandler.handle(ex);
        } catch (Exception ex) {
            ExceptionHandler.handle(ex, AppException.ErrorType.DATABASE_OPERATION,
                "Error cargando informe de resultados");
        }
    }

    private String formatCarreraTiempo(double segundos) {
        if (segundos <= 0) return "0";

        int horas = (int) (segundos / 3600);
        int minutos = (int) ((segundos % 3600) / 60);
        int seg = (int) (segundos % 60);

        if (horas > 0) {
            return String.format("%d:%02d:%02d", horas, minutos, seg);
        } else {
            return String.format("%d:%02d", minutos, seg);
        }
    }

    private void exportarPDF() {
        try {
            // Implementación futura para exportar a PDF
            ExceptionHandler.showInfoDialog("Funcionalidad de exportación PDF no implementada aún", "Pendiente");
        } catch (Exception ex) {
            ExceptionHandler.handle(ex, AppException.ErrorType.SYSTEM_ERROR,
                "Error exportando PDF");
        }
    }
}