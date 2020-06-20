package com.example.jardigii.model;

public class Planta {
    private String nomePlanta;
    private String luminosidadePlanta;
    private String umidadeSoloPlanta;
    private String temperaturaPlanta;

    public Planta(String nomePlanta, String luminosidadePlanta, String umidadeSoloPlanta, String temperaturaPlanta) {
        this.nomePlanta = nomePlanta;
        this.luminosidadePlanta = luminosidadePlanta;
        this.umidadeSoloPlanta = umidadeSoloPlanta;
        this.temperaturaPlanta = temperaturaPlanta;
    }

    public Planta() {
    }

    public String getNomePlanta() {
        return nomePlanta;
    }

    public void setNomePlanta(String nomePlanta) {
        this.nomePlanta = nomePlanta;
    }

    public String getLuminosidadePlanta() {
        return luminosidadePlanta;
    }

    public void setLuminosidadePlanta(String luminosidadePlanta) {
        this.luminosidadePlanta = luminosidadePlanta;
    }

    public String getUmidadeSoloPlanta() {
        return umidadeSoloPlanta;
    }

    public void setUmidadeSoloPlanta(String umidadeSoloPlanta) {
        this.umidadeSoloPlanta = umidadeSoloPlanta;
    }

    public String getTemperaturaPlanta() {
        return temperaturaPlanta;
    }

    public void setTemperaturaPlanta(String temperaturaPlanta) {
        this.temperaturaPlanta = temperaturaPlanta;
    }
}
