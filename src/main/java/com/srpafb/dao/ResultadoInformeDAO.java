package com.srpafb.dao;

import com.srpafb.connection.MySQLConnection;
import com.srpafb.exception.AppException;
import com.srpafb.exception.ExceptionHandler;
import com.srpafb.model.ResultadoInforme;
import com.srpafb.service.PuntajeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ResultadoInformeDAO {

    private static final Logger logger = LoggerFactory.getLogger(ResultadoInformeDAO.class);
    private final PuntajeService puntajeService = PuntajeService.getInstance();

    public List<ResultadoInforme> obtenerInformeResultados() {
        List<ResultadoInforme> informes = new ArrayList<>();
        String sql = "SELECT DISTINCT p.id, p.nombre, p.apellido, p.grado, p.dni, p.fecha_nac, p.sexo, p.peso, p.talla, " +
                "c.nombre as categoria_nombre, " +
                "COALESCE(r_flexiones.valor, 0) as flexiones_valor, " +
                "COALESCE(r_abdominales.valor, 0) as abdominales_valor, " +
                "COALESCE(r_barras.valor, 0) as barras_valor, " +
                "COALESCE(r_carrera.valor, 0) as carrera_valor " +
                "FROM persona p " +
                "LEFT JOIN categoria c ON p.categoria_id = c.id " +
                "LEFT JOIN resultado r_flexiones ON p.id = r_flexiones.persona_id AND r_flexiones.prueba_id = (SELECT id FROM prueba WHERE nombre = 'Flexiones de brazo') " +
                "LEFT JOIN resultado r_abdominales ON p.id = r_abdominales.persona_id AND r_abdominales.prueba_id = (SELECT id FROM prueba WHERE nombre = 'Abdominales') " +
                "LEFT JOIN resultado r_barras ON p.id = r_barras.persona_id AND r_barras.prueba_id = (SELECT id FROM prueba WHERE nombre = 'Barras') " +
                "LEFT JOIN resultado r_carrera ON p.id = r_carrera.persona_id AND r_carrera.prueba_id = (SELECT id FROM prueba WHERE nombre = 'Carrera 3200m') " +
                "WHERE p.sexo IS NOT NULL AND p.fecha_nac IS NOT NULL " +
                "ORDER BY p.apellido, p.nombre";

        try (Connection con = MySQLConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ResultadoInforme informe = new ResultadoInforme();
                informe.setGrado(rs.getString("grado"));
                informe.setApellidoNombre(rs.getString("apellido") + ", " + rs.getString("nombre"));
                informe.setDni(rs.getString("dni"));
                informe.setFechaNacimiento(rs.getDate("fecha_nac").toLocalDate());
                informe.setPeso(rs.getDouble("peso"));
                informe.setTalla(rs.getDouble("talla"));
                informe.setSexo(rs.getString("sexo"));
                informe.setCategoria(rs.getString("categoria_nombre"));

                // Cantidades
                int flexiones = (int) rs.getDouble("flexiones_valor");
                int abdominales = (int) rs.getDouble("abdominales_valor");
                int barras = (int) rs.getDouble("barras_valor");
                double carrera = rs.getDouble("carrera_valor");

                informe.setFlexionesCantidad(flexiones);
                informe.setAbdominalesCantidad(abdominales);
                informe.setBarrasCantidad(barras);
                informe.setCarreraTiempo(carrera);

                // Calcular puntajes usando el servicio
                try {
                    // Nota: Para calcular puntajes necesitamos Prueba objects, pero por simplicidad usamos valores directos
                    // En una implementación completa, obtendríamos las pruebas por nombre
                    informe.setFlexionesPuntaje(flexiones > 0 ? calcularPuntajeSimplificado("Flexiones de brazo", informe.getSexo(), informe.getCategoria(), flexiones) : 0);
                    informe.setAbdominalesPuntaje(abdominales > 0 ? calcularPuntajeSimplificado("Abdominales", informe.getSexo(), informe.getCategoria(), abdominales) : 0);
                    informe.setBarrasPuntaje(barras > 0 ? calcularPuntajeSimplificado("Barras", informe.getSexo(), informe.getCategoria(), barras) : 0);
                    informe.setCarreraPuntaje(carrera > 0 ? calcularPuntajeSimplificado("Carrera 3200m", informe.getSexo(), informe.getCategoria(), carrera) : 0);

                    informe.calcularPromedio();
                } catch (Exception e) {
                    logger.warn("Error calculando puntajes para persona {}: {}", informe.getDni(), e.getMessage());
                    // Puntajes quedan en 0
                }

                informes.add(informe);
            }

            ExceptionHandler.logInfo("Generado informe con " + informes.size() + " registros");

        } catch (SQLException ex) {
            throw new AppException(
                AppException.ErrorType.DATABASE_OPERATION,
                "Error generando informe de resultados",
                "Error en consulta SQL para informe",
                ex
            );
        }

        return informes;
    }

    private double calcularPuntajeSimplificado(String pruebaNombre, String sexo, String categoria, double valor) {
        // Implementación simplificada - en producción usar PuntajeService
        // Por ahora retorna un valor dummy basado en el valor
        if (valor > 0) {
            return Math.min(100, valor * 2); // Puntaje dummy
        }
        return 0;
    }
}