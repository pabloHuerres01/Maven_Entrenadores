package org.example.DAO;

import org.example.conexion.Conexion;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AdminDAO {
    public int id;
    private String nombre, pass;
    private Conexion conexion = new Conexion();

    // Constructor
    public AdminDAO(String nombre, String pass) {
        this.nombre = nombre;
        this.pass = pass;


    }

    public void AdminDAONuevoDB(String nombre, String pass) {
        this.nombre = nombre;
        this.pass = pass;

        // Llamar al método para insertar en la base de datos
        insertarEnDB();
    }

    // Constructor vacío
    public AdminDAO() {}

    public AdminDAO(int id) {
        this.id = id;
    }

    // Método para mostrar los datos de un torneo
    public void mostrarDatosTorneo(int idTorneo) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        // SQL para verificar si el administrador actual es dueño del torneo
        String verificarAdminSql = "SELECT idAdmin FROM appdatabase.torneo WHERE id = ?";

        try {
            conn = conexion.getConnection();
            stmt = conn.prepareStatement(verificarAdminSql);
            stmt.setInt(1, idTorneo);

            rs = stmt.executeQuery();

            if (rs.next() && rs.getInt("idAdmin") == this.id) {
                // SQL para obtener datos del torneo
                String sql = """
                SELECT t.nombre AS torneo, e1.nombre AS entrenador1, e2.nombre AS entrenador2
                FROM appdatabase.torneo t
                LEFT JOIN appdatabase.combate c ON t.id = c.idTorneo
                LEFT JOIN appdatabase.entrenador e1 ON c.idEntrenador1 = e1.id
                LEFT JOIN appdatabase.entrenador e2 ON c.idEntrenador2 = e2.id
                WHERE t.id = ?;
                """;

                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, idTorneo);

                try (ResultSet rs2 = stmt.executeQuery()) {
                    if (!rs2.next()) {
                        System.out.println("No se encontraron datos para el torneo con ID: " + idTorneo);
                        return;
                    }

                    // Mostrar datos en el formato especificado
                    StringBuilder datos = new StringBuilder();
                    do {
                        String nombreTorneo = rs2.getString("torneo");
                        String entrenador1 = rs2.getString("entrenador1");
                        String entrenador2 = rs2.getString("entrenador2");

                        datos.append(nombreTorneo).append(" - ")
                                .append(entrenador1 == null ? "N/A" : entrenador1).append(" - ")
                                .append(entrenador2 == null ? "N/A" : entrenador2).append("\n");
                    } while (rs2.next());

                    System.out.println("Datos del torneo:\n" + datos);

                    // Preguntar si desea exportar a un archivo de texto
                    Scanner scanner = new Scanner(System.in);
                    System.out.println("¿Desea exportar los datos a un archivo txt? (1 para Sí, cualquier otra tecla para No): ");
                    String respuesta = scanner.nextLine();

                    if ("1".equals(respuesta)) {
                        exportarDatosTorneoTxt(datos.toString(), "torneo_" + idTorneo + ".txt");
                    }
                }
            } else {
                System.out.println("No tienes permisos para acceder a este torneo.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar el administrador del torneo: " + e.getMessage(), e);
        } finally {
            // Cerrar la conexión y los recursos abiertos
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    // Método para exportar los datos a un archivo de texto
    private void exportarDatosTorneoTxt(String datos, String nombreArchivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/Files/" + nombreArchivo))) {
            writer.write(datos);
            System.out.println("Datos exportados correctamente al archivo: " + nombreArchivo);
        } catch (IOException e) {
            System.out.println("Error al exportar los datos a un archivo txt: " + e.getMessage());
        }
    }

    // Método para insertar el admin en la base de datos
    private void insertarEnDB() {
        String sql = "INSERT INTO appdatabase.admintorneo (nombre, contrasena) VALUES (?, ?)";

        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, nombre);
            stmt.setString(2, pass);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Error al insertar el admin, no se afectaron filas.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    setId(generatedKeys.getInt(1));
                    System.out.println("Admin insertado con éxito. ID generado: " + id);
                } else {
                    throw new SQLException("Error al obtener el ID generado.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar el admin en la base de datos: " + e.getMessage(), e);
        }
    }

    // Método para verificar si el admin existe en la base de datos por su ID
    public boolean existeEnDB() {
        String sql = "SELECT id FROM appdatabase.admintorneo WHERE id = " + id + ";";

        System.out.println(sql);

        try (ResultSet rs = conexion.getStatement().executeQuery(sql)) {
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Método para verificar si el admin existe en la base de datos con sus credenciales
    public boolean existeEnDBConCredenciales() {
        String sql = "SELECT id FROM appdatabase.admintorneo WHERE nombre ='" + nombre + "' AND contrasena = '" + pass + "';";

        System.out.println(sql);

        try (ResultSet rs = conexion.getStatement().executeQuery(sql)) {
            if (rs.next()) {
                setId(rs.getInt("id"));
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
