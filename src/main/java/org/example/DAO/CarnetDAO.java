package org.example.DAO;

import org.example.conexion.Conexion;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class CarnetDAO {
    public Conexion conexion = new Conexion();

    private int id; // ID autoincremental generado por la base de datos
    private int idEntrenador;
    private LocalDate fechaExpedicion;
    private float puntos;
    private int numVictorias;

    // Constructor
    public CarnetDAO(int numVictorias, float puntos, LocalDate fechaExpedicion, int idEntrenador) {
        this.numVictorias = numVictorias;
        this.puntos = puntos;
        this.fechaExpedicion = fechaExpedicion;
        this.idEntrenador = idEntrenador;

        // Llamar al método para insertar en la base de datos
        insertarEnDB();
    }

    // Método para insertar el carnet en la base de datos
    private void insertarEnDB() {
        String sql = "INSERT INTO carnet (numVictorias, puntos, fechaExpedicion, idEntrenador) VALUES (?, ?, ?, ?)";

        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            // Configurar los parámetros de la consulta
            stmt.setInt(1, numVictorias);
            stmt.setFloat(2, puntos);
            stmt.setDate(3, Date.valueOf(fechaExpedicion));
            stmt.setInt(4, idEntrenador);

            // Ejecutar la inserción
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Error al insertar el carnet, no se afectaron filas.");
            }

            // Obtener el ID generado automáticamente
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    setId(generatedKeys.getInt(1)); // Usar el setter para guardar el ID
                    System.out.println("Carnet insertado con éxito. ID generado: " + id);
                } else {
                    throw new SQLException("Error al obtener el ID generado.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar el carnet en la base de datos: " + e.getMessage(), e);
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
    public int getIdEntrenador() {
        return idEntrenador;
    }

    public LocalDate getFechaExpedicion() {
        return fechaExpedicion;
    }

    public float getPuntos() {
        return puntos;
    }

    public int getNumVictorias() {
        return numVictorias;
    }
}
