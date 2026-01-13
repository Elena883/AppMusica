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
public class Artista {
     private int idArtista;
    private String nombre;
    private String descripcion;
    private String imagenUrl;

    public Artista(int idArtista, String nombre, String descripcion, String imagenUrl) {
        this.idArtista = idArtista;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
    }

    public int getIdArtista() { return idArtista; }
    public void setIdArtista(int idArtista) { this.idArtista = idArtista; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    
}
