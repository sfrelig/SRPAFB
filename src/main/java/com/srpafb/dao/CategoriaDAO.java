package com.srpafb.dao;

import com.srpafb.database.DatabaseConnection;
import com.srpafb.model.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public void insertar(Categoria c) {
        String sql = "INSERT INTO categoria (nombre) VALUES (?)";

        try (Connection con = Connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error insertando categoría", e);
        }
    }

    public List<Categoria> obtenerTodas() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT id, nombre FROM categoria";

        try (Connection con = Connection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Categoria c = new Categoria(
                        rs.getInt("id"),
                        rs.getString("nombre")
                );
                lista.add(c);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo categorías", e);
        }

        return lista;
    }

    public Categoria obtenerPorId(int id) {
        String sql = "SELECT id, nombre FROM categoria WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Categoria(rs.getInt("id"), rs.getString("nombre"));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo categoría por ID", e);
        }

        return null;
    }
}