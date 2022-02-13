/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrencia;

import distribucion.ClienteInformacion;
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author Adri
 */
public class MainInterfaz extends javax.swing.JFrame {

    //MAIN DE LA GASOLINERA
    //Aquí se crean los distintos vehículos, operarrios y, en este caso, los ocho surtidores.
    private Gestor gestor;
    private Log log;
    private ConcurrenciaGlobal parada;

    private int numeroVehiculos = 2000; //Cantidad de vehiculos que generamos
    private int numeroOperarios = 3;    //Cantidad de operarios que generamos
    private int numeroSurtidores = 8;   //Cantidad de surtidores que generamos
    private boolean empezado = false;   //Si el programa ha empezado o no
    private int parado = 0;             //Si el programa ha sido parado o no

    //Listas que gestionan la interfaz
    private JTextField[] jTextVehiculos; //Array
    private JTextField[] jTextOperarios;
    private ArrayList<String> arrayVehiculosEntrada = new ArrayList<>();
    private JLabel[] iconosVehiculos;
    private JLabel[] iconosOperarios;

    //ArrayList donde se guardan los surtidores
    private ArrayList<Surtidor> surtidores;

    //ArrayList que gestionan la interfaz distribuida
    private ArrayList<String> disVehiculosEnSurtidores = new ArrayList<>();
    private ArrayList<String> disOperariosEnSurtidores = new ArrayList<>();

    //locks que controlan la escritura
    Lock lock_escritura = new ReentrantLock();

    public MainInterfaz() throws RemoteException, MalformedURLException {
        

        ClienteInformacion remota = new ClienteInformacion(this);
        Registry registro = LocateRegistry.createRegistry(1099);
        Naming.rebind("//127.0.0.1/info", remota);

        initComponents();

        this.parada = new ConcurrenciaGlobal();
        this.log = new Log();
        this.gestor = new Gestor(this, parada, log);
        this.surtidores = new ArrayList();
        this.jTextVehiculos = new JTextField[]{su1, su2, su3, su4, su5, su6, su7, su8};
        this.jTextOperarios = new JTextField[]{op1, op2, op3, op4, op5, op6, op7, op8};
        this.iconosVehiculos = new JLabel[]{iv1, iv2, iv3, iv4, iv5, iv6, iv7, iv8};
        this.iconosOperarios = new JLabel[]{io1, io2, io3, io4, io5, io6, io7, io8};
    }

    public void crear() throws InterruptedException {
        //Genera los surtidores
        for (int i = 1; i < numeroSurtidores + 1; i++) {
            Surtidor surt = new Surtidor(i, gestor);
            surtidores.add(surt);
        }
        //Genera los vehiculos
        int tiempoLlegada = 0;
        for (int i = 1; i < numeroVehiculos + 1; i++) {
            tiempoLlegada = tiempoLlegada + this.gestor.obtenerAleatorio(500, 6000);
            Vehiculo v = new Vehiculo(i, log, gestor, tiempoLlegada);
            v.start();
        }
        //Genera los operarios
        for (int i = 1; i < numeroOperarios + 1; i++) {
            Operario o = new Operario(i, log, gestor);
            o.start();
        }
    }

    //---------------------------------------------------------------------------------------------------------------------//
    //Muestra en la interfaz la fila de coches en espera
    public void escribirEntradaVehiculos() {
        if (this.gestor.getListaEntradaVehiculos().isEmpty()) {
            jTextFieldCochesEsperando.setText("Cola de espera vacía");
        } else {
            jTextFieldCochesEsperando.setText(gestor.getListaEntradaVehiculos().toString());
            arrayVehiculosEntrada = gestor.getListaEntradaVehiculos();
        }
    }

    //Muestra en la interfaz los vehiculos de cada surtidor
    public void escribirSurtidores(String idVehiculo, int idSurtidor) {
        try {
            lock_escritura.lock();
            for (int i = 0; i < numeroSurtidores; i++) {
                disVehiculosEnSurtidores.add(""); //Rellena array de gestion de la interfaz distribuida para trabjar con los metodos set
                if (idSurtidor == i + 1) {
                    disVehiculosEnSurtidores.set(i, idVehiculo);
                    jTextVehiculos[i].setText(idVehiculo);
                    if (idVehiculo.equals("")) {
                        iconosVehiculos[i].setEnabled(false);
                    } else {
                        iconosVehiculos[i].setEnabled(true);
                    }
                }
            }
        } finally {
            lock_escritura.unlock();
        }
    }

