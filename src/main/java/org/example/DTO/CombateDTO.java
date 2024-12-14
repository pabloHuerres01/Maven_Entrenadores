package org.example.DTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class CombateDTO implements Serializable {
    private Date fecha;
    private long id;
    private int idEntreandor1, idEntenador2, idTorneo;

    public CombateDTO(Date fecha, int idEntreandor1, long id, int idEntenador2, int idTorneo) {
        this.fecha = fecha;
        this.idEntreandor1 = idEntreandor1;
        this.id = id;
        this.idEntenador2 = idEntenador2;
        this.idTorneo = idTorneo;
    }
}
