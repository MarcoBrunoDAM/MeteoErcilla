package com.example.meteoercilla.models;

public class NivelPeligro {
    private String nombre;
    private boolean seleccionado;

    public NivelPeligro(String nombre, boolean seleccionada) {
        this.nombre = nombre;
        this.seleccionado = seleccionada;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }
}
