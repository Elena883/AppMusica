/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.impl;

import dao.IGeneroDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import vo.Cancion;

/**
 *
 * @author Elena
 */
public class GeneroDaoImpl implements IGeneroDao{
    
    
    private Connection conexion;

    public GeneroDaoImpl(Connection conexion) {
        this.conexion = conexion;
    }
    
    
    // Obtengo una lista de canciones por el id del genero
    public List<Cancion> obtenerCancionesPorGenero(int idGenero) {

        List<Cancion> canciones = new ArrayList<>();
        String sql = "SELECT * FROM canciones WHERE id_genero = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idGenero);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                canciones.add(crearCancion(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return canciones;
    }
    
    //creo un objeto cancion
    private Cancion crearCancion(ResultSet rs) throws SQLException {

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
}
