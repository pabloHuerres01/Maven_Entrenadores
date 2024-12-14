package org.example.DTO;

import java.io.Serializable;
import java.time.LocalDate;

public class CarnetDTO implements Serializable {

    private long idEntrenador;
    private LocalDate fechaExpedicion;
    private float puntos;
    private int numVictorias;

    //Constructor

    public CarnetDTO() {
        super();
    }

    public CarnetDTO(long idEntrenador, LocalDate fechaExpedicion, float puntos, int numVictorias) {
        this.idEntrenador = idEntrenador;
        this.fechaExpedicion = fechaExpedicion;
        this.puntos = puntos;
        this.numVictorias = numVictorias;
    }
}
