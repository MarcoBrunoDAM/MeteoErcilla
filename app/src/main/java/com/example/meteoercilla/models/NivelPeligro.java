package com.example.meteoercilla.models;

public class NivelPeligro {
    private String nombre;
    private boolean seleccionada;

    public NivelPeligro(String nombre, boolean seleccionada) {
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
