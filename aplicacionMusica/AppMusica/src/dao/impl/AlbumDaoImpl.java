/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.impl;

import dao.IAlbumDao;
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
 * 
 */
public class AlbumDaoImpl implements IAlbumDao{
    private Connection conexion;

    public AlbumDaoImpl(Connection conexion) {
        this.conexion = conexion;
    }

    // Obtengo una lista de canciones por el id del Album
    public List<Cancion> obtenerCancionesPorAlbum(int idAlbum) {

        List<Cancion> canciones = new ArrayList<>();

        String sql = "SELECT * FROM canciones WHERE id_album = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idAlbum);
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
