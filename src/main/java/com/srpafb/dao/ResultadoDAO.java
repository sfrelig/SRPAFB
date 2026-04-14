package dao;

import connection.MySQLConnection;
import model.Resultado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultadoDAO {

    public void insertar(Resultado r) {
        String sql = "INSERT INTO resultado (persona_id, prueba_id, valor, año) VALUES (?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, r.getPersona().getId());
            stmt.setInt(2, r.getPrueba().getId());
            stmt.setDouble(3, r.getValor());
            stmt.setInt(4, r.getAño());
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Resultado> listarPorPersona(int idPersona) {
        List<Resultado> lista = new ArrayList<>();
        String sql = "SELECT * FROM resultado WHERE persona_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPersona);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Resultado r = new Resultado(
                        rs.getInt("id"),
                        rs.getInt("persona_id"),
                        rs.getInt("prueba_id"),
                        rs.getDouble("valor"),
                        rs.getInt("año")
                );
                lista.add(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}