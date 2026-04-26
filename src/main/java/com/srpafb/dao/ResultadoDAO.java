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
    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    public void insertar(Resultado r) {
        String sql = "INSERT INTO resultado (persona_id, prueba_id, fecha, valor) VALUES (?, ?, ?, ?)";

        try (Connection con = MySQLConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, r.getPersona().getId());
            ps.setInt(2, r.getPrueba().getId());
            ps.setDate(3, Date.valueOf(r.getFecha()));
            ps.setDouble(4, r.getValor());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    r.setId(keys.getInt(1));
                }
            }

            String detalle = String.format("persona_id=%d, prueba_id=%d, valor=%.2f, fecha=%s",
                    r.getPersona().getId(), r.getPrueba().getId(), r.getValor(), r.getFecha());
            auditoriaDAO.registrar("resultado", r.getId(), "INSERT", detalle, "SISTEMA");

        } catch (SQLException e) {
            throw new RuntimeException("Error insertando resultado", e);
        }
    }

    public Resultado obtenerPorId(int id) {
        String sql = "SELECT * FROM resultado WHERE id = ?";

        try (Connection con = MySQLConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Persona p = personaDAO.obtenerPorId(rs.getInt("persona_id"));
                Prueba pr = pruebaDAO.obtenerPorId(rs.getInt("prueba_id"));
                return new Resultado(
                        rs.getInt("id"),
                        p,
                        pr,
                        rs.getDate("fecha").toLocalDate(),
                        rs.getDouble("valor")
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo resultado por ID", e);
        }

        return null;
    }

    public void actualizar(Resultado r) {
        Resultado anterior = obtenerPorId(r.getId());
        String sql = "UPDATE resultado SET fecha = ?, valor = ? WHERE id = ?";

        try (Connection con = MySQLConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(r.getFecha()));
            ps.setDouble(2, r.getValor());
            ps.setInt(3, r.getId());
            ps.executeUpdate();

            String detalle = String.format("antes: fecha=%s, valor=%.2f; despues: fecha=%s, valor=%.2f",
                    anterior != null ? anterior.getFecha() : null,
                    anterior != null ? anterior.getValor() : 0.0,
                    r.getFecha(),
                    r.getValor());
            auditoriaDAO.registrar("resultado", r.getId(), "UPDATE", detalle, "SISTEMA");

        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando resultado", e);
        }
    }

    public void eliminar(int id) {
        Resultado anterior = obtenerPorId(id);
        String sql = "DELETE FROM resultado WHERE id = ?";

        try (Connection con = MySQLConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

            String detalle = anterior != null ? String.format("persona_id=%d, prueba_id=%d, valor=%.2f, fecha=%s",
                    anterior.getPersona().getId(), anterior.getPrueba().getId(), anterior.getValor(), anterior.getFecha()) : "registro inexistente";
            auditoriaDAO.registrar("resultado", id, "DELETE", detalle, "SISTEMA");

        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando resultado", e);
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