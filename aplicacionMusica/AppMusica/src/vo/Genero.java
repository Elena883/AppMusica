/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vo;

/**
 *
 * @author Elena
 * Creo la clase con sus atributos, getters y constructor 
 */
public class Genero {
    private int idGenero;
    private String nombre;
    private String imagenUrl;

    public Genero(int idGenero, String nombre, String imagenUrl) {
        this.idGenero = idGenero;
        this.nombre = nombre;
        this.imagenUrl = imagenUrl;
    }

    public int getIdGenero() { return idGenero; }
    public void setIdGenero(int idGenero) { this.idGenero = idGenero; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
}
