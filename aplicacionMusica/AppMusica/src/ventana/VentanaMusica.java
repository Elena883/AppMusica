package ventana;


import dao.impl.CancionDaoImpl;
import dao.impl.AlbumDaoImpl;
import dao.impl.ArtistaDaoImpl;
import dao.impl.CancionArtistaDaoImpl;
import dao.impl.GeneroDaoImpl;
import dao.impl.ListaDaoImpl;
import gestor.GestorCanciones;

import java.awt.Color;
import gestor.GestorConexion;
import gestor.ReproductorMusica;
import java.awt.Image;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import vo.Cancion;
import javax.swing.DefaultListModel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.ImageIcon;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**ha
 *
 * @author T
 */
public class VentanaMusica extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VentanaMusica.class.getName());
    GestorConexion _conexion = new GestorConexion();
    PanelVacio panelVacio;
    PanelInicio panelInicio;
    PanelAlbumes panelAlbumes;
    PanelArtistas panelArtistas;
    PanelListas panelListas;
    PanelLetra panelLetra;
    ReproductorMusica reproductor = new ReproductorMusica();
    private String tituloCancionActual = null;
    private Cancion cancionActual = null;
    //atributos para el slider
    private boolean estaSonando = false;
    private double tiempoActual = 0;
    private double duracionCancion = 0;
    private Thread hiloSlider;

    
    //atributos para usar los metodos de cada clase
    private CancionDaoImpl cancionDao;
    private AlbumDaoImpl albumDao;
    private ArtistaDaoImpl artistaDao;
    private CancionArtistaDaoImpl cancionArtistaDao;
    private GeneroDaoImpl generoDao;
    private ListaDaoImpl listaDao;
    private GestorCanciones gestorCanciones;
    
    
    private List<Cancion> listaActual = new ArrayList<>();
    private int indiceActual = -1;
    private DefaultListModel<String> modeloCola = new DefaultListModel<>();
    boolean cambios = false;
    private boolean modoRepetir = false;
    private boolean modoAleatorio = false;


    /**
     * Creates new form VentanaMusica
     *  
     */
    public VentanaMusica() {
        initComponents();
       
        lCola.setModel(modeloCola);

        
        _conexion = new GestorConexion();
        _conexion.conectarBBDD(this);
        
        gestorCanciones = new GestorCanciones(_conexion.getConexion());
        cancionDao = new CancionDaoImpl(_conexion.getConexion());
        cancionArtistaDao = new CancionArtistaDaoImpl(_conexion.getConexion()); 
        albumDao = new AlbumDaoImpl(_conexion.getConexion()); 
        artistaDao = new ArtistaDaoImpl(_conexion.getConexion()); 
        generoDao = new GeneroDaoImpl(_conexion.getConexion()); 
        listaDao = new ListaDaoImpl(_conexion.getConexion()); 
         
        panelInicio = new PanelInicio(this);
        panelVacio = new PanelVacio(this); 
        panelAlbumes = new PanelAlbumes(this);
        panelArtistas = new PanelArtistas(this);
        panelListas = new PanelListas(this);
        panelLetra = new PanelLetra();
        
        bListas.setBackground(new Color(68,68,68));
        bListas.setForeground(Color.WHITE);
        bArtistas.setBackground(Color.WHITE);
        bArtistas.setForeground(Color.black);
        bAlbumes.setBackground(new Color(68,68,68));
        bAlbumes.setForeground(Color.WHITE);
        pContenedorBiblioteca.add(panelArtistas, SwingConstants.CENTER);
        pContenedorBiblioteca.revalidate();
        pContenedorBiblioteca.repaint();
               
        pContenedorInicio.add(panelInicio, SwingConstants.CENTER);
        pContenedorInicio.revalidate();
        pContenedorInicio.repaint();
        
        
        /*
        
        
        */
        
    }
    
    //para que se mueva el slider
    private void iniciarSlider() {

        if (hiloSlider != null && hiloSlider.isAlive()) {
            hiloSlider.interrupt();
        }

        hiloSlider = new Thread(() -> {
            while (estaSonando && tiempoActual <= duracionCancion) {
                try {
                    Thread.sleep(1000);
                    tiempoActual++;

                    int valor = (int) ((tiempoActual / duracionCancion) * 100);

                    javax.swing.SwingUtilities.invokeLater(() -> {
                        sPosicion.setValue(valor);
                    });

                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        hiloSlider.start();
    }

    
    //metodo para obtener la lista actual que se esta reproduciendo
    public List<Cancion> getListaActual() {
        return listaActual;
    }   

    
    //metodo para actualizar la informacion de la cancion
    public void actualizarInfoCancion(Cancion c) {

        lTituloCancionReproduciendo.setText(c.getTitulo());

        try {
            String nombreArtista = cancionArtistaDao.obtenerArtistaPorCancion(c.getId());
            lArtistaCancionReproduciendo.setText(nombreArtista);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    //metodo en el quue llamo a la clase CancionArtistaDaoImpl para obtener el artista de la cancion
    public String getArtistaDeCancion(int idCancion) {
        return cancionArtistaDao.obtenerArtistaPorCancion(idCancion);
    }

    //metodo para poner los valores a las variables para la cola por defecto
    public void setListaActual(List<Cancion> canciones) {
        this.listaActual = canciones;
        this.indiceActual = 0;
        actualizarCola();
    }
    
    
    //metodo para actualizar la cola 
    private void actualizarCola() {
    modeloCola.clear();

        if (listaActual == null || listaActual.isEmpty()) return;

            for (int i = indiceActual; i < listaActual.size(); i++) {
                modeloCola.addElement(listaActual.get(i).getTitulo());
                
            }
    }
    
    //metodo para añadir los valores a las variables para actualizar cola cuando selecciono una cancion de la lista
    public void reproducirDesdeLista(Cancion c) {
        indiceActual = listaActual.indexOf(c);
        reproducirCancion(c.getTitulo());
        actualizarCola();
        duracionCancion = c.getDuracion(); // de la BBDD (en segundos)
        tiempoActual = 0;
        estaSonando = true;

        iniciarSlider();

    }
    
    //metodo para pasar a la siguiente cancion
    public void siguienteCancion() {

    if (listaActual == null || listaActual.isEmpty()) return;

    //repetir canción actual
    if (modoRepetir) {
        reproducirCancion(listaActual.get(indiceActual).getTitulo());
        return;
    }

    //para el modo aleatorio
    if (modoAleatorio) {
        int nuevoIndice;
        do {
            nuevoIndice = (int) (Math.random() * listaActual.size());
        } while (nuevoIndice == indiceActual && listaActual.size() > 1);

        indiceActual = nuevoIndice;
    } 
    //para pasar de canciones normal
    else {
        if (indiceActual < listaActual.size() - 1) {
            indiceActual++;
        } else {
            return; 
        }
    }

    reproducirCancion(listaActual.get(indiceActual).getTitulo());
    actualizarCola();
}
    
    
    //para pasar a la cancion anterior
    public void cancionAnterior() {

        if (listaActual == null || listaActual.isEmpty()) return;

        if (indiceActual > 0) {
            indiceActual--;
            Cancion c = listaActual.get(indiceActual);
            reproducirCancion(c.getTitulo());
            actualizarCola();
        }
    }

    
    //metodo en el que llamo a GestorCanciones para buscar una cancion segun lo que pide el usuario
    public void buscar(String texto) {
        List<Cancion> resultados = gestorCanciones.buscarCanciones(texto);

        setListaActual(resultados);
        mostrarCancionesEnVacio(resultados);
    }
    
    //metodo en el que llamo a AlbumDaImpl para obtener la lista de canciones 
    public List<Cancion> getCancionesPorAlbum(int id_album) {
        return albumDao.obtenerCancionesPorAlbum(id_album);
    }

    //metodo en el que llamo a ArtistaDaImpl para obtener la lista de canciones 
     public List<Cancion> getCancionesPorArtista(int id_artista) {
        return artistaDao.obtenerCancionesPorArtista(id_artista);
    }

   //metodo en el que llamo a GeneroDaImpl para obtener la lista de canciones 
    public List<Cancion> getCancionesPorGenero(int id_genero) {
        return generoDao.obtenerCancionesPorGenero(id_genero);
    }

    //metodo en el que llamo a ListaDaImpl para obtener la lista de canciones 
    public List<Cancion> getCancionesPorLista(int id_lista) {
        return listaDao.obtenerCancionesPorLista(id_lista);
    }
    
    //metodo en el que llamo a PanelVacio para poner las canciones en la lista y en el panel
    public void mostrarCancionesEnVacio(List<Cancion> canciones) {
        // quitar otros paneles si es necesario
        pContenedorInicio.removeAll();

        // llenar la lista del panelVacio
        panelVacio.mostrarCanciones(canciones);
        setListaActual(canciones);
        // añadir panelVacio al contenedor
        pContenedorInicio.add(panelVacio, java.awt.BorderLayout.CENTER);
        pContenedorInicio.revalidate();
        pContenedorInicio.repaint();
}

    //metodo para que me salgan los ventana de albumes
    public void mostrarAlbumes() {
        pContenedorInicio.remove(panelInicio);
        pContenedorInicio.remove(panelLetra);
        pContenedorInicio.add(panelVacio, SwingConstants.CENTER);
        pContenedorInicio.revalidate();
        pContenedorInicio.repaint();
    }
    
    //metodo para reproducir una cancion desde la lista
    public void reproducirDesdeLista(int index) {

        if (listaActual == null || index < 0 || index >= listaActual.size()) {
            return;
        }

        indiceActual = index;
        Cancion c = listaActual.get(indiceActual);

        tituloCancionActual = c.getTitulo();   
        actualizarInfoCancion(c);

        reproductor.reproducir(c.getAudioUrl(), this);

        bPausa.setText("⏸");                   
        actualizarCola();
        duracionCancion = c.getDuracion(); // de la BBDD (en segundos)
        tiempoActual = 0;
        estaSonando = true;

        iniciarSlider();

    }
      

    
    
    //metodo para reproducir la cancion y obtener el titulo
    public void reproducirCancion(String titulo) {

        CancionDaoImpl cargador =
            new CancionDaoImpl(_conexion.getConexion());

        Cancion c = cargador.obtenerCancionPorTitulo(titulo);

        if (c != null) {
            tituloCancionActual = titulo;

            actualizarInfoCancion(c);

            reproductor.reproducir(c.getAudioUrl(), this);

            
            bPausa.setText("⏸");
            duracionCancion = c.getDuracion(); // de la BBDD (en segundos)
            tiempoActual = 0;
            estaSonando = true;

            iniciarSlider();


        } else {
            System.out.println("Canción no encontrada: " + titulo);
        }
    }





    // metodo para pausar cancion
    public void pausarCancion() {
        reproductor.pausar();
        estaSonando = false;
    }

    // metodo para reanudar cancion
    public void reanudarCancion() {
        reproductor.reanudar();
        estaSonando = true;
        iniciarSlider();
    }

    // metodo para detener cancion
    public void detenerCancion() {
        reproductor.parar();
    }
    
    
    //metodo para usar el boton de pausa y play
    public void botonPausar() {

    if (tituloCancionActual == null) return;

    if (bPausa.getText().equals("⏸")) {
        pausarCancion();
        bPausa.setText("▶");
    } else {
        reanudarCancion();
        bPausa.setText("⏸");
    }
}

    
    //metodo en el que llamo al ReproductorMusica para que suene la cancion
    public ReproductorMusica getReproductor() {
        return reproductor;
    }
    
    //metodo para poder usar la conexion en otras clases
     public GestorConexion getGestorConexion() {
        return _conexion;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        tBuscar = new javax.swing.JTextField();
        bCasa = new javax.swing.JButton();
        bBuscar = new javax.swing.JButton();
        pBiblioteca = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        bListas = new javax.swing.JButton();
        bAlbumes = new javax.swing.JButton();
        bArtistas = new javax.swing.JButton();
        pContenedorBiblioteca = new javax.swing.JPanel();
        pCola = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lCola = new javax.swing.JList<>();
        pReproduccion = new javax.swing.JPanel();
        lFotoCancionReproduciendo = new javax.swing.JLabel();
        lTituloCancionReproduciendo = new javax.swing.JLabel();
        bPausa = new javax.swing.JButton();
        lArtistaCancionReproduciendo = new javax.swing.JLabel();
        bSiguiente = new javax.swing.JButton();
        bAnterior = new javax.swing.JButton();
        bRepetir = new javax.swing.JButton();
        bAleatorio = new javax.swing.JButton();
        sPosicion = new javax.swing.JSlider();
        bLista = new javax.swing.JButton();
        bMicrofono = new javax.swing.JButton();
        bInfo = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        pContenedorInicio = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        Listas = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Espatify");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));
        jPanel1.setForeground(new java.awt.Color(102, 102, 102));

        tBuscar.setBackground(new java.awt.Color(68, 68, 68));
        tBuscar.setForeground(new java.awt.Color(204, 204, 204));
        tBuscar.setText("¿Qué quieres reproducir?");
        tBuscar.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(68, 68, 68), 9, true));
        tBuscar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tBuscarMouseClicked(evt);
            }
        });
        tBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tBuscarActionPerformed(evt);
            }
        });

        bCasa.setBackground(new java.awt.Color(68, 68, 68));
        bCasa.setForeground(new java.awt.Color(204, 204, 204));
        bCasa.setText("🏠");
        bCasa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bCasaActionPerformed(evt);
            }
        });

        bBuscar.setBackground(new java.awt.Color(68, 68, 68));
        bBuscar.setForeground(new java.awt.Color(204, 204, 204));
        bBuscar.setText("🔍");
        bBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bBuscarActionPerformed(evt);
            }
        });

        pBiblioteca.setBackground(new java.awt.Color(68, 68, 68));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Tu Biblioteca");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(204, 204, 204));
        jLabel2.setText("+");

        bListas.setBackground(new java.awt.Color(68, 68, 68));
        bListas.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        bListas.setForeground(new java.awt.Color(255, 255, 255));
        bListas.setText("Listas");
        bListas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bListasActionPerformed(evt);
            }
        });

        bAlbumes.setBackground(new java.awt.Color(68, 68, 68));
        bAlbumes.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        bAlbumes.setForeground(new java.awt.Color(255, 255, 255));
        bAlbumes.setText("Albumes");
        bAlbumes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAlbumesActionPerformed(evt);
            }
        });

        bArtistas.setBackground(new java.awt.Color(68, 68, 68));
        bArtistas.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        bArtistas.setForeground(new java.awt.Color(255, 255, 255));
        bArtistas.setText("Artistas");
        bArtistas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bArtistasActionPerformed(evt);
            }
        });

        pContenedorBiblioteca.setBackground(new java.awt.Color(68, 68, 68));
        pContenedorBiblioteca.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout pBibliotecaLayout = new javax.swing.GroupLayout(pBiblioteca);
        pBiblioteca.setLayout(pBibliotecaLayout);
        pBibliotecaLayout.setHorizontalGroup(
            pBibliotecaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pBibliotecaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pBibliotecaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pBibliotecaLayout.createSequentialGroup()
                        .addGroup(pBibliotecaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pBibliotecaLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(161, 161, 161)
                                .addComponent(jLabel2))
                            .addGroup(pBibliotecaLayout.createSequentialGroup()
                                .addComponent(bListas, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(bAlbumes)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(bArtistas, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 43, Short.MAX_VALUE))
                    .addComponent(pContenedorBiblioteca, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pBibliotecaLayout.setVerticalGroup(
            pBibliotecaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pBibliotecaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pBibliotecaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pBibliotecaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bAlbumes)
                    .addComponent(bListas)
                    .addComponent(bArtistas))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pContenedorBiblioteca, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pCola.setBackground(new java.awt.Color(68, 68, 68));

        lCola.setBackground(new java.awt.Color(68, 68, 68));
        lCola.setForeground(new java.awt.Color(255, 255, 255));
        lCola.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(lCola);

        javax.swing.GroupLayout pColaLayout = new javax.swing.GroupLayout(pCola);
        pCola.setLayout(pColaLayout);
        pColaLayout.setHorizontalGroup(
            pColaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pColaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                .addContainerGap())
        );
        pColaLayout.setVerticalGroup(
            pColaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pColaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
                .addContainerGap())
        );

        pReproduccion.setBackground(new java.awt.Color(51, 51, 51));

        lFotoCancionReproduciendo.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lFotoCancionReproduciendo.setForeground(new java.awt.Color(255, 255, 255));
        lFotoCancionReproduciendo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lFotoCancionReproduciendo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/NuevoSonido.png"))); // NOI18N
        lFotoCancionReproduciendo.setPreferredSize(new java.awt.Dimension(48, 48));

        lTituloCancionReproduciendo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lTituloCancionReproduciendo.setForeground(new java.awt.Color(255, 255, 255));

        bPausa.setText("▶");
        bPausa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bPausaActionPerformed(evt);
            }
        });

        lArtistaCancionReproduciendo.setForeground(new java.awt.Color(255, 255, 255));

        bSiguiente.setBackground(new java.awt.Color(68, 68, 68));
        bSiguiente.setForeground(new java.awt.Color(255, 255, 255));
        bSiguiente.setText("⏭");
        bSiguiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSiguienteActionPerformed(evt);
            }
        });

        bAnterior.setBackground(new java.awt.Color(68, 68, 68));
        bAnterior.setForeground(new java.awt.Color(255, 255, 255));
        bAnterior.setText("⏮");
        bAnterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAnteriorActionPerformed(evt);
            }
        });

        bRepetir.setBackground(new java.awt.Color(68, 68, 68));
        bRepetir.setForeground(new java.awt.Color(255, 255, 255));
        bRepetir.setText("🔁");
        bRepetir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRepetirActionPerformed(evt);
            }
        });

        bAleatorio.setBackground(new java.awt.Color(68, 68, 68));
        bAleatorio.setForeground(new java.awt.Color(255, 255, 255));
        bAleatorio.setText("🔀");
        bAleatorio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAleatorioActionPerformed(evt);
            }
        });

        sPosicion.setBackground(new java.awt.Color(255, 255, 255));
        sPosicion.setForeground(new java.awt.Color(0, 255, 0));

        bLista.setBackground(new java.awt.Color(68, 68, 68));
        bLista.setForeground(new java.awt.Color(255, 255, 255));
        bLista.setText("☰");

        bMicrofono.setBackground(new java.awt.Color(68, 68, 68));
        bMicrofono.setForeground(new java.awt.Color(255, 255, 255));
        bMicrofono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/microfono (1).png"))); // NOI18N
        bMicrofono.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bMicrofonoActionPerformed(evt);
            }
        });

        bInfo.setBackground(new java.awt.Color(68, 68, 68));
        bInfo.setForeground(new java.awt.Color(255, 255, 255));
        bInfo.setText("▣");

        javax.swing.GroupLayout pReproduccionLayout = new javax.swing.GroupLayout(pReproduccion);
        pReproduccion.setLayout(pReproduccionLayout);
        pReproduccionLayout.setHorizontalGroup(
            pReproduccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pReproduccionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lFotoCancionReproduciendo, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(pReproduccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lTituloCancionReproduciendo, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lArtistaCancionReproduciendo, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(208, 208, 208)
                .addGroup(pReproduccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pReproduccionLayout.createSequentialGroup()
                        .addComponent(sPosicion, javax.swing.GroupLayout.PREFERRED_SIZE, 454, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(444, Short.MAX_VALUE))
                    .addGroup(pReproduccionLayout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addComponent(bAleatorio, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(bAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(bPausa, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(bSiguiente, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(bRepetir, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(bInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42)
                        .addComponent(bMicrofono, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42)
                        .addComponent(bLista, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21))))
        );
        pReproduccionLayout.setVerticalGroup(
            pReproduccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pReproduccionLayout.createSequentialGroup()
                .addGroup(pReproduccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pReproduccionLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(pReproduccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pReproduccionLayout.createSequentialGroup()
                                .addGroup(pReproduccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(bPausa, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(bSiguiente)
                                    .addComponent(bAnterior)
                                    .addComponent(bRepetir)
                                    .addComponent(bAleatorio))
                                .addGap(18, 18, 18))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pReproduccionLayout.createSequentialGroup()
                                .addGroup(pReproduccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lFotoCancionReproduciendo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(pReproduccionLayout.createSequentialGroup()
                                        .addComponent(lTituloCancionReproduciendo)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lArtistaCancionReproduciendo)))
                                .addGap(3, 3, 3))))
                    .addGroup(pReproduccionLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(pReproduccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(bLista)
                            .addComponent(bMicrofono)
                            .addComponent(bInfo))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)))
                .addComponent(sPosicion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/usuario.png"))); // NOI18N

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/NuevoLogo.png"))); // NOI18N
        jLabel14.setText("jLabel14");

        pContenedorInicio.setBackground(new java.awt.Color(68, 68, 68));
        pContenedorInicio.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pBiblioteca, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(bCasa, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 673, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(bBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(pContenedorInicio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pCola, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel10)
                                .addGap(11, 11, 11))))
                    .addComponent(pReproduccion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel14)
                        .addComponent(bCasa, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(bBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pCola, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pBiblioteca, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pContenedorInicio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pReproduccion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jMenuBar1.setBackground(new java.awt.Color(204, 204, 204));

        jMenu1.setText("Aplicacion");

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem2.setText("Inicio");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);
        jMenu1.add(jSeparator2);

        Listas.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        Listas.setText("Listas");
        Listas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ListasActionPerformed(evt);
            }
        });
        jMenu1.add(Listas);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem5.setText("Albumes");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem4.setText("Artistas");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);
        jMenu1.add(jSeparator1);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem1.setText("Cerrar Aplicacion");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Musica");

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SPACE, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem3.setText("Pausar");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_RIGHT, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem6.setText("Siguiente");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem6);

        jMenuItem7.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_LEFT, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem7.setText("Anterior");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem7);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    //el boton para buscar la informacion que llama a otro metodo
    private void bBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bBuscarActionPerformed
       String texto = tBuscar.getText().trim();

        if (!texto.isEmpty()) {
            buscar(texto);
        }      
    }//GEN-LAST:event_bBuscarActionPerformed

    private void tBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tBuscarActionPerformed
    
    //boton en el que cambio al panel de listas
    private void bListasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bListasActionPerformed
       pContenedorBiblioteca.remove(panelArtistas);
       pContenedorBiblioteca.remove(panelAlbumes);
       pContenedorBiblioteca.add(panelListas, SwingConstants.CENTER);
       pContenedorBiblioteca.revalidate();
       pContenedorBiblioteca.repaint();
       
       bListas.setBackground(Color.WHITE);
       bListas.setForeground(Color.black);
       bArtistas.setBackground(new Color(68,68,68));
       bArtistas.setForeground(Color.WHITE);
       bAlbumes.setBackground(new Color(68,68,68));
       bAlbumes.setForeground(Color.WHITE);
       
       
    }//GEN-LAST:event_bListasActionPerformed
    
    
    //boton en el que cambio al panel de albumes
    private void bAlbumesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAlbumesActionPerformed
       pContenedorBiblioteca.remove(panelListas);
       pContenedorBiblioteca.remove(panelArtistas);
       pContenedorBiblioteca.add(panelAlbumes, SwingConstants.CENTER);
       pContenedorBiblioteca.revalidate();
       pContenedorBiblioteca.repaint();
       
       bListas.setBackground(new Color(68,68,68));
       bListas.setForeground(Color.WHITE);
       bArtistas.setBackground(new Color(68,68,68));
       bArtistas.setForeground(Color.WHITE);
       bAlbumes.setBackground(Color.WHITE);
       bAlbumes.setForeground(Color.black);
       
    }//GEN-LAST:event_bAlbumesActionPerformed
    
    //boton en el que cambio al panel de artistas
    private void bArtistasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bArtistasActionPerformed
       pContenedorBiblioteca.remove(panelListas);
       pContenedorBiblioteca.remove(panelAlbumes);
       pContenedorBiblioteca.add(panelArtistas, SwingConstants.CENTER);
       pContenedorBiblioteca.revalidate();
       pContenedorBiblioteca.repaint();
       
       bListas.setBackground(new Color(68,68,68));
       bListas.setForeground(Color.WHITE);
       bArtistas.setBackground(Color.WHITE);
       bArtistas.setForeground(Color.black);
       bAlbumes.setBackground(new Color(68,68,68));
       bAlbumes.setForeground(Color.WHITE);
    }//GEN-LAST:event_bArtistasActionPerformed

    //boton para ir de vuelta al inicio
    private void bCasaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCasaActionPerformed
      pContenedorInicio.remove(panelVacio);
      pContenedorInicio.remove(panelLetra);
      pContenedorInicio.add(panelInicio, SwingConstants.CENTER);
      pContenedorInicio.revalidate();
      pContenedorInicio.repaint();
       
    }//GEN-LAST:event_bCasaActionPerformed

    // para cuando le de a buscar se borre el texto
    private void tBuscarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tBuscarMouseClicked
        tBuscar.setText("");
    }//GEN-LAST:event_tBuscarMouseClicked
    //boton para la letra de la cancion
    private void bMicrofonoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bMicrofonoActionPerformed
      
      if(cambios == true){
        pContenedorInicio.remove(panelLetra);
        pContenedorInicio.remove(panelInicio);
        pContenedorInicio.add(panelVacio, SwingConstants.CENTER);
        pContenedorInicio.revalidate();
        pContenedorInicio.repaint();
        cambios= false;
      }else{
        pContenedorInicio.remove(panelVacio);
        pContenedorInicio.remove(panelInicio);
        pContenedorInicio.add(panelLetra, SwingConstants.CENTER);
        pContenedorInicio.revalidate();
        pContenedorInicio.repaint();
        cambios = true;
      }
      
    }//GEN-LAST:event_bMicrofonoActionPerformed
    //boton para pausar la cancion
    private void bPausaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bPausaActionPerformed
       botonPausar();
       
    }//GEN-LAST:event_bPausaActionPerformed
    //boton para pasar la cancion
    private void bSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSiguienteActionPerformed
        siguienteCancion();
    }//GEN-LAST:event_bSiguienteActionPerformed
    //boton para ir a la cancion anterior
    private void bAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAnteriorActionPerformed
       cancionAnterior();
    }//GEN-LAST:event_bAnteriorActionPerformed
    //boton para que se repita la cancion
    private void bRepetirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRepetirActionPerformed
        modoRepetir = !modoRepetir;

        if (modoRepetir) {
            bRepetir.setForeground(new Color(0,255,0)); 
        } else {
            bRepetir.setForeground(Color.WHITE);
        }
    }//GEN-LAST:event_bRepetirActionPerformed
    //boton para poner el modo aleatorio de la lista
    private void bAleatorioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAleatorioActionPerformed
        modoAleatorio = !modoAleatorio;

        if (modoAleatorio) {
            bAleatorio.setForeground(new Color(0,255,0));
        } else {
            bAleatorio.setForeground(Color.WHITE);
        }
    }//GEN-LAST:event_bAleatorioActionPerformed
    //atajo de teclado para cerrar la aplicacion
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jMenuItem1ActionPerformed
    //atajo de teclado para parar la cancion
    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        bPausa.doClick();
    }//GEN-LAST:event_jMenuItem3ActionPerformed
    //atajo de teclado para ir al inicio 
    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        bCasa.doClick();
    }//GEN-LAST:event_jMenuItem2ActionPerformed
    //atajo de teclado para ir al panel de listas
    private void ListasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ListasActionPerformed
        bListas.doClick();
    }//GEN-LAST:event_ListasActionPerformed
    //atajo de teclado para ir a la cancion anterior
    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
       bAnterior.doClick();
    }//GEN-LAST:event_jMenuItem7ActionPerformed
    //atajo de teclado para ir al panel de albumes
    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        bAlbumes.doClick();
    }//GEN-LAST:event_jMenuItem5ActionPerformed