    //Muestra en la interfaz los operarios de cada surtidor
    public void escribirOperarios(String idOpeario, int idSurtidor) {
        for (int i = 0; i < numeroSurtidores; i++) {
            disOperariosEnSurtidores.add(""); //Rellena array de gestion de la interfaz distribuida para trabjar con los metodos set
            if (idSurtidor == i + 1) {
                disOperariosEnSurtidores.set(i, idOpeario);
                jTextOperarios[i].setText(idOpeario);
                if (idOpeario.equals("")) {
                    iconosOperarios[i].setEnabled(false);
                } else {
                    iconosOperarios[i].setEnabled(true);
                }
            }
        }
    }

    public ArrayList<Surtidor> getArraySurtidores() {
        return surtidores;
    }

    public int getNumeroSurtidores() {
        return numeroSurtidores;
    }

    public int getNumeroVehiculos() {
        return numeroVehiculos;
    }
    
    public ArrayList<String> getArrayVehiculosEntrada() {
        return arrayVehiculosEntrada;
    }

    public ArrayList<String> getArrayVehiculosEnSurtidores() {
        return disVehiculosEnSurtidores;
    }

    public ArrayList<String> getArrayOperariosEnSurtidores() {
        return disOperariosEnSurtidores;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextFieldCochesEsperando = new javax.swing.JTextField();
        su6 = new javax.swing.JTextField();
        su3 = new javax.swing.JTextField();
        su4 = new javax.swing.JTextField();
        su5 = new javax.swing.JTextField();
        su7 = new javax.swing.JTextField();
        su8 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        op1 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        op2 = new javax.swing.JTextField();
        op3 = new javax.swing.JTextField();
        op4 = new javax.swing.JTextField();
        op5 = new javax.swing.JTextField();
        op6 = new javax.swing.JTextField();
        op7 = new javax.swing.JTextField();
        op8 = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        startButtom = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        su2 = new javax.swing.JTextField();
        su1 = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        iv8 = new javax.swing.JLabel();
        io8 = new javax.swing.JLabel();
        iv2 = new javax.swing.JLabel();
        iv3 = new javax.swing.JLabel();
        iv4 = new javax.swing.JLabel();
        iv5 = new javax.swing.JLabel();
        iv6 = new javax.swing.JLabel();
        iv7 = new javax.swing.JLabel();
        iv1 = new javax.swing.JLabel();
        io1 = new javax.swing.JLabel();
        io2 = new javax.swing.JLabel();
        io3 = new javax.swing.JLabel();
        io4 = new javax.swing.JLabel();
        io5 = new javax.swing.JLabel();
        io6 = new javax.swing.JLabel();
        io7 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Gasolinera");
        setIconImage(getIconImage());
        setLocation(new java.awt.Point(400, 250));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTextFieldCochesEsperando.setEditable(false);
        getContentPane().add(jTextFieldCochesEsperando, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 970, 30));

