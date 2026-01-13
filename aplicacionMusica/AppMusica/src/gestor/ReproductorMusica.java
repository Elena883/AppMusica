/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestor;
import jaco.mp3.player.MP3Player;
import java.io.File;
import ventana.VentanaMusica;
/**
 *
 * @author Elena
 */
public class ReproductorMusica {
   private MP3Player player; // creo una instancia que controla la canción actual
   private VentanaMusica ventana;
    // Reproduce una cancion desde la ruta
    public void reproducir(String urlMp3, VentanaMusica v) {
        ventana = v;
        try {
            File mp3 = DescargadorMP3.descargar(urlMp3); 

            // si ya hay una cancion sonando, la paro antes
            if (player != null) {
                player.stop();
            }

            // creo un nuevo MP3Player con el archivo descargado
            player = new MP3Player(mp3);
            player.play(); 
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Pausa la canción actual
    public void pausar() {
        if (player != null) {
            player.pause();
        }
    }

    // Reanuda la canción si está pausada
    public void reanudar() {
        if (player != null) {
            player.play();
        }
    }

    // Detiene la canción actual
    public void parar() {
        if (player != null) {
            player.stop();
        }
    }
    
   


    

}
