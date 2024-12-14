package org.example.clasesBase;

public class AdminTorneo {
    public int id;
    public String nombre, password;

    public AdminTorneo(int id, String nombre, String password) {
        this.id = id;
        this.nombre = nombre;
        this.password = password;
    }

    public String getNombreAdmin() {
        return nombre;
    }
}
