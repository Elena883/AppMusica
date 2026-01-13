/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import vo.Cancion;

/**
 *
 * @author Elena
 * Creo una interfaz con los metodos que hay que implementar
 */
public interface ICancionDao {
    public Cancion obtenerCancionPorTitulo(String titulo);
}
