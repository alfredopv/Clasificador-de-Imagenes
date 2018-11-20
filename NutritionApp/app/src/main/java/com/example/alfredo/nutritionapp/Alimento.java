package com.example.alfredo.nutritionapp;


import org.json.JSONArray;
import org.json.JSONObject;

public class Alimento {
public String Producto, Cantidad, Marca, id;
public double PS, LP, HC,Kcal;

    public Alimento(String producto, String cantidad, String marca, String id, double PS, double LP, double HC,double kcal) {
        this.Producto = producto;
        Cantidad = cantidad;
        Marca = marca;
        this.id = id;
        this.PS = PS;
        this.LP = LP;
        this.HC = HC;
        this.Kcal= kcal;
    }

    public double getKcal() {
        return Kcal;
    }

    public String getCantidad() {
        return Cantidad;
    }

    public String getMarca() {
        return Marca;
    }

    public String getId() {
        return id;
    }

    public double getPS() {
        return PS;
    }

    public double getLP() {
        return LP;
    }

    public double getHC() {
        return HC;
    }


}




