/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.impl;
import dao.ICancionDao;
import java.sql.Connection;
import vo.Cancion;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 *
 * @author Elena
 */
public class CancionDaoImpl implements ICancionDao {
   
     private Connection conexion;

    public CancionDaoImpl(Connection conexion) {
        this.conexion = conexion;
    }

    
    // Obtengo una cancion por el titulo de la cancion
    public Cancion obtenerCancionPorTitulo(String titulo) {

        String sql = "SELECT * FROM canciones WHERE titulo = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, titulo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Cancion(
                    rs.getInt("id_cancion"),
                    rs.getString("titulo"),
                    rs.getInt("duracion"),
                    rs.getString("audio_url"),
                    rs.getString("imagen_url"),
                    (Integer) rs.getObject("id_album"),
                    (Integer) rs.getObject("id_genero")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    

     

}
