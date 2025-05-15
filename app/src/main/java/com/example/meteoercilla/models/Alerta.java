package com.example.meteoercilla.models;

import java.io.Serializable;
import java.time.LocalDate;


//Implementamos serializable para poder meter alertas en un arraylist y poder pasar
//esa lista de un activity a otra con un intent
public class Alerta implements Serializable {
    String ubicacion , provincia,nivelPeligro,tipoAlerta,fechaAlerta;
    int idAlerta;
    String icono;

    public Alerta(int idAlerta,String ubicacion, String provincia, String nivelPeligro, String fechaAlerta,String tipoAlerta,String icono) {
        this.ubicacion = ubicacion;
        this.provincia = provincia;
        this.nivelPeligro = nivelPeligro;
        this.fechaAlerta = fechaAlerta;
        this.tipoAlerta = tipoAlerta;
        this.icono = icono;
        this.idAlerta = idAlerta;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getNivelPeligro() {
        return nivelPeligro;
    }

    public void setNivelPeligro(String nivelPeligro) {
        this.nivelPeligro = nivelPeligro;
    }

    public String getTipoAlerta() {
        return tipoAlerta;
    }

    public void setTipoAlerta(String tipoAlerta) {
        this.tipoAlerta = tipoAlerta;
    }

    public String getFechaAlerta() {
        return fechaAlerta;
    }

    public void setFechaAlerta(String fechaAlerta) {
        this.fechaAlerta = fechaAlerta;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public int getIdAlerta() {
        return idAlerta;
    }

    public void setIdAlerta(int idAlerta) {
        this.idAlerta = idAlerta;
    }
}
