package org.example.DAO;

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
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.io.Serializable;
import java.util.Scanner;

public class TorneoDAO implements Serializable {

    public Conexion conexion = new Conexion();
    private int id;
    private String nombre;
    private char codRegion;
    private int puntos = (int) (Math.random() * 51) + 50;
    private int idAdmin;

    // Constructor
    public TorneoDAO(int id, String nombre, char codRegion, int idAdmin) {
        this.id = id;
        this.nombre = nombre;
        this.codRegion = codRegion;
        this.idAdmin = idAdmin;
    }

    public TorneoDAO() {

    }

    public TorneoDAO(String nombreTorneo, char region, int idAdmin) {
        this.nombre = nombre;
        this.codRegion = codRegion;
        this.idAdmin = idAdmin;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Método para verificar si existe un torneo por su ID
    public boolean existeTorneo(int id) {
        try {
            // Construye la consulta SQL para contar el número de torneos con el ID especificado.
            String query = "SELECT COUNT(*) FROM appdatabase.torneo WHERE id = ?";

            // Se prepara la sentencia SQL con la conexión obtenida de `conexion.getConnection()`.
            PreparedStatement stmt = conexion.getConnection().prepareStatement(query);

            // Se establece el parámetro del ID del torneo.
            stmt.setInt(1, id);

            // Se ejecuta la consulta y se obtiene el ResultSet.
            ResultSet rs = stmt.executeQuery();

            // Se mueve al primer registro del ResultSet y se obtiene el conteo de torneos con el ID especificado.
            rs.next();

            // Si el resultado es mayor a cero, significa que existe un torneo con el ID especificado.
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            // Maneja cualquier excepción SQL lanzada y la encapsula en una RuntimeException con el mensaje adecuado.
            throw new RuntimeException("Error al verificar la existencia del torneo: " + e.getMessage(), e);
        }
    }



    // Método para cargar un torneo desde la base de datos por su ID
    public void cargarTorneoDB(int id) {
        // Consulta SQL para obtener todos los detalles del torneo con el ID especificado.
        String query = "SELECT * FROM appdatabase.torneo WHERE id = ?";
        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(query)) {
            // Se establece el parámetro del ID del torneo.
            stmt.setInt(1, id);

            // Se ejecuta la consulta y se obtiene el ResultSet.
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Si el ResultSet contiene un registro, se extraen los datos del torneo.
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
            // Captura y maneja excepciones SQL, mostrando el mensaje de error.
            e.printStackTrace();
            System.out.println("Error al cargar el torneo desde la base de datos: " + e.getMessage());
        }
    }

    // Método para exportar los datos de un torneo a un archivo XML
    public void exportarTorneoMain() {
        try {
            // Crea un nuevo documento XML.
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            // Crea el elemento raíz "torneo" en el documento.
            Element root = document.createElement("torneo");
            document.appendChild(root);

            // Agrega información del torneo como elementos hijos del nodo raíz.
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

            // Consulta SQL para obtener los combates relacionados con el torneo.
            String query = "SELECT * FROM combate WHERE idTorneo = ?";
            try (PreparedStatement stmt = conexion.getConnection().prepareStatement(query)) {
                stmt.setInt(1, this.id);
                try (ResultSet resultado = stmt.executeQuery()) {
                    // Crea el nodo "combates" en el documento XML.
                    Element combates = document.createElement("combates");
                    root.appendChild(combates);

                    // Procesa cada combate del ResultSet.
                    while (resultado.next()) {
                        Element combate = document.createElement("combate");

                        // Agrega información de cada combate como elementos hijos del nodo "combate".
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

                        // Agrega el nodo "combate" al nodo "combates".
                        combates.appendChild(combate);
                    }
                }
            }

            // Exporta el documento XML a un archivo en el sistema de archivos.
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File("src/main/Files/torneo_" + this.id + ".xml"));

            transformer.transform(source, result);

            System.out.println("Archivo XML exportado correctamente como torneo_" + this.id + ".xml");
        } catch (Exception e) {
            // Captura cualquier excepción y muestra un mensaje de error.
            e.printStackTrace();
            System.out.println("Error al exportar el torneo a XML.");
        }
    }


}
