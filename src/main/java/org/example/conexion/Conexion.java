package org.example.conexion;

import java.sql.*;

public class Conexion {

    private String url = "jdbc:mysql://localhost:3306/appdatabase";
    private String user = "root";
    private String password = "root";
    private String driver = "com.mysql.cj.jdbc.Driver";
    private Connection connection;
    public Statement statement;

    public Conexion() {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            // Realiza las operaciones SQL aquí, ya sea un Query inicial o cualquier otra operación.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Statement getStatement() {
        return statement;
    }


    public Connection getConnection() {
        return connection;
    }

    // Método para cerrar la conexión
    public void cerrarConexion() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada");
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexión");
            e.printStackTrace();
        }
    }
}

