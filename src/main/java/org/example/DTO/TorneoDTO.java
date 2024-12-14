package org.example.DTO;

import org.example.conexion.Conexion;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.Serializable;

public class TorneoDTO implements Serializable {

    public Conexion conexion = new Conexion();
    private int id;
    private String nombre;
    private char codRegion;
    private int puntos = (int) (Math.random() * 51) + 50;
    private int idAdmin;

    // Constructor
    public TorneoDTO(int id, String nombre, char codRegion, int idAdmin) {
        this.id = id;
        this.nombre = nombre;
        this.codRegion = codRegion;
        this.idAdmin = idAdmin;
    }

    public TorneoDTO() {

    }

    public void mostrarTodosTorneos() {
        String sql = "SELECT * FROM appdatabase.torneo";

        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Comprobar si hay resultados
            if (!rs.isBeforeFirst()) {
                System.out.println("No se encontraron torneos en la base de datos.");
                return;
            }

            // Mostrar los resultados
            System.out.println("ID\tNombre\tCod. Región\tPuntos Victoria\tID Admin");
            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String codRegionStr = rs.getString("codRegion");
                char codRegion = codRegionStr != null && !codRegionStr.isEmpty() ? codRegionStr.charAt(0) : ' ';
                int puntosVictoria = rs.getInt("puntosVictoria");
                int idAdmin = rs.getInt("idAdmin");

                System.out.printf("%d\t%s\t%c\t%d\t%d%n", id, nombre, codRegion, puntosVictoria, idAdmin);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener los torneos de la base de datos: " + e.getMessage(), e);
        }
    }
    public void cargarTorneoDB(int id) {
        String query = "SELECT * FROM appdatabase.torneo WHERE id = ?";
        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    this.id = rs.getInt("id");
                    this.nombre = rs.getString("nombre");
                    this.codRegion = rs.getString("codRegion").charAt(0);
                    this.idAdmin = rs.getInt("idAdmin");
                    System.out.println("Torneo cargado con éxito desde la base de datos.");
                } else {
                    System.out.println("No se encontró un torneo con el ID especificado.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al cargar el torneo desde la base de datos: " + e.getMessage());
        }
    }

    // Método para verificar si existe un torneo por su ID
    public boolean existeTorneo(int id) {
        try {
            String query = "SELECT COUNT(*) FROM appdatabase.torneo WHERE id = ?";
            PreparedStatement stmt = conexion.getConnection().prepareStatement(query);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar la existencia del torneo: " + e.getMessage(), e);
        }
    }

    // Método para exportar el torneo y sus combates a un archivo XML
    public void exportarTorneo(Connection conexion) {
        try {
            // Crear el documento XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            // Crear elemento raíz
            Element root = document.createElement("torneo");
            document.appendChild(root);

            // Agregar información del torneo
            Element idTorneo = document.createElement("id");
            idTorneo.appendChild(document.createTextNode(String.valueOf(this.id)));
            root.appendChild(idTorneo);

            Element nombreTorneo = document.createElement("nombre");
            nombreTorneo.appendChild(document.createTextNode(this.nombre));
            root.appendChild(nombreTorneo);

            Element codRegion = document.createElement("codRegion");
            codRegion.appendChild(document.createTextNode(String.valueOf(this.codRegion)));
            root.appendChild(codRegion);

            Element puntosVictoria = document.createElement("puntosVictoria");
            puntosVictoria.appendChild(document.createTextNode(String.valueOf(this.puntos)));
            root.appendChild(puntosVictoria);

            // Obtener combates relacionados desde la base de datos
            String query = "SELECT * FROM combate WHERE idTorneo = ?";
            PreparedStatement stmt = conexion.prepareStatement(query);
            stmt.setInt(1, this.id);
            ResultSet resultado = stmt.executeQuery();

            // Crear el nodo de combates
            Element combates = document.createElement("combates");
            root.appendChild(combates);

            while (resultado.next()) {
                Element combate = document.createElement("combate");

                Element idCombate = document.createElement("id");
                idCombate.appendChild(document.createTextNode(String.valueOf(resultado.getInt("id"))));
                combate.appendChild(idCombate);

                Element fecha = document.createElement("fecha");
                fecha.appendChild(document.createTextNode(resultado.getDate("fecha").toString()));
                combate.appendChild(fecha);

                Element entrenador1 = document.createElement("idEntrenador1");
                entrenador1.appendChild(document.createTextNode(String.valueOf(resultado.getInt("idEntrenador1"))));
                combate.appendChild(entrenador1);

                Element entrenador2 = document.createElement("idEntrenador2");
                entrenador2.appendChild(document.createTextNode(String.valueOf(resultado.getInt("idEntrenador2"))));
                combate.appendChild(entrenador2);

                combates.appendChild(combate);
            }

            // Guardar el archivo XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File("torneo_" + this.id + ".xml"));

            transformer.transform(source, result);

            System.out.println("Archivo XML exportado correctamente como torneo_" + this.id + ".xml");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al exportar el torneo a XML.");
        }
    }
}
