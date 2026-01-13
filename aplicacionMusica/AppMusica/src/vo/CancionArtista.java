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
public class CancionArtista {
     private int idCancion;
    private int idArtista;

    public CancionArtista(int idCancion, int idArtista) {
        this.idCancion = idCancion;
        this.idArtista = idArtista;
    }

    public int getIdCancion() { return idCancion; }
    public void setIdCancion(int idCancion) { this.idCancion = idCancion; }

    public int getIdArtista() { return idArtista; }
    public void setIdArtista(int idArtista) { this.idArtista = idArtista; }
}
