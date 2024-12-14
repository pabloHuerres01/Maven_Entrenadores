package org.example.DTO;

import org.example.conexion.Conexion;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PaisDTO {
    private String id, nombre;
    public Conexion conexion = new Conexion();

    public PaisDTO() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Método para mostrar todos los países disponibles
    public void mostrarPais() {
        try {
            ResultSet paisesQuery = conexion.getStatement().executeQuery("SELECT * FROM appdatabase.pais");
            while (paisesQuery.next()) {
                System.out.println("Nombre: " + paisesQuery.getString("nombre") +
                        " El id es " + paisesQuery.getString("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Método para verificar si un país existe por código
    public boolean existePaisPorCodigo(String codigo) {
        try {
            ResultSet rs = conexion.getStatement().executeQuery(
                    "SELECT nombre FROM appdatabase.pais WHERE id = '" + codigo + "'");
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar el país por código: " + e.getMessage(), e);
        }
    }

    // Método para verificar si un país existe por nombre
    public boolean existePaisPorNombre(String nombre) {
        try {
            ResultSet rs = conexion.getStatement().executeQuery(
                    "SELECT nombre FROM appdatabase.pais WHERE nombre = '" + nombre + "'");
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar el país por nombre: " + e.getMessage(), e);
        }
    }
}
