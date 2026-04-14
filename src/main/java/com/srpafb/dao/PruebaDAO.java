package dao;

import connection.MySQLConnection;
import model.Prueba;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PruebaDAO {

    public void insertar(Prueba p) {
        String sql = "INSERT INTO prueba (nombre, unidad) VALUES (?, ?)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getNombre());
            stmt.setString(2, p.getUnidad());
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Prueba> listar() {
        List<Prueba> lista = new ArrayList<>();
        String sql = "SELECT * FROM prueba";

        try (Connection conn = MySQLConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Prueba p = new Prueba(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("unidad")
                );
                lista.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}