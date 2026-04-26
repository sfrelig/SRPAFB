package com.srpafb.view;

import com.srpafb.dao.CategoriaDAO;
import com.srpafb.dao.ResultadoDAO;
import com.srpafb.model.Categoria;
import com.srpafb.model.Persona;
import com.srpafb.model.Resultado;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResultadosView extends JFrame {

    private final DefaultTableModel tableModel;
    private final JTable table;
    private final ResultadoDAO resultadoDAO;
    private final CategoriaDAO categoriaDAO;
    private final EstadisticasPanel chartPanel;
    private final JLabel summaryLabel;
    private final JComboBox<String> comboEdad;
    private final JComboBox<String> comboSexo;
    private final JComboBox<Object> comboCategoria;
    private List<Resultado> resultados;
    private List<Resultado> resultadosFiltrados;

    public ResultadosView() {
        resultadoDAO = new ResultadoDAO();
        categoriaDAO = new CategoriaDAO();

        setTitle("Resultados PAFB");
        setSize(860, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        comboCategoria = new JComboBox<>();
        comboCategoria.addItem("Todas las categorías");
        for (Categoria c : categoriaDAO.obtenerTodas()) {
            comboCategoria.addItem(c);
        }

        comboSexo = new JComboBox<>(new String[]{"Todos", "Masculino", "Femenino", "Otro"});
        comboEdad = new JComboBox<>(new String[]{"Todas", "Menos de 18", "18-25", "26-35", "36-50", "51 o más"});

        JButton btnFiltrar = new JButton("Aplicar filtros");
        JButton btnReset = new JButton("Limpiar filtros");

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filtros"));
        filterPanel.add(new JLabel("Categoría:"));
        filterPanel.add(comboCategoria);
        filterPanel.add(new JLabel("Sexo:"));
        filterPanel.add(comboSexo);
        filterPanel.add(new JLabel("Edad:"));
        filterPanel.add(comboEdad);
        filterPanel.add(btnFiltrar);
        filterPanel.add(btnReset);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Persona", "Sexo", "Edad", "Categoría", "Prueba", "Valor", "Puntaje", "Fecha"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);
        table.setRowHeight(24);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(820, 260));

        JPanel topPanel = new JPanel(new BorderLayout(8, 8));
        topPanel.add(filterPanel, BorderLayout.NORTH);
        topPanel.add(new JLabel("Resultados registrados", SwingConstants.CENTER), BorderLayout.CENTER);
        topPanel.add(scrollPane, BorderLayout.SOUTH);

        chartPanel = new EstadisticasPanel();
        chartPanel.setPreferredSize(new Dimension(820, 280));

        summaryLabel = new JLabel();
        summaryLabel.setVerticalAlignment(SwingConstants.TOP);
        summaryLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel infoPanel = new JPanel(new BorderLayout(8, 8));
        infoPanel.add(chartPanel, BorderLayout.CENTER);
        infoPanel.add(summaryLabel, BorderLayout.SOUTH);

        JButton btnExportar = new JButton("Exportar a PDF");
        JButton btnRefrescar = new JButton("Refrescar");
        JButton btnEditar = new JButton("Editar resultado");
        JButton btnEliminar = new JButton("Eliminar resultado");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.add(btnRefrescar);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnExportar);

        root.add(topPanel, BorderLayout.NORTH);
        root.add(infoPanel, BorderLayout.CENTER);
        root.add(buttonPanel, BorderLayout.SOUTH);

        btnFiltrar.addActionListener(e -> aplicarFiltros());
        btnReset.addActionListener(e -> resetearFiltros());
        btnRefrescar.addActionListener(e -> cargarResultados());
        btnExportar.addActionListener(e -> exportarInformePDF());
        btnEditar.addActionListener(e -> editarResultadoSeleccionado());
        btnEliminar.addActionListener(e -> eliminarResultadoSeleccionado());

        add(root);
        cargarResultados();
        setVisible(true);
    }

    private void cargarResultados() {
        resultados = resultadoDAO.obtenerTodos();
        resultadosFiltrados = new ArrayList<Resultado>(resultados);
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        List<Resultado> filtrados = new ArrayList<Resultado>(resultados);
        Object categoriaSeleccionada = comboCategoria.getSelectedItem();
        String categoriaFiltro = categoriaSeleccionada instanceof Categoria ? ((Categoria) categoriaSeleccionada).getNombre() : "Todas las categorías";
        String sexoFiltro = (String) comboSexo.getSelectedItem();
        String edadFiltro = (String) comboEdad.getSelectedItem();

        if (!"Todas las categorías".equals(categoriaFiltro)) {
            filtrados = filtrados.stream().filter(r -> r.getPersona() != null && r.getPersona().getCategoria() != null && categoriaFiltro.equals(r.getPersona().getCategoria().getNombre())).collect(Collectors.toList());
        }

        if (!"Todos".equals(sexoFiltro)) {
            filtrados = filtrados.stream().filter(r -> r.getPersona() != null && sexoFiltro.equalsIgnoreCase(r.getPersona().getSexo())).collect(Collectors.toList());
        }

        if (!"Todas".equals(edadFiltro)) {
            filtrados = filtrados.stream().filter(r -> r.getPersona() != null && cumpleRangoEdad(r.getPersona().getFechaNacimiento(), edadFiltro)).collect(Collectors.toList());
        }

        resultadosFiltrados = filtrados;
        tableModel.setRowCount(0);
        for (Resultado r : filtrados) {
            Persona p = r.getPersona();
            tableModel.addRow(new Object[]{
                    r.getId(),
                    p != null ? p.toString() : "-",
                    p != null && p.getSexo() != null ? p.getSexo() : "-",
                    p != null ? calcularEdad(p.getFechaNacimiento()) : "-",
                    p != null && p.getCategoria() != null ? p.getCategoria().getNombre() : "-",
                    r.getPrueba() != null ? r.getPrueba().toString() : "-",
                    formatearValor(r),
                    String.format("%.2f", r.getPuntaje()),
                    r.getFecha().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            });
        }

        actualizarEstadisticas(filtrados);
    }

    private void resetearFiltros() {
        comboCategoria.setSelectedIndex(0);
        comboSexo.setSelectedIndex(0);
        comboEdad.setSelectedIndex(0);
        aplicarFiltros();
    }

    private boolean cumpleRangoEdad(LocalDate nacimiento, String rango) {
        if (nacimiento == null) {
            return false;
        }
        int edad = calcularEdad(nacimiento);
        if ("Menos de 18".equals(rango)) {
            return edad < 18;
        }
        if ("18-25".equals(rango)) {
            return edad >= 18 && edad <= 25;
        }
        if ("26-35".equals(rango)) {
            return edad >= 26 && edad <= 35;
        }
        if ("36-50".equals(rango)) {
            return edad >= 36 && edad <= 50;
        }
        if ("51 o más".equals(rango)) {
            return edad >= 51;
        }
        return true;
    }

    private void actualizarEstadisticas(List<Resultado> filtrados) {
        Map<String, Double> conteoPorCategoria = calcularConteoPorCategoria(filtrados);
        chartPanel.setData(conteoPorCategoria);

        StringBuilder resumen = new StringBuilder("<html><b>Estadísticas generales:</b><br>");
        resumen.append("Resultados visibles: ").append(filtrados.size()).append("<br>");

        double total = filtrados.stream().mapToDouble(Resultado::getValor).sum();
        double promedioGeneral = filtrados.isEmpty() ? 0.0 : total / filtrados.size();
        resumen.append(String.format("Valor promedio visible: %.2f<br>", promedioGeneral));

        if (conteoPorCategoria.isEmpty()) {
            resumen.append("No hay datos para el gráfico de torta.");
        } else {
            resumen.append("Distribución de resultados por categoría:<br>");
            conteoPorCategoria.forEach((categoria, valor) -> resumen.append(String.format("&nbsp;&nbsp;%s: %.0f<br>", categoria, valor)));
        }

        resumen.append("</html>");
        summaryLabel.setText(resumen.toString());
    }

    private Map<String, Double> calcularConteoPorCategoria(List<Resultado> lista) {
        Map<String, Double> conteos = new LinkedHashMap<>();
        for (Resultado r : lista) {
            String categoria = "(sin categoría)";
            if (r.getPersona() != null && r.getPersona().getCategoria() != null) {
                categoria = r.getPersona().getCategoria().getNombre();
            }
            conteos.put(categoria, conteos.getOrDefault(categoria, 0.0) + 1);
        }
        return conteos;
    }

    private String formatearValor(Resultado r) {
        if (r == null || r.getPrueba() == null || r.getPrueba().getNombre() == null) {
            return "-";
        }
        String nombre = r.getPrueba().getNombre().toLowerCase();
        if (nombre.contains("carrera") || nombre.contains("3200")) {
            return formatearSegundos(r.getValor());
        }
        return String.format("%.0f", r.getValor());
    }

    private String formatearSegundos(double valor) {
        long segundosTotales = Math.round(valor);
        long horas = segundosTotales / 3600;
        long minutos = (segundosTotales % 3600) / 60;
        long segundos = segundosTotales % 60;
        if (horas > 0) {
            return String.format("%d:%02d:%02d", horas, minutos, segundos);
        }
        return String.format("%02d:%02d", minutos, segundos);
    }

    private int calcularEdad(LocalDate nacimiento) {
        if (nacimiento == null) {
            return 0;
        }
        return Persona.calcularEdad(nacimiento);
    }

    private double calcularPromedioPuntajeVisible() {
        if (resultadosFiltrados == null || resultadosFiltrados.isEmpty()) {
            return 0.0;
        }
        return resultadosFiltrados.stream().mapToDouble(Resultado::getPuntaje).average().orElse(0.0);
    }

    private void editarResultadoSeleccionado() {
        int fila = table.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un resultado para editar.", "Editar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(table.getValueAt(fila, 0).toString());
        Resultado resultado = resultadoDAO.obtenerPorId(id);
        if (resultado == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el resultado seleccionado.", "Editar", JOptionPane.ERROR_MESSAGE);
            return;
        }
        new ResultadoEditForm(this, resultado, this::cargarResultados);
    }

    private void eliminarResultadoSeleccionado() {
        int fila = table.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un resultado para eliminar.", "Eliminar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(table.getValueAt(fila, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar este resultado?", "Eliminar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            resultadoDAO.eliminar(id);
            cargarResultados();
            JOptionPane.showMessageDialog(this, "Resultado eliminado correctamente.", "Eliminar", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar el resultado: " + ex.getMessage(), "Eliminar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportarInformePDF() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("informe_resultados.pdf"));
        int opcion = chooser.showSaveDialog(this);
        if (opcion != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = chooser.getSelectedFile();
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);

            PDPageContentStream cs = new PDPageContentStream(document, page);
            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
            cs.newLineAtOffset(50, 740);
            cs.showText("Informe de Resultados PAFB");
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA, 12);
            cs.newLineAtOffset(50, 720);
            cs.showText("Filtros aplicados: " + comboCategoria.getSelectedItem() + ", " + comboSexo.getSelectedItem() + ", " + comboEdad.getSelectedItem());
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA, 12);
            cs.newLineAtOffset(50, 700);
            cs.showText("Total de resultados visibles: " + tableModel.getRowCount());
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA, 12);
            cs.newLineAtOffset(50, 680);
            cs.showText(String.format("Valor promedio visible: %.2f", calcularPromedioVisible()));
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA, 12);
            cs.newLineAtOffset(50, 660);
            cs.showText(String.format("Puntaje promedio visible: %.2f", calcularPromedioPuntajeVisible()));
            cs.endText();

            int yTexto = 640;
            Map<String, Double> conteo = calcularConteoPorCategoria(resultadosFiltrados != null ? resultadosFiltrados : resultados);
            for (Map.Entry<String, Double> entry : conteo.entrySet()) {
                if (yTexto < 150) {
                    cs.close();
                    page = new PDPage(PDRectangle.LETTER);
                    document.addPage(page);
                    cs = new PDPageContentStream(document, page);
                    yTexto = 720;
                }
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.newLineAtOffset(50, yTexto);
                cs.showText(String.format("- %s: %.0f resultados", entry.getKey(), entry.getValue()));
                cs.endText();
                yTexto -= 18;
            }

            BufferedImage image = crearImagenDeGrafico();
            if (image != null) {
                org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject pdImage = LosslessFactory.createFromImage(document, image);
                float width = 480;
                float height = pdImage.getHeight() * width / pdImage.getWidth();
                cs.drawImage(pdImage, 50, 240, width, height);
            }

            cs.close();
            document.save(archivo);
            JOptionPane.showMessageDialog(this, "Informe PDF exportado correctamente:\n" + archivo.getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error exportando PDF: " + ex.getMessage(), "PDF", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double calcularPromedioVisible() {
        int filas = tableModel.getRowCount();
        if (filas == 0) {
            return 0.0;
        }
        double total = 0.0;
        for (int i = 0; i < filas; i++) {
            Object valor = tableModel.getValueAt(i, 6);
            if (valor instanceof Number) {
                total += ((Number) valor).doubleValue();
            } else {
                total += Double.parseDouble(valor.toString());
            }
        }
        return total / filas;
    }

    private BufferedImage crearImagenDeGrafico() {
        BufferedImage image = new BufferedImage(chartPanel.getWidth(), chartPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        chartPanel.paint(g2);
        g2.dispose();
        return image;
    }

    private static class EstadisticasPanel extends JPanel {

        private Map<String, Double> data = new LinkedHashMap<>();

        public void setData(Map<String, Double> data) {
            this.data = data;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight());

            if (data == null || data.isEmpty()) {
                g2.setColor(Color.DARK_GRAY);
                g2.drawString("No hay datos para el gráfico de torta.", 20, 30);
                return;
            }

            double total = data.values().stream().mapToDouble(Double::doubleValue).sum();
            int diameter = Math.min(getWidth(), getHeight()) - 120;
            int centerX = getWidth() / 2 - diameter / 2;
            int centerY = getHeight() / 2 - diameter / 2;

            int startAngle = 0;
            int colorIndex = 0;
            Color[] palette = new Color[]{new Color(0x4caf50), new Color(0xff9800), new Color(0x2196f3), new Color(0xe91e63), new Color(0x9c27b0), new Color(0x03a9f4)};

            for (Map.Entry<String, Double> entry : data.entrySet()) {
                int angle = (int) Math.round(entry.getValue() / total * 360);
                g2.setColor(palette[colorIndex % palette.length]);
                g2.fillArc(centerX, centerY, diameter, diameter, startAngle, angle);
                g2.setColor(Color.WHITE);
                g2.drawArc(centerX, centerY, diameter, diameter, startAngle, angle);

                int legendX = centerX + diameter + 20;
                int legendY = centerY + 20 + colorIndex * 24;
                g2.setColor(palette[colorIndex % palette.length]);
                g2.fillRect(legendX, legendY, 16, 16);
                g2.setColor(Color.DARK_GRAY);
                g2.drawString(entry.getKey() + " (" + String.format("%.0f", entry.getValue()) + ")", legendX + 22, legendY + 12);

                startAngle += angle;
                colorIndex++;
            }
        }
    }
}
