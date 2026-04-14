package com.srpafb.dao;

import com.srpafb.connection.MySQLConnection;
import com.srpafb.model.Persona;
import com.srpafb.model.Prueba;
import com.srpafb.model.Resultado;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ResultadoDAO {

    private final PersonaDAO personaDAO = new PersonaDAO();
    private final PruebaDAO pruebaDAO = new PruebaDAO();

    public void insertar(Resultado r) {
        String sql = "INSERT INTO resultado (persona_id, prueba_id, fecha, valor) VALUES (?, ?, ?, ?)";

        try (Connection con = MySQLConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, r.getPersona().getId());
            ps.setInt(2, r.getPrueba().getId());
            ps.setDate(3, Date.valueOf(r.getFecha()));
            ps.setDouble(4, r.getValor());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error insertando resultado", e);
        }
    }

    public List<Resultado> obtenerTodos() {
        List<Resultado> lista = new ArrayList<>();
        String sql = "SELECT * FROM resultado";

        try (Connection con = MySQLConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Persona p = personaDAO.obtenerPorId(rs.getInt("persona_id"));
                Prueba pr = pruebaDAO.obtenerPorId(rs.getInt("prueba_id"));

                Resultado r = new Resultado(
                        rs.getInt("id"),
                        p,
                        pr,
                        rs.getDate("fecha").toLocalDate(),
                        rs.getDouble("valor")
                );

                lista.add(r);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo resultados", e);
        }

        return lista;
    }
}