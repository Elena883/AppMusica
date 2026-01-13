/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestor;
import java.io.*;
import java.net.URL;
/**
 *
 * @author Elena
 */
public class DescargadorMP3 {
    
    
   //con este metodo descargo la cancion para poder usarla con la libreria
   public static File descargar(String urlDrive) throws Exception {

        
        String id;

        if (urlDrive.contains("/d/")) {
            id = urlDrive.split("/d/")[1].split("/")[0];
        } else if (urlDrive.contains("id=")) {
            id = urlDrive.split("id=")[1];
        } else {
            throw new Exception("URL de Drive no válida");
        }

        
        String urlDescarga =
            "https://drive.google.com/uc?export=download&id=" + id;

        System.out.println("Descargando desde: " + urlDescarga);

       
        File mp3 = File.createTempFile("cancion_", ".mp3");
        mp3.deleteOnExit();

        
        try (InputStream in = new URL(urlDescarga).openStream();
             FileOutputStream out = new FileOutputStream(mp3)) {

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        return mp3;
    }
}
