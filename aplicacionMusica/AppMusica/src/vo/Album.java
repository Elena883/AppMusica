/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vo;

import java.time.LocalDate;

/**
 *
 * @author Elena
 * Creo la clase con sus atributos, getters y constructor 
 */
public class Album {
     private int idAlbum;
    private String titulo;
    private LocalDate fechaLanzamiento;
    private String imagenUrl;
    private int idArtista;

    public Album(int idAlbum, String titulo, LocalDate fechaLanzamiento, String imagenUrl, int idArtista) {
        this.idAlbum = idAlbum;
        this.titulo = titulo;
        this.fechaLanzamiento = fechaLanzamiento;
        this.imagenUrl = imagenUrl;
        this.idArtista = idArtista;
    }

    public int getIdAlbum() { return idAlbum; }
    public void setIdAlbum(int idAlbum) { this.idAlbum = idAlbum; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public LocalDate getFechaLanzamiento() { return fechaLanzamiento; }
    public void setFechaLanzamiento(LocalDate fechaLanzamiento) { this.fechaLanzamiento = fechaLanzamiento; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public int getIdArtista() { return idArtista; }
    public void setIdArtista(int idArtista) { this.idArtista = idArtista; }
}
