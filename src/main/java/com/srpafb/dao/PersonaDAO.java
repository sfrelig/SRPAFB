package com.srpafb.dao;

import com.srpafb.connection.MySQLConnection;
import com.srpafb.model.Categoria;
import com.srpafb.model.Persona;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAO {

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    public void insertar(Persona p) {
        if (p.getDni() == null || p.getDni().trim().isEmpty()) {
            throw new IllegalArgumentException("DNI es obligatorio.");
        }
        if (obtenerPorDni(p.getDni().trim()) != null) {
            throw new IllegalArgumentException("Ya existe una persona con ese DNI.");
        }
        try (Connection con = MySQLConnection.getConnection()) {
            boolean tieneGrado = hasTableColumn(con, "persona", "grado");
            boolean tieneSexo = hasTableColumn(con, "persona", "sexo");
            boolean tienePeso = hasTableColumn(con, "persona", "peso");
            boolean tieneTalla = hasTableColumn(con, "persona", "talla");
            boolean tieneImc = hasTableColumn(con, "persona", "imc");

            StringBuilder columnas = new StringBuilder("nombre, apellido, dni, fecha_nacimiento, categoria_id");
            StringBuilder valores = new StringBuilder("?, ?, ?, ?, ?");

            if (tieneGrado) {
                columnas.append(", grado");
                valores.append(", ?");
            }
            if (tieneSexo) {
                columnas.append(", sexo");
                valores.append(", ?");
            }
            if (tienePeso) {
                columnas.append(", peso");
                valores.append(", ?");
            }
            if (tieneTalla) {
                columnas.append(", talla");
                valores.append(", ?");
            }
            if (tieneImc) {
                columnas.append(", imc");
                valores.append(", ?");
            }

            String sql = "INSERT INTO persona (" + columnas + ") VALUES (" + valores + ")";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                int index = 1;
                ps.setString(index++, p.getNombre());
                ps.setString(index++, p.getApellido());
                ps.setString(index++, p.getDni());
                ps.setDate(index++, Date.valueOf(p.getFechaNacimiento()));
                int categoriaId = p.getCategoria() != null ? p.getCategoria().getId() : p.getCategoriaId();
                ps.setInt(index++, categoriaId);

                if (tieneGrado) {
                    ps.setString(index++, p.getGrado());
                }
                if (tieneSexo) {
                    ps.setString(index++, p.getSexo());
                }
                if (tienePeso) {
                    ps.setDouble(index++, p.getPeso());
                }
                if (tieneTalla) {
                    ps.setDouble(index++, p.getTalla());
                }
                if (tieneImc) {
                    ps.setDouble(index++, p.getImc());
                }

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error insertando persona", e);
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM persona WHERE id = ?";

        try (Connection con = MySQLConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando persona", e);
        }
    }

    public Persona obtenerPorDni(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT * FROM persona WHERE dni = ?";

        try (Connection con = MySQLConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dni.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Categoria c = null;
                if (hasColumn(rs, "categoria_id")) {
                    c = categoriaDAO.obtenerPorId(rs.getInt("categoria_id"));
                }
                String sexo = hasColumn(rs, "sexo") ? rs.getString("sexo") : null;
                String grado = hasColumn(rs, "grado") ? rs.getString("grado") : null;
                double peso = hasColumn(rs, "peso") ? rs.getDouble("peso") : 0;
                double talla = hasColumn(rs, "talla") ? rs.getDouble("talla") : 0;
                double imc = hasColumn(rs, "imc") ? rs.getDouble("imc") : 0;
                LocalDate fechaNacimiento = rs.getDate("fecha_nacimiento") != null ? rs.getDate("fecha_nacimiento").toLocalDate() : null;

                Persona p = new Persona(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        grado,
                        rs.getString("dni"),
                        sexo,
                        fechaNacimiento,
                        c,
                        peso,
                        talla,
                        imc
                );

                if (c == null) {
                    p.setCategoria(new Categoria(0, p.getCategoriaNombre()));
                }

                if (hasColumn(rs, "categoria_id")) {
                    p.setCategoriaId(rs.getInt("categoria_id"));
                }

                return p;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo persona por DNI", e);
        }
        return null;
    }

    public List<Persona> obtenerTodas() {
        List<Persona> lista = new ArrayList<>();
        String sql = "SELECT * FROM persona";

        try (Connection con = MySQLConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Categoria c = null;
                if (hasColumn(rs, "categoria_id")) {
                    c = categoriaDAO.obtenerPorId(rs.getInt("categoria_id"));
                }
                String sexo = hasColumn(rs, "sexo") ? rs.getString("sexo") : null;
                String grado = hasColumn(rs, "grado") ? rs.getString("grado") : null;
                double peso = hasColumn(rs, "peso") ? rs.getDouble("peso") : 0;
                double talla = hasColumn(rs, "talla") ? rs.getDouble("talla") : 0;
                double imc = hasColumn(rs, "imc") ? rs.getDouble("imc") : 0;
                LocalDate fechaNacimiento = rs.getDate("fecha_nacimiento") != null ? rs.getDate("fecha_nacimiento").toLocalDate() : null;

                Persona p = new Persona(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        grado,
                        rs.getString("dni"),
                        sexo,
                        fechaNacimiento,
                        c,
                        peso,
                        talla,
                        imc
                );

                if (c == null) {
                    p.setCategoria(new Categoria(0, p.getCategoriaNombre()));
                }

                if (hasColumn(rs, "categoria_id")) {
                    p.setCategoriaId(rs.getInt("categoria_id"));
                }

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
                Categoria c = null;
                if (hasColumn(rs, "categoria_id")) {
                    c = categoriaDAO.obtenerPorId(rs.getInt("categoria_id"));
                }
                String sexo = hasColumn(rs, "sexo") ? rs.getString("sexo") : null;
                String grado = hasColumn(rs, "grado") ? rs.getString("grado") : null;
                double peso = hasColumn(rs, "peso") ? rs.getDouble("peso") : 0;
                double talla = hasColumn(rs, "talla") ? rs.getDouble("talla") : 0;
                double imc = hasColumn(rs, "imc") ? rs.getDouble("imc") : 0;
                LocalDate fechaNacimiento = rs.getDate("fecha_nacimiento") != null ? rs.getDate("fecha_nacimiento").toLocalDate() : null;

                Persona p = new Persona(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        grado,
                        rs.getString("dni"),
                        sexo,
                        fechaNacimiento,
                        c,
                        peso,
                        talla,
                        imc
                );

                if (c == null) {
                    p.setCategoria(new Categoria(0, p.getCategoriaNombre()));
                }

                if (hasColumn(rs, "categoria_id")) {
                    p.setCategoriaId(rs.getInt("categoria_id"));
                }

                return p;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo persona por ID", e);
        }

        return null;
    }

    private boolean hasColumn(ResultSet rs, String columnName) {
        try {
            ResultSetMetaData meta = rs.getMetaData();
            int count = meta.getColumnCount();
            for (int i = 1; i <= count; i++) {
                if (meta.getColumnLabel(i).equalsIgnoreCase(columnName)) {
                    return true;
                }
            }
        } catch (SQLException ignored) {
        }
        return false;
    }

    private boolean hasTableColumn(Connection con, String tableName, String columnName) {
        try {
            DatabaseMetaData meta = con.getMetaData();
            try (ResultSet rs = meta.getColumns(null, null, tableName, columnName)) {
                return rs.next();
            }
        } catch (SQLException ignored) {
        }
        return false;
    }
}