        su6.setEditable(false);
        su6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        su6.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(su6, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 340, 140, 40));

        su3.setEditable(false);
        su3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        su3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(su3, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 170, 140, 40));

        su4.setEditable(false);
        su4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        su4.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(su4, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 170, 140, 40));

        su5.setEditable(false);
        su5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        su5.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(su5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 340, 140, 40));

        su7.setEditable(false);
        su7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        su7.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(su7, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 340, 140, 40));

        su8.setEditable(false);
        su8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        su8.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(su8, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 340, 140, 40));

        jLabel1.setText("Surtidor 1");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, 60, -1));

        jLabel2.setText("Surtidor 2");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 150, 60, -1));

        jLabel3.setText("Surtidor 3");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 150, 60, -1));

        jLabel4.setText("Surtidor 4");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 150, 60, -1));

        jLabel5.setText("Surtidor 5");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 320, 60, -1));

        jLabel6.setText("Surtidor 6");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 320, 60, -1));

        jLabel7.setText("Surtidor 7");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 320, 60, -1));

        jLabel8.setText("Surtidor 8");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 320, 60, -1));

        jLabel9.setText("Coches esperando fuera de la gasolinera:");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, -1, -1));

        op1.setEditable(false);
        op1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        op1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(op1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 240, 140, -1));

        jLabel10.setText("Operado por:");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 390, -1, -1));

        op2.setEditable(false);
        op2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        op2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(op2, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 240, 140, -1));

        op3.setEditable(false);
        op3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        op3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(op3, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 240, 140, -1));

        op4.setEditable(false);
        op4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        op4.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(op4, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 240, 140, -1));

        op5.setEditable(false);
        op5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        op5.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(op5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 410, 140, -1));

        op6.setEditable(false);
        op6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        op6.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(op6, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 410, 140, -1));

        op7.setEditable(false);
        op7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        op7.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(op7, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 410, 140, -1));

        op8.setEditable(false);
        op8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        op8.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(op8, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 410, 140, -1));

        jLabel11.setText("Operado por:");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 220, -1, -1));

        jLabel12.setText("Operado por:");
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 220, -1, -1));

        jLabel13.setText("Operado por:");
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 220, -1, -1));

        jLabel14.setText("Operado por:");
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 220, -1, -1));

        jLabel15.setText("Operado por:");
        getContentPane().add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 390, -1, -1));

        jLabel16.setText("Operado por:");
        getContentPane().add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 390, -1, -1));

        jLabel17.setText("Operado por:");
        getContentPane().add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 390, -1, -1));

        startButtom.setBackground(new java.awt.Color(51, 51, 51));
        startButtom.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        startButtom.setForeground(new java.awt.Color(255, 255, 255));
        startButtom.setText("START");
        startButtom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtomActionPerformed(evt);
            }
        });
        getContentPane().add(startButtom, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 470, 390, 50));
        getContentPane().add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 530, 20, 20));

        su2.setEditable(false);
        su2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        su2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(su2, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 170, 140, 40));

        su1.setEditable(false);
        su1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        su1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(su1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 170, 140, 40));

        jLabel19.setBackground(new java.awt.Color(255, 51, 51));
        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("GASOLINERA - CONCURRENCIA");
        jLabel19.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(0, 0, 0)));
        getContentPane().add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 30, 400, 40));

        iv8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/iconVehiculo.png"))); // NOI18N
        iv8.setEnabled(false);
        getContentPane().add(iv8, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 340, 50, 40));

        io8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/iconOperario.png"))); // NOI18N
        io8.setEnabled(false);
        getContentPane().add(io8, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 400, 50, 50));

        iv2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/iconVehiculo.png"))); // NOI18N
        iv2.setEnabled(false);
        getContentPane().add(iv2, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 170, 50, 40));

        iv3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/iconVehiculo.png"))); // NOI18N
        iv3.setEnabled(false);
        getContentPane().add(iv3, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 170, 50, 40));

        iv4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/iconVehiculo.png"))); // NOI18N
        iv4.setEnabled(false);
        getContentPane().add(iv4, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 170, 50, 40));

        iv5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/iconVehiculo.png"))); // NOI18N
        iv5.setEnabled(false);
        getContentPane().add(iv5, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 340, 50, 40));

        iv6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/iconVehiculo.png"))); // NOI18N
        iv6.setEnabled(false);
        getContentPane().add(iv6, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 340, 50, 40));

        iv7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/iconVehiculo.png"))); // NOI18N
        iv7.setEnabled(false);
        getContentPane().add(iv7, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 340, 50, 40));

        iv1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/iconVehiculo.png"))); // NOI18N
        iv1.setEnabled(false);
        getContentPane().add(iv1, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 170, 50, 40));

        io1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/iconOperario.png"))); // NOI18N
        io1.setEnabled(false);
        getContentPane().add(io1, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 230, 50, 50));

        io2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/iconOperario.png"))); // NOI18N
        io2.setEnabled(false);
        getContentPane().add(io2, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 230, 50, 50));

        io3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/iconOperario.png"))); // NOI18N
        io3.setEnabled(false);
        getContentPane().add(io3, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 230, 50, 50));

        io4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/iconOperario.png"))); // NOI18N
        io4.setEnabled(false);
        getContentPane().add(io4, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 230, 50, 50));

        io5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/iconOperario.png"))); // NOI18N
        io5.setEnabled(false);
        getContentPane().add(io5, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 400, 50, 50));

        io6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/iconOperario.png"))); // NOI18N
        io6.setEnabled(false);
        getContentPane().add(io6, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 400, 50, 50));

        io7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/iconOperario.png"))); // NOI18N
        io7.setEnabled(false);
        getContentPane().add(io7, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 400, 50, 50));
        getContentPane().add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 30, 80, -1));

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icon.png"))); // NOI18N
        getContentPane().add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 30, 80, 40));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //Esta es la acción del botón start/stop. Cuando se pulsa por primera vez, se crean surtidores, vehiculos y operarios.
    //Tras la primera acción, este botón llama a los métodos para parar y reanudar la ejecución.
    private void startButtomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtomActionPerformed
        if (empezado == false) {
            try {
                crear();
                empezado = true;
                this.startButtom.setText("PARAR");
                this.startButtom.setForeground(Color.orange);
            } catch (InterruptedException ex) {
                Logger.getLogger(MainInterfaz.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            switch (parado) {
                case 0:
                    parada.cerrar_global();
                    this.startButtom.setText("REANUDAR");
                    this.startButtom.setForeground(Color.green);
                    parado = 1;
                    break;
                case 1:
                    parada.abrir_global();
                    this.startButtom.setText("PARAR");
                    this.startButtom.setForeground(Color.orange);
                    parado = 0;
                    break;
            }
        }
    }//GEN-LAST:event_startButtomActionPerformed
    //Cuando se cierra la centana de ejecución del progrma, si existía un archivo log, éste es eliminado
    private void formWindowClosed(java.awt.event.WindowEvent evt) {
        String directorio = System.getProperty("user.dir");
        File archivo = new File(directorio + "/evolucionGasolinera.txt");
        if (archivo.exists()) {
            archivo.delete();
        }
        System.exit(0);
    }

    //Método que recoge el icono para su uso en la ventana
    @Override
    public Image getIconImage() {
        Image icon = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("imagenes/icon.png"));
        return icon;
    }

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainInterfaz.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainInterfaz.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainInterfaz.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainInterfaz.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new MainInterfaz().setVisible(true);

                } catch (RemoteException ex) {
                    Logger.getLogger(MainInterfaz.class
                            .getName()).log(Level.SEVERE, null, ex);

                } catch (MalformedURLException ex) {
                    Logger.getLogger(MainInterfaz.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel io1;
    private javax.swing.JLabel io2;
    private javax.swing.JLabel io3;
    private javax.swing.JLabel io4;
    private javax.swing.JLabel io5;
    private javax.swing.JLabel io6;
    private javax.swing.JLabel io7;
    private javax.swing.JLabel io8;
    private javax.swing.JLabel iv1;
    private javax.swing.JLabel iv2;
    private javax.swing.JLabel iv3;
    private javax.swing.JLabel iv4;
    private javax.swing.JLabel iv5;
    private javax.swing.JLabel iv6;
    private javax.swing.JLabel iv7;
    private javax.swing.JLabel iv8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTextField jTextFieldCochesEsperando;
    private javax.swing.JTextField op1;
    private javax.swing.JTextField op2;
    private javax.swing.JTextField op3;
    private javax.swing.JTextField op4;
    private javax.swing.JTextField op5;
    private javax.swing.JTextField op6;
    private javax.swing.JTextField op7;
    private javax.swing.JTextField op8;
    private javax.swing.JButton startButtom;
    private javax.swing.JTextField su1;
    private javax.swing.JTextField su2;
    private javax.swing.JTextField su3;
    private javax.swing.JTextField su4;
    private javax.swing.JTextField su5;
    private javax.swing.JTextField su6;
    private javax.swing.JTextField su7;
    private javax.swing.JTextField su8;
    // End of variables declaration//GEN-END:variables
}
