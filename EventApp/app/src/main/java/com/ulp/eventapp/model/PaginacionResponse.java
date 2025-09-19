package com.ulp.eventapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PaginacionResponse<T> {
    @SerializedName("totalRegistros")
    private int totalRegistros;

    @SerializedName("datos")
    private List<T> datos;

    public int getTotalRegistros() { return totalRegistros; }
    public void setTotalRegistros(int totalRegistros) { this.totalRegistros = totalRegistros; }

    public List<T> getDatos() { return datos; }
    public void setDatos(List<T> datos) { this.datos = datos; }
}