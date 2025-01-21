package com.example.salud_total_v2;

public class Cita {
    private int id;
    private int idUsuario;
    private int idDoctor;
    private String fechaCita;
    private String horaCita;
    private int idEspecialidad;
    private String ubicacion;
    private String nota;

    public Cita(int id, int idUsuario, int idDoctor, String fechaCita, String horaCita, int idEspecialidad, String ubicacion, String nota) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idDoctor = idDoctor;
        this.fechaCita = fechaCita;
        this.horaCita = horaCita;
        this.idEspecialidad = idEspecialidad;
        this.ubicacion = ubicacion;
        this.nota = nota;
    }

    public int getId() {
        return id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public int getIdDoctor() {
        return idDoctor;
    }

    public String getFechaCita() {
        return fechaCita;
    }

    public String getHoraCita() {
        return horaCita;
    }

    public int getIdEspecialidad() {
        return idEspecialidad;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getNota() {
        return nota;
    }
}
