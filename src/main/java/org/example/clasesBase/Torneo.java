package org.example.clasesBase;

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
import java.util.ArrayList;

public class Torneo {
    private int id;
    private String nombre;
    private char codRegion;
    private float PuntosVictoria;
    private int puntos = (int) (Math.random() * 51) + 50;  // Puntos que ganas
    private String nombreAdmin;
    private String passAdmin;
    private ArrayList<Entrenador> entrenadores = new ArrayList<>();
    private ArrayList<Combate> combates = new ArrayList<>();
    private int idAdmin;

    // Constructores
    public Torneo(int id, String nombre, char codRegion, String nombreAdmin, String passAdmin) {
        this.id = id;
        this.nombre = nombre;
        this.codRegion = codRegion;
        this.nombreAdmin = nombreAdmin;
        this.passAdmin = passAdmin;
    }

    public Torneo(String nombre, char codRegion, int idAdmin) {
        this.nombre = nombre;
        this.codRegion = codRegion;
        this.idAdmin=idAdmin;
    }

    public Torneo(int id, String nombre, char codRegion, ArrayList<Entrenador> entrenadores, ArrayList<Combate> combates) {
        this.id = id;
        this.nombre = nombre;
        this.codRegion = codRegion;
        this.entrenadores = entrenadores;
        this.combates = combates;
    }

    // Getters y Setters
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

    public char getCodRegion() {
        return codRegion;
    }

    public void setCodRegion(char codRegion) {
        this.codRegion = codRegion;
    }

    public float getPuntosVictoria() {
        return PuntosVictoria;
    }

    public void setPuntosVictoria(float puntosVictoria) {
        PuntosVictoria = puntosVictoria;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public String getNombreAdmin() {
        return nombreAdmin;
    }

    public void setNombreAdmin(String nombreAdmin) {
        this.nombreAdmin = nombreAdmin;
    }

    public String getPassAdmin() {
        return passAdmin;
    }

    public void setPassAdmin(String passAdmin) {
        this.passAdmin = passAdmin;
    }

    public ArrayList<Entrenador> getEntrenadores() {
        return entrenadores;
    }

    public void setEntrenadores(ArrayList<Entrenador> entrenadores) {
        this.entrenadores = entrenadores;
    }

    public ArrayList<Combate> getCombates() {
        return combates;
    }

    public void setCombates(ArrayList<Combate> combates) {
        this.combates = combates;
    }


}
