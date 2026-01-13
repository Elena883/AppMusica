/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestor;

import ventana.VentanaMusica;
import java.sql.Connection; 
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 *
 * @author Elena Sánchez
 */
public class GestorConexion {
    //creo un atributo de tipo Connection
    private Connection _conexion;
    
    //creo un metodo para hacer la conexion al que le paso un objeto GestorAeropuerto para poder utilizar la etiqueta Status
    public void conectarBBDD(VentanaMusica vm){
        
        try {
            //tras haber metido el jar utilizo el DriverManagger para crear la conexion
            _conexion = DriverManager.getConnection("jdbc:"
                    + "mysql:"
                    + "//localhost:3306/"
                    + "appMusica?user=root&"
                    + "password=");
           
               

        
        } catch (SQLException ex) {
            System.getLogger(GestorConexion.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    
           
       
    
    
    
    }
    
    
    //creo un metodo para devolver la conexion y poder acceder desde otras clases
     public Connection getConexion() {
        return _conexion;
    }
}
