package com.srpafb.dao;

import com.srpafb.connection.MySQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AuditoriaDAO {

    public void registrar(String entidad, int entidadId, String operacion, String detalle, String usuario) {
        String sql = "INSERT INTO auditoria (entidad, entidad_id, operacion, detalle, usuario) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = MySQLConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, entidad);
            ps.setInt(2, entidadId);
            ps.setString(3, operacion);
            ps.setString(4, detalle);
            ps.setString(5, usuario != null ? usuario : "SISTEMA");
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error registrando auditoría", e);
        }
    }
}
