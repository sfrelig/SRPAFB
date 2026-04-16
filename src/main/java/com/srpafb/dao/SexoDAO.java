package com.srpafb.dao;

import com.srpafb.connection.MySQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SexoDAO {

    public List<String> obtenerTodos() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT nombre FROM sexo ORDER BY nombre";

        try (Connection con = MySQLConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo sexos", e);
        }

        return lista;
    }

    public Integer obtenerIdPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT id FROM sexo WHERE LOWER(nombre) = LOWER(?)";

        try (Connection con = MySQLConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo id de sexo", e);
        }

        return null;
    }
}
