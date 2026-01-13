/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestor;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URL;
import java.sql.Connection;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.sql.PreparedStatement;
import java.sql.ResultSet;






/**
 *
 * @author Elena
 */
public class CargarImagenes {

     private Connection conexion;

    public CargarImagenes(GestorConexion gc) {
        this.conexion = gc.getConexion();
    }

    // metodo para cargar las imagenes en los botones 
    private void cargarEnBotones(String sql, JButton[] botones) {
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int i = 0;
            while (rs.next() && i < botones.length) {
                String url = rs.getString("imagen_url");

                if (url != null && !url.isEmpty() && !url.contains("sin_imagen")) {
                    String finalUrl = convertirUrlDrive(url);
                    ImageIcon icon = new ImageIcon(new URL(finalUrl));

                    JButton btn = botones[i];

                    // Escalar la imagen manteniendo la proporción
                    int anchoBtn = btn.getWidth();
                    int altoBtn = btn.getHeight();

                    // Si el botón todavía no tiene tamaño, usar un tamaño por defecto
                    if (anchoBtn == 0) anchoBtn = 150;
                    if (altoBtn == 0) altoBtn = 150;

                    ImageIcon iconEscalado = crearIconoEscalado(icon, anchoBtn, altoBtn);
                    btn.setIcon(iconEscalado);

                    // Opcional: añadir listener para redimensionar si cambia el tamaño del botón
                    btn.addComponentListener(new ComponentAdapter() {
                        @Override
                        public void componentResized(ComponentEvent e) {
                            int w = btn.getWidth();
                            int h = btn.getHeight();
                            if (w > 0 && h > 0) {
                                btn.setIcon(crearIconoEscalado(icon, w, h));
                            }
                        }
                    });
                }

                i++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //creo un ImageIcon escalado manteniendo la proporcion
    private ImageIcon crearIconoEscalado(ImageIcon icon, int ancho, int alto) {
        Image img = icon.getImage();

        double ratio = Math.min((double) ancho / img.getWidth(null),
                                (double) alto / img.getHeight(null));
 
        int nuevoAncho = (int) (img.getWidth(null) * ratio);
        int nuevoAlto = (int) (img.getHeight(null) * ratio);

        Image imgEscalada = img.getScaledInstance(nuevoAncho, nuevoAlto, Image.SCALE_SMOOTH);
        return new ImageIcon(imgEscalada);
    }


    // convierto la url para poder descargarla
    private String convertirUrlDrive(String url) {
        if (url.contains("drive.google.com")) {
            String id = url.split("/d/")[1].split("/")[0];
            return "https://drive.google.com/uc?export=download&id=" + id;
        }
        return url;
    }

    

    // Cargo las imagenes de las listas
    public void cargarListas(JButton[] botones) {
        cargarEnBotones(
            "SELECT imagen_url FROM listas_reproduccion WHERE imagen_url IS NOT NULL AND imagen_url <> '' LIMIT " + botones.length,
            botones
        );
    }
    
    
    // Cargo las imagenes de las canciones usando el campo 'orden' de la BD, evitando repeticiones de imagen
    public void cargarCanciones(JButton[] botones) {
    String sql = "SELECT imagen_url " +
                 "FROM canciones " +
                 "WHERE imagen_url IS NOT NULL " +
                 "  AND imagen_url <> '' " +
                 "  AND imagen_url NOT LIKE '%sin_imagen%' " +
                 "  AND orden BETWEEN 1 AND 8 " +
                 "ORDER BY orden ASC";

    cargarEnBotones(sql, botones);
}




    // // Cargo las imagenes de los albumes
    public void cargarAlbumes(JButton[] botones) {
        cargarEnBotones(
            "SELECT imagen_url FROM albumes WHERE imagen_url IS NOT NULL AND imagen_url <> '' LIMIT " + botones.length,
            botones
        );
    }

    
    // Cargo las imagenes de los artistas
    public void cargarArtistas(JButton[] botones, int id_Artista) {
    cargarEnBotones(
        "SELECT imagen_url " +
        "FROM artistas " +
        "WHERE imagen_url IS NOT NULL " +
        "AND imagen_url <> '' " +
        "AND imagen_url NOT LIKE '%sin_imagen%' " +
        "AND id_artista = " + id_Artista + " " +
        "LIMIT " + botones.length,
        botones
    );
}


    
    
    // // Cargo las imagenes de los generos
    public void cargarGeneros(JButton[] botones) {
        cargarEnBotones(
            "SELECT imagen_url FROM generos WHERE imagen_url IS NOT NULL AND imagen_url <> '' LIMIT " + botones.length,
            botones
        );
    }
}
    
    
    
   

