/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.impl;

import dao.ICancionArtistaDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import vo.Cancion;

/**
 *
 * @author Elena
 */
public class CancionArtistaDaoImpl implements ICancionArtistaDao{
    private Connection conexion;

    public CancionArtistaDaoImpl(Connection conexion) {
        this.conexion = conexion;
    }
    
    // Obtengo un artista por el id de la cancion
    public String obtenerArtistaPorCancion(int idCancion) {

        String sql = """
            SELECT ar.nombre
            FROM artistas ar
            JOIN canciones_artistas ca ON ar.id_artista = ca.id_artista
            WHERE ca.id_cancion = ?
            LIMIT 1
        """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idCancion);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("nombre");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "Desconocido";
    }
    
 
   
}
