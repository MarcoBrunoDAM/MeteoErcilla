package com.example.meteoercilla.models;

public class Provincia {
    private String nombre;
    private boolean seleccionada;

    public Provincia(String nombre, boolean seleccionada) {
        this.nombre = nombre;
        this.seleccionada = seleccionada;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isSeleccionada() {
        return seleccionada;
    }

    public void setSeleccionada(boolean seleccionada) {
        this.seleccionada = seleccionada;
    }
}
