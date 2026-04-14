package com.srpafb.dao;

import com.srpafb.connection.MySQLConnection;
import com.srpafb.model.Categoria;
import com.srpafb.model.Persona;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAO {

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    public void insertar(Persona p) {
        String sql = "INSERT INTO persona (nombre, apellido, dni, fecha_nacimiento, categoria_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = MySQLConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getApellido());
            ps.setString(3, p.getDni());
            ps.setDate(4, Date.valueOf(p.getFechaNacimiento()));
            ps.setInt(5, p.getCategoria().getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error insertando persona", e);
        }
    }

    public List<Persona> obtenerTodas() {
        List<Persona> lista = new ArrayList<>();
        String sql = "SELECT * FROM persona";

        try (Connection con = MySQLConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Categoria c = categoriaDAO.obtenerPorId(rs.getInt("categoria_id"));

                Persona p = new Persona(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("dni"),
                        rs.getDate("fecha_nacimiento").toLocalDate(),
                        c
                );

                lista.add(p);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo personas", e);
        }

        return lista;
    }

    public Persona obtenerPorId(int id) {
        String sql = "SELECT * FROM persona WHERE id = ?";

        try (Connection con = MySQLConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Categoria c = categoriaDAO.obtenerPorId(rs.getInt("categoria_id"));
                return new Persona(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("dni"),
                        rs.getDate("fecha_nacimiento").toLocalDate(),
                        c
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo persona por ID", e);
        }

        return null;
    }
}