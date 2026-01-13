/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vo;

/**
 *
 * @author Elena
 */
public class Lista {
     private int idLista;
    private String nombre;
    private String descripcion;
    private String imagenUrl;
    private java.time.LocalDateTime fechaCreacion;

    // Constructor
    public Lista(int idLista, String nombre, String descripcion, String imagenUrl, java.time.LocalDateTime fechaCreacion) {
        this.idLista = idLista;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters
    public int getIdLista() {
        return idLista;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public java.time.LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    // Setters
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public void setFechaCreacion(java.time.LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
