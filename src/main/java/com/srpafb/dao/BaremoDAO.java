package com.srpafb.dao;

import com.srpafb.connection.MySQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BaremoDAO {

    public double obtenerPuntaje(int pruebaId, int categoriaId, String sexo, double valor) {
        String sql = "SELECT b.puntaje FROM baremo b " +
                "JOIN sexo s ON b.sexo_id = s.id " +
                "WHERE b.prueba_id = ? AND b.categoria_id = ? " +
                "AND (LOWER(s.nombre) = LOWER(?) OR LOWER(s.nombre) = 'todos') " +
                "AND ? BETWEEN b.min_valor AND b.max_valor " +
                "ORDER BY CASE WHEN LOWER(s.nombre) = LOWER(?) THEN 0 ELSE 1 END, (b.max_valor - b.min_valor) ASC " +
                "LIMIT 1";

        try (Connection con = MySQLConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pruebaId);
            ps.setInt(2, categoriaId);
            ps.setString(3, sexo);
            ps.setDouble(4, valor);
            ps.setString(5, sexo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("puntaje");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo puntaje de baremo", e);
        }

        return -1;
    }
}
