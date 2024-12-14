package org.example.DAO;

import org.example.conexion.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EntrenadorDAO {
    public Conexion conexion = new Conexion();
    private int id; // ID autoincremental
    private String nombre;
    private String nacionalidad;

    // Constructor
    public EntrenadorDAO(String nombre, String nacionalidad) {
        this.nombre = nombre;
        this.nacionalidad = nacionalidad;

        // Llamar al método para insertar en la base de datos
        insertarEnDB();
    }

    // Método para insertar el entrenador en la base de datos
    public void insertarEnDB() {
        String sql = "INSERT INTO entrenador (nombre, nacionalidad) VALUES (?, ?)";

        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            // Configurar los parámetros de la consulta
            stmt.setString(1, nombre);
            stmt.setString(2, nacionalidad);

            // Ejecutar la inserción
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Error al insertar el entrenador, no se afectaron filas.");
            }

            // Obtener el ID generado automáticamente
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    setId(generatedKeys.getInt(1)); // Usar el setter para guardar el ID
                    System.out.println("Entrenador insertado con éxito. ID generado: " + id);
                } else {
                    throw new SQLException("Error al obtener el ID generado.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar el entrenador en la base de datos: " + e.getMessage(), e);
        }
    }

    // Getter y setter para id
    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    // Otros getters
    public String getNombre() {
        return nombre;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    // Método para asociar un entrenador a un torneo en la base de datos
    public void asociarEntrenadorTorneo(int idEntrenador, int idTorneo) {
        String torneoQuery = "INSERT INTO appdatabase.torneos_entrenadores (idEntrenador, idTorneo) VALUES (?, ?)";

        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(torneoQuery)) {

            stmt.setInt(1, idEntrenador);
            stmt.setInt(2, idTorneo);
            stmt.executeUpdate();
            System.out.println("Entrenador asociado al torneo.");

        } catch (SQLException e) {
            throw new RuntimeException("Error al asociar el entrenador al torneo: " + e.getMessage(), e);
        }
    }
}
