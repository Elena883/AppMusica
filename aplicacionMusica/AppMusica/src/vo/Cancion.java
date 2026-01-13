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
public class Cancion {
      private int id;
    private String titulo;
    private int duracion;
    private String audioUrl;
    private String imagenUrl;
    private Integer idAlbum;
    private Integer idGenero;

    public Cancion(int id, String titulo, int duracion,
                   String audioUrl, String imagenUrl,
                   Integer idAlbum, Integer idGenero) {
        this.id = id;
        this.titulo = titulo;
        this.duracion = duracion;
        this.audioUrl = audioUrl;
        this.imagenUrl = imagenUrl;
        this.idAlbum = idAlbum;
        this.idGenero = idGenero;
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public int getDuracion() { return duracion; }
    public String getAudioUrl() { return audioUrl; }
    public String getImagenUrl() { return imagenUrl; }
    public Integer getIdAlbum() { return idAlbum; }
    public Integer getIdGenero() { return idGenero; }

    @Override
    public String toString() {
        return titulo;
    }
    
}
