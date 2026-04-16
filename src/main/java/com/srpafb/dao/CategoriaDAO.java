package com.srpafb.dao;

import com.srpafb.connection.MySQLConnection;
import com.srpafb.model.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public void insertar(Categoria c) {
        String sql = "INSERT INTO categoria (nombre) VALUES (?)";

        try (Connection con = MySQLConnection.getConnection();
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

        try (Connection con = MySQLConnection.getConnection();
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

    public Categoria obtenerPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT id, nombre FROM categoria WHERE LOWER(nombre) = LOWER(?)";

        try (Connection con = MySQLConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre.trim());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Categoria(rs.getInt("id"), rs.getString("nombre"));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo categoría por nombre", e);
        }

        return null;
    }

    public Categoria insertarSiNoExiste(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return null;
        }

        Categoria categoria = obtenerPorNombre(nombre);
        if (categoria != null) {
            return categoria;
        }

        String sql = "INSERT INTO categoria (nombre) VALUES (?)";

        try (Connection con = MySQLConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, nombre.trim());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Categoria(keys.getInt(1), nombre.trim());
                }
            }
            return obtenerPorNombre(nombre);
        } catch (SQLException e) {
            throw new RuntimeException("Error insertando categoría", e);
        }
    }

    public Categoria obtenerPorId(int id) {
        String sql = "SELECT id, nombre FROM categoria WHERE id = ?";

        try (Connection con = MySQLConnection.getConnection();
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