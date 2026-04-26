package com.srpafb.view;

import com.srpafb.dao.PersonaDAO;
import com.srpafb.dao.PruebaDAO;
import com.srpafb.dao.ResultadoDAO;
import com.srpafb.exception.AppException;
import com.srpafb.exception.ExceptionHandler;
import com.srpafb.model.Persona;
import com.srpafb.model.Prueba;
import com.srpafb.model.Resultado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ResultadoForm extends JFrame {

    private static final Logger logger = LoggerFactory.getLogger(ResultadoForm.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private JComboBox<Persona> comboPersona;
    private DatePicker datePicker;
    private JTextField txtFlexiones;
    private JTextField txtAbdominales;
    private JTextField txtBarras;
    private JTextField txtCarrera;

    public ResultadoForm() {
        try {
            initializeUI();
            ExceptionHandler.logInfo("Formulario de Resultados inicializado correctamente");
        } catch (AppException ex) {
            ExceptionHandler.handle(ex);
            dispose();
        } catch (Exception ex) {
            ExceptionHandler.handle(ex, AppException.ErrorType.SYSTEM_ERROR,
                "Error al inicializar el formulario de resultados");
            dispose();
        }
    }

    private void initializeUI() {
        setTitle("Registro de Pruebas PAFB");
        setSize(520, 420);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        comboPersona = new JComboBox<>();
        datePicker = new DatePicker();
        txtFlexiones = new JTextField();
        txtAbdominales = new JTextField();
        txtBarras = new JTextField();
        txtCarrera = new JTextField();
        txtCarrera.setToolTipText("Ingrese el tiempo en mm:ss o hh:mm:ss");

        cargarPersonas();

        addLabel(panel, "Persona:", 0, gbc);
        addField(panel, comboPersona, 0, gbc);
        addLabel(panel, "Fecha de prueba:", 1, gbc);
        addField(panel, datePicker, 1, gbc);
        addLabel(panel, "Flexiones de brazo:", 2, gbc);
        addField(panel, txtFlexiones, 2, gbc);
        addLabel(panel, "Abdominales:", 3, gbc);
        addField(panel, txtAbdominales, 3, gbc);
        addLabel(panel, "Barras:", 4, gbc);
        addField(panel, txtBarras, 4, gbc);
        addLabel(panel, "Carrera 3200m (mm:ss):", 5, gbc);
        addField(panel, txtCarrera, 5, gbc);

        JLabel mensaje = new JLabel("Ingrese los resultados de todas las pruebas en los casilleros correspondientes.");
        mensaje.setFont(mensaje.getFont().deriveFont(Font.PLAIN, 12f));
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        panel.add(mensaje, gbc);

        JButton btnGuardar = new JButton("Guardar resultados");
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(btnGuardar, gbc);

        btnGuardar.addActionListener(e -> guardarResultados());

        add(panel);
        setVisible(true);
    }

    private void addLabel(JPanel panel, String text, int row, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(text), gbc);
    }

    private void addField(JPanel panel, JComponent field, int row, GridBagConstraints gbc) {
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private void cargarPersonas() {
        try {
            PersonaDAO dao = new PersonaDAO();
            List<Persona> lista = dao.obtenerTodas();
            if (lista.isEmpty()) {
                comboPersona.addItem(new Persona());
            } else {
                for (Persona p : lista) {
                    comboPersona.addItem(p);
                }
            }
            ExceptionHandler.logDebug("Se cargaron " + lista.size() + " personas");
        } catch (AppException ex) {
            ExceptionHandler.handle(ex);
        } catch (Exception ex) {
            ExceptionHandler.handle(ex, AppException.ErrorType.DATABASE_OPERATION,
                "No se pudieron cargar las personas");
        }
    }

    private void guardarResultados() {
        try {
            Persona persona = (Persona) comboPersona.getSelectedItem();
            LocalDate fecha = datePicker.getDate();
            String flexionesTexto = txtFlexiones.getText().trim();
            String abdominalesTexto = txtAbdominales.getText().trim();
            String barrasTexto = txtBarras.getText().trim();
            String carreraTexto = txtCarrera.getText().trim();

            if (persona == null || persona.getId() == 0) {
                throw new AppException(
                    AppException.ErrorType.VALIDATION_ERROR,
                    "Seleccione una persona válida.",
                    "Intento de guardar resultado sin persona seleccionada"
                );
            }
            if (fecha == null) {
                throw new AppException(
                    AppException.ErrorType.VALIDATION_ERROR,
                    "Seleccione una fecha válida.",
                    "Intento de guardar resultado sin fecha"
                );
            }

            if (flexionesTexto.isEmpty() || abdominalesTexto.isEmpty() ||
                barrasTexto.isEmpty() || carreraTexto.isEmpty()) {
                throw new AppException(
                    AppException.ErrorType.VALIDATION_ERROR,
                    "Debe completar todos los valores de las pruebas.",
                    "Campos vacíos en formulario de resultados"
                );
            }

            PruebaDAO pruebaDAO = new PruebaDAO();
            ResultadoDAO resultadoDAO = new ResultadoDAO();

            guardarResultadoIndividual(resultadoDAO, pruebaDAO, persona, fecha, "Flexiones de brazo", "rep", flexionesTexto);
            guardarResultadoIndividual(resultadoDAO, pruebaDAO, persona, fecha, "Abdominales", "rep", abdominalesTexto);
            guardarResultadoIndividual(resultadoDAO, pruebaDAO, persona, fecha, "Barras", "rep", barrasTexto);
            guardarResultadoIndividual(resultadoDAO, pruebaDAO, persona, fecha, "Carrera 3200m", "seg", carreraTexto);

            ExceptionHandler.showInfoDialog(
                "Resultados guardados correctamente para " + persona.getNombre() + ".",
                "Resultados Guardados"
            );
            logger.info("Resultados guardados para persona ID: {}", persona.getId());
            limpiarFormulario();
        } catch (AppException ex) {
            ExceptionHandler.handle(ex);
        } catch (Exception ex) {
            ExceptionHandler.handle(ex, AppException.ErrorType.DATABASE_OPERATION,
                "Error al guardar los resultados");
        }
    }

    private void guardarResultadoIndividual(ResultadoDAO resultadoDAO, PruebaDAO pruebaDAO,
                                           Persona persona, LocalDate fecha, String nombrePrueba,
                                           String unidad, String valorTexto) {
        try {
            Prueba prueba = pruebaDAO.insertarSiNoExiste(nombrePrueba, unidad);
            Resultado resultado = new Resultado();
            resultado.setPersona(persona);
            resultado.setPrueba(prueba);
            resultado.setFecha(fecha);
            resultado.setValor(parseValor(nombrePrueba, valorTexto));
            resultadoDAO.insertar(resultado);
            logger.debug("Resultado guardado: {} - {} - Valor: {}", persona.getDni(), nombrePrueba, valorTexto);
        } catch (AppException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AppException(
                AppException.ErrorType.DATABASE_OPERATION,
                "Error al guardar el resultado de " + nombrePrueba + ".",
                "Error guardando resultado para prueba: " + nombrePrueba,
                ex
            );
        }
    }

    private double parseValor(String nombrePrueba, String texto) {
        try {
            if (nombrePrueba.toLowerCase().contains("carrera")) {
                return parseTiempoSegundos(texto);
            }
            return Double.parseDouble(texto.replace(',', '.'));
        } catch (NumberFormatException ex) {
            throw new AppException(
                AppException.ErrorType.VALIDATION_ERROR,
                "El valor para " + nombrePrueba + " no es válido. Ingrese un número.",
                "Error parseando valor para: " + nombrePrueba + " - Valor: " + texto,
                ex
            );
        }
    }

    private double parseTiempoSegundos(String texto) {
        String input = texto.trim();
        if (input.isEmpty()) {
            throw new AppException(
                AppException.ErrorType.VALIDATION_ERROR,
                "El tiempo de carrera es obligatorio.",
                "Campo de tiempo vacío"
            );
        }

        String[] partes = input.split(":");
        if (partes.length < 2 || partes.length > 3) {
            throw new AppException(
                AppException.ErrorType.VALIDATION_ERROR,
                "Formato de tiempo inválido. Use mm:ss o hh:mm:ss. Ej: 12:45 o 0:12:45.",
                "Formato de tiempo inválido: " + input
            );
        }

        int horas = 0;
        int minutos;
        int segundos;
        try {
            if (partes.length == 3) {
                horas = Integer.parseInt(partes[0].trim());
                minutos = Integer.parseInt(partes[1].trim());
                segundos = Integer.parseInt(partes[2].trim());
            } else {
                minutos = Integer.parseInt(partes[0].trim());
                segundos = Integer.parseInt(partes[1].trim());
            }
        } catch (NumberFormatException e) {
            throw new AppException(
                AppException.ErrorType.VALIDATION_ERROR,
                "Los valores de tiempo deben ser numéricos.",
                "Valor no numérico en tiempo: " + input,
                e
            );
        }

        if (horas < 0 || minutos < 0 || segundos < 0 || segundos >= 60 || minutos >= 60) {
            throw new AppException(
                AppException.ErrorType.VALIDATION_ERROR,
                "Tiempo inválido. Minutos y segundos deben estar entre 0 y 59.",
                "Valores fuera de rango: " + input
            );
        }

        long totalSegundos = horas * 3600L + minutos * 60L + segundos;
        if (totalSegundos > 10800) {
            throw new AppException(
                AppException.ErrorType.VALIDATION_ERROR,
                "El tiempo de carrera no puede superar las 3 horas.",
                "Tiempo demasiado largo: " + totalSegundos + " segundos"
            );
        }
        return totalSegundos;
    }

    private void limpiarFormulario() {
        datePicker.setDate(LocalDate.now());
        txtFlexiones.setText("");
        txtAbdominales.setText("");
        txtBarras.setText("");
        txtCarrera.setText("");
    }
}