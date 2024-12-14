package org.example.DAO;

import org.example.conexion.Conexion;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CombateDAO {

    public Conexion conexion = new Conexion();

    private Date fecha;
    private long id; // ID autoincremental generado por la base de datos
    private int idEntreandor1, idEntenador2, idTorneo;

    // Constructor
    public CombateDAO(Date fecha, int idEntreandor1, int idEntenador2, int idTorneo) {
        this.fecha = fecha;
        this.idEntreandor1 = idEntreandor1;
        this.idEntenador2 = idEntenador2;
        this.idTorneo = idTorneo;

        // Llamar al método para insertar en la base de datos
        insertarEnDB();
    }

    // Método para insertar en la base de datos
    private void insertarEnDB() {
        String sql = "INSERT INTO combate (fecha, idEntreandor1, idEntenador2, idTorneo) VALUES (?, ?, ?, ?)";

        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            // Configurar los parámetros de la consulta
            stmt.setDate(1, fecha);
            stmt.setInt(2, idEntreandor1);
            stmt.setInt(3, idEntenador2);
            stmt.setInt(4, idTorneo);

            // Ejecutar la inserción
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Error al insertar el combate, no se afectaron filas.");
            }

            // Obtener el ID generado automáticamente
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    setId(generatedKeys.getLong(1)); // Usar el setter para guardar el ID
                    System.out.println("Combate insertado con éxito. ID generado: " + id);
                } else {
                    throw new SQLException("Error al obtener el ID generado.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar el combate en la base de datos: " + e.getMessage(), e);
        }
    }

    // Getter y setter para id
    public long getId() {
        return id;
    }

    private void setId(long id) {
        this.id = id;
    }

    // Otros getters
    public Date getFecha() {
        return fecha;
    }

    public int getIdEntreandor1() {
        return idEntreandor1;
    }

    public int getIdEntenador2() {
        return idEntenador2;
    }

    public int getIdTorneo() {
        return idTorneo;
    }
}
