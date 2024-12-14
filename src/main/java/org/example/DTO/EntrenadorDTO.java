package org.example.DTO;

import org.example.clasesBase.Carnet;
import org.example.clasesBase.Torneo;
import org.example.controlFicheros.LecturaFicheros;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class EntrenadorDTO implements Serializable {
    private long id;
    private String nombre, nacionalidad;
    private LecturaFicheros lecturaFicheros = new LecturaFicheros();
    private LocalDate fechaHoy = LocalDate.now();
    private Carnet carnet; //
    private DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    //Constructores

    public EntrenadorDTO() {
    }

    /**
     *
     * @param id
     * @param nombre
     * @param nacionalidad
     * @param lecturaFicheros
     * @param torneosEntrenadores
     * @param fechaHoy
     * @param carnet
     * @param formato
     */
    public EntrenadorDTO(long id, String nombre, String nacionalidad, LecturaFicheros lecturaFicheros, ArrayList<Torneo> torneosEntrenadores, LocalDate fechaHoy, Carnet carnet, DateTimeFormatter formato) {
        this.id = id;
        this.nombre = nombre;
        this.nacionalidad = nacionalidad;
        this.lecturaFicheros = lecturaFicheros;
        this.fechaHoy = fechaHoy;
        this.carnet = carnet;
        this.formato = formato;
    }
}
