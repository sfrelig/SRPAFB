package com.srpafb.dao;

import com.srpafb.connection.MySQLConnection;
import com.srpafb.exception.AppException;
import com.srpafb.exception.ExceptionHandler;
import com.srpafb.model.Categoria;
import com.srpafb.model.Persona;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la entidad Persona.
 * Proporciona operaciones CRUD con logging comprehensivo y manejo centralizado de excepciones.
 */
public class PersonaDAO {

    private static final Logger logger = LoggerFactory.getLogger(PersonaDAO.class);
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    /**
     * Inserta una nueva persona en la base de datos.
     * @param p Persona a insertar
     * @throws AppException si falla la validación o la inserción
     */
    public void insertar(Persona p) {
        try {
            // Validación
            if (p.getDni() == null || p.getDni().trim().isEmpty()) {
                throw new AppException(
                    AppException.ErrorType.VALIDATION_ERROR,
                    "El DNI es obligatorio.",
                    "Intento de insertar persona sin DNI"
                );
            }

            if (obtenerPorDni(p.getDni().trim()) != null) {
                throw new AppException(
                    AppException.ErrorType.BUSINESS_LOGIC,
                    "Ya existe una persona con el DNI " + p.getDni() + ".",
                    "DNI duplicado en base de datos: " + p.getDni()
                );
            }

            try (Connection con = MySQLConnection.getConnection()) {
                boolean tieneGrado = hasTableColumn(con, "persona", "grado");
                boolean tieneSexo = hasTableColumn(con, "persona", "sexo");
                boolean tienePeso = hasTableColumn(con, "persona", "peso");
                boolean tieneTalla = hasTableColumn(con, "persona", "talla");
                boolean tieneImc = hasTableColumn(con, "persona", "imc");

                StringBuilder columnas = new StringBuilder("nombre, apellido, dni, fecha_nac, categoria_id");
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
                    logger.info("Persona insertada exitosamente - DNI: {}, Nombre: {} {}", 
                        p.getDni(), p.getNombre(), p.getApellido());
                }
            }
        } catch (AppException ex) {
            throw ex;
        } catch (SQLException e) {
            String logMsg = "Error SQL al insertar persona - DNI: " + p.getDni();
            logger.error(logMsg, e);
            throw new AppException(
                AppException.ErrorType.DATABASE_OPERATION,
                "No se pudo guardar la persona. Verifique los datos e intente nuevamente.",
                logMsg,
                e
            );
        } catch (Exception e) {
            String logMsg = "Error inesperado al insertar persona - DNI: " + p.getDni();
            logger.error(logMsg, e);
            throw new AppException(
                AppException.ErrorType.SYSTEM_ERROR,
                "Error inesperado al guardar la persona.",
                logMsg,
                e
            );
        }
    }

    /**
     * Elimina una persona por su ID.
     * @param id ID de la persona
     * @throws AppException si falla la eliminación
     */
    public void eliminar(int id) {
        String sql = "DELETE FROM persona WHERE id = ?";

        try (Connection con = MySQLConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected == 0) {
                logger.warn("Intento de eliminar persona inexistente con ID: {}", id);
                throw new AppException(
                    AppException.ErrorType.BUSINESS_LOGIC,
                    "La persona con ID " + id + " no existe.",
                    "No se encontró persona con ID: " + id
                );
            }
            
            logger.info("Persona eliminada exitosamente - ID: {}", id);
        } catch (AppException ex) {
            throw ex;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451 || e.getMessage().contains("foreign key")) {
                logger.warn("Intento de eliminar persona con registros relacionados - ID: {}", id);
                throw new AppException(
                    AppException.ErrorType.BUSINESS_LOGIC,
                    "No se puede eliminar la persona porque tiene resultados asociados.",
                    "Violación de restricción de clave foránea para ID: " + id,
                    e
                );
            }
            
            String logMsg = "Error SQL al eliminar persona con ID: " + id;
            logger.error(logMsg, e);
            throw new AppException(
                AppException.ErrorType.DATABASE_OPERATION,
                "No se pudo eliminar la persona. Intente nuevamente.",
                logMsg,
                e
            );
        } catch (Exception e) {
            String logMsg = "Error inesperado al eliminar persona con ID: " + id;
            logger.error(logMsg, e);
            throw new AppException(
                AppException.ErrorType.SYSTEM_ERROR,
                "Error al eliminar la persona.",
                logMsg,
                e
            );
        }
    }

    /**
     * Obtiene una persona por DNI.
     * @param dni DNI a buscar
     * @return Persona si existe, null si no
     */
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
                logger.debug("Persona encontrada por DNI: {}", dni);
                return construirPersonaDesdeResultSet(rs);
            }

            logger.debug("No se encontró persona con DNI: {}", dni);
            return null;
        } catch (SQLException e) {
            String logMsg = "Error SQL al obtener persona por DNI: " + dni;
            logger.error(logMsg, e);
            throw new AppException(
                AppException.ErrorType.DATABASE_OPERATION,
                "Error al buscar la persona. Intente nuevamente.",
                logMsg,
                e
            );
        } catch (Exception e) {
            String logMsg = "Error inesperado al obtener persona por DNI: " + dni;
            logger.error(logMsg, e);
            throw new AppException(
                AppException.ErrorType.SYSTEM_ERROR,
                "Error al buscar la persona.",
                logMsg,
                e
            );
        }
    }

    /**
     * Obtiene todas las personas de la base de datos.
     * @return Lista de personas
     * @throws AppException si falla la consulta
     */
    public List<Persona> obtenerTodas() {
        List<Persona> lista = new ArrayList<>();
        String sql = "SELECT * FROM persona";

        try (Connection con = MySQLConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(construirPersonaDesdeResultSet(rs));
            }

            logger.info("Se obtuvieron {} personas de la base de datos", lista.size());
            return lista;
        } catch (SQLException e) {
            String logMsg = "Error SQL al obtener todas las personas";
            logger.error(logMsg, e);
            throw new AppException(
                AppException.ErrorType.DATABASE_OPERATION,
                "No se pudieron cargar las personas. Intente nuevamente.",
                logMsg,
                e
            );
        } catch (Exception e) {
            String logMsg = "Error inesperado al obtener todas las personas";
            logger.error(logMsg, e);
            throw new AppException(
                AppException.ErrorType.SYSTEM_ERROR,
                "Error al cargar las personas.",
                logMsg,
                e
            );
        }
    }

    /**
     * Obtiene una persona por ID.
     * @param id ID a buscar
     * @return Persona si existe, null si no
     * @throws AppException si falla la consulta
     */
    public Persona obtenerPorId(int id) {
        String sql = "SELECT * FROM persona WHERE id = ?";

        try (Connection con = MySQLConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                logger.debug("Persona encontrada por ID: {}", id);
                return construirPersonaDesdeResultSet(rs);
            }

            logger.debug("No se encontró persona con ID: {}", id);
            return null;
        } catch (SQLException e) {
            String logMsg = "Error SQL al obtener persona por ID: " + id;
            logger.error(logMsg, e);
            throw new AppException(
                AppException.ErrorType.DATABASE_OPERATION,
                "Error al buscar la persona. Intente nuevamente.",
                logMsg,
                e
            );
        } catch (Exception e) {
            String logMsg = "Error inesperado al obtener persona por ID: " + id;
            logger.error(logMsg, e);
            throw new AppException(
                AppException.ErrorType.SYSTEM_ERROR,
                "Error al buscar la persona.",
                logMsg,
                e
            );
        }
    }

    /**
     * Construye un objeto Persona a partir de un ResultSet.
     * @param rs ResultSet con datos de persona
     * @return Objeto Persona construido
     * @throws SQLException si hay error al leer el ResultSet
     */
    private Persona construirPersonaDesdeResultSet(ResultSet rs) throws SQLException {
        Categoria c = null;
        if (hasColumn(rs, "categoria_id")) {
            try {
                c = categoriaDAO.obtenerPorId(rs.getInt("categoria_id"));
            } catch (Exception e) {
                logger.warn("No se pudo obtener categoría para ID: {}", rs.getInt("categoria_id"), e);
            }
        }

        String sexo = hasColumn(rs, "sexo") ? rs.getString("sexo") : null;
        String grado = hasColumn(rs, "grado") ? rs.getString("grado") : null;
        double peso = hasColumn(rs, "peso") ? rs.getDouble("peso") : 0;
        double talla = hasColumn(rs, "talla") ? rs.getDouble("talla") : 0;
        double imc = hasColumn(rs, "imc") ? rs.getDouble("imc") : 0;
        LocalDate fechaNacimiento = rs.getDate("fecha_nac") != null ? rs.getDate("fecha_nac").toLocalDate() : null;

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

    /**
     * Verifica si una columna existe en el ResultSet.
     * @param rs ResultSet a verificar
     * @param columnName Nombre de la columna
     * @return true si existe, false si no
     */
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
            logger.debug("Error al verificar columna: {}", columnName);
        }
        return false;
    }

    /**
     * Verifica si una columna existe en la tabla de la base de datos.
     * @param con Conexión a la base de datos
     * @param tableName Nombre de la tabla
     * @param columnName Nombre de la columna
     * @return true si existe, false si no
     */
    private boolean hasTableColumn(Connection con, String tableName, String columnName) {
        try {
            DatabaseMetaData meta = con.getMetaData();
            try (ResultSet rs = meta.getColumns(null, null, tableName, columnName)) {
                return rs.next();
            }
        } catch (SQLException ignored) {
            logger.debug("Error al verificar columna {} en tabla {}", columnName, tableName);
        }
        return false;
    }
}
