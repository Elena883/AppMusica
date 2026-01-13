/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import java.util.List;
import vo.Cancion;

/**
 *
 * @author Elena
 * Creo una interfaz con los metodos que hay que implementar
 */
public interface IAlbumDao {
    public List<Cancion> obtenerCancionesPorAlbum(int idAlbum);
}