//atajo de teclado para ir al panel de artistas
    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        bArtistas.doClick();
    }//GEN-LAST:event_jMenuItem4ActionPerformed
    //atajo de teclado para ir a la siguiente cancion
    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
       bSiguiente.doClick();
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new VentanaMusica().setVisible(true));
        
        // Colores base personalizados para que coincidan con el diseño oscuro
        UIManager.put("nimbusBase",new Color(51,51,51));
        UIManager.put("nimbusBlueGrey",new Color(102,102,102));
        UIManager.put("control", new Color(51,51,51));
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Listas;
    private javax.swing.JButton bAlbumes;
    private javax.swing.JButton bAleatorio;
    private javax.swing.JButton bAnterior;
    private javax.swing.JButton bArtistas;
    private javax.swing.JButton bBuscar;
    private javax.swing.JButton bCasa;
    private javax.swing.JButton bInfo;
    private javax.swing.JButton bLista;
    private javax.swing.JButton bListas;
    private javax.swing.JButton bMicrofono;
    private javax.swing.JButton bPausa;
    private javax.swing.JButton bRepetir;
    private javax.swing.JButton bSiguiente;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JLabel lArtistaCancionReproduciendo;
    private javax.swing.JList<String> lCola;
    private javax.swing.JLabel lFotoCancionReproduciendo;
    private javax.swing.JLabel lTituloCancionReproduciendo;
    private javax.swing.JPanel pBiblioteca;
    private javax.swing.JPanel pCola;
    private javax.swing.JPanel pContenedorBiblioteca;
    private javax.swing.JPanel pContenedorInicio;
    private javax.swing.JPanel pReproduccion;
    private javax.swing.JSlider sPosicion;
    private javax.swing.JTextField tBuscar;
    // End of variables declaration//GEN-END:variables
}
/*
 
*/