package com.srpafb.dao;

import com.srpafb.connection.MySQLConnection;
import com.srpafb.model.Baremo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BaremoDAO {

    public double obtenerPuntaje(int pruebaId, int categoriaId, String genero, double valor) {
        String sql = "SELECT b.puntaje FROM baremo b " +
                "WHERE b.prueba_id = ? AND b.categoria_id = ? " +
                "AND b.genero = ? " +
                "AND ? BETWEEN b.valor_min AND b.valor_max " +
                "ORDER BY (b.valor_max - b.valor_min) ASC " +
                "LIMIT 1";

        try (Connection con = MySQLConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pruebaId);
            ps.setInt(2, categoriaId);
            ps.setString(3, genero);
            ps.setDouble(4, valor);

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

    public List<Baremo> obtenerTodos() {
        List<Baremo> lista = new ArrayList<>();
        String sql = "SELECT b.*, p.nombre as prueba_nombre, c.nombre as categoria_nombre " +
                "FROM baremo b " +
                "JOIN prueba p ON b.prueba_id = p.id " +
                "JOIN categoria c ON b.categoria_id = c.id " +
                "ORDER BY p.nombre, c.nombre, b.genero, b.valor_min";

        try (Connection con = MySQLConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Baremo b = new Baremo();
                b.setId(rs.getInt("id"));
                b.setPruebaId(rs.getInt("prueba_id"));
                b.setCategoriaId(rs.getInt("categoria_id"));
                b.setGenero(rs.getString("genero"));
                b.setMinValor(rs.getDouble("valor_min"));
                b.setMaxValor(rs.getDouble("valor_max"));
                b.setPuntaje(rs.getDouble("puntaje"));
                b.setPruebaNombre(rs.getString("prueba_nombre"));
                b.setCategoriaNombre(rs.getString("categoria_nombre"));
                lista.add(b);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo baremos", e);
        }

        return lista;
    }

    public void insertar(Baremo b) {
        String sql = "INSERT INTO baremo (prueba_id, categoria_id, genero, valor_min, valor_max, puntaje) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = MySQLConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, b.getPruebaId());
            ps.setInt(2, b.getCategoriaId());
            ps.setString(3, b.getGenero());
            ps.setDouble(4, b.getMinValor());
            ps.setDouble(5, b.getMaxValor());
            ps.setDouble(6, b.getPuntaje());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error insertando baremo", e);
        }
    }

    public void actualizar(Baremo b) {
        String sql = "UPDATE baremo SET prueba_id = ?, categoria_id = ?, genero = ?, valor_min = ?, valor_max = ?, puntaje = ? WHERE id = ?";

        try (Connection con = MySQLConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, b.getPruebaId());
            ps.setInt(2, b.getCategoriaId());
            ps.setString(3, b.getGenero());
            ps.setDouble(4, b.getMinValor());
            ps.setDouble(5, b.getMaxValor());
            ps.setDouble(6, b.getPuntaje());
            ps.setInt(7, b.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando baremo", e);
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM baremo WHERE id = ?";

        try (Connection con = MySQLConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando baremo", e);
        }
    }
}
