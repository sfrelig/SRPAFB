package com.srpafb.dao;

import com.srpafb.connection.MySQLConnection;
import com.srpafb.model.Prueba;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PruebaDAO {

    public void insertar(Prueba p) {
        String sql = "INSERT INTO prueba (nombre, unidad) VALUES (?, ?)";

        try (Connection con = MySQLConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getUnidad());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error insertando prueba", e);
        }
    }

    public List<Prueba> obtenerTodas() {
        List<Prueba> lista = new ArrayList<>();
        String sql = "SELECT * FROM prueba";

        try (Connection con = MySQLConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Prueba(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("unidad")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo pruebas", e);
        }

        return lista;
    }

    public Prueba obtenerPorId(int id) {
        String sql = "SELECT * FROM prueba WHERE id = ?";

        try (Connection con = MySQLConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Prueba(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("unidad")
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo prueba por ID", e);
        }

        return null;
    }
}