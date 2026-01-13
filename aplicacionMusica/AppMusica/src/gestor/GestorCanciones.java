/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestor;
import java.sql.Connection; 
import java.util.ArrayList;
import java.util.List;
import vo.Cancion;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author T
 */
public class GestorCanciones {
    
     private Connection conexion;

    public GestorCanciones(Connection conexion) {
        this.conexion = conexion;
    }    
    
    
    //busco en la bbdd segun lo que escriba el usuario 
    public List<Cancion> buscarCanciones(String texto) {
    List<Cancion> canciones = new ArrayList<>();

        String sql = """
        SELECT DISTINCT c.*
        FROM canciones c
        LEFT JOIN albumes a ON c.id_album = a.id_album
        LEFT JOIN canciones_artistas ca ON c.id_cancion = ca.id_cancion
        LEFT JOIN artistas ar ON ca.id_artista = ar.id_artista
        LEFT JOIN generos g ON c.id_genero = g.id_genero
        WHERE
            c.titulo LIKE ?
            OR a.titulo LIKE ?
            OR ar.nombre LIKE ?
            OR g.nombre LIKE ?
        """;


    try (PreparedStatement ps = conexion.prepareStatement(sql)) {

        String filtro = "%" + texto + "%";
        ps.setString(1, filtro);
        ps.setString(2, filtro);
        ps.setString(3, filtro);
        ps.setString(4, filtro);
    

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            canciones.add(new Cancion(
                rs.getInt("id_cancion"),
                rs.getString("titulo"),
                rs.getInt("duracion"),
                rs.getString("audio_url"),
                rs.getString("imagen_url"),
                (Integer) rs.getObject("id_album"),
                (Integer) rs.getObject("id_genero")
            ));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return canciones;
}
    

    

}
