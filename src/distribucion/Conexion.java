/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distribucion;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Adri
 */
public class Conexion extends Thread {

    private ClienteInterfaz cliente;

    public Conexion(ClienteInterfaz cliente) {
        this.cliente = cliente;
    }

    @Override
    public void run() {
        Interfaz remoto = null;
        while (true) {
            try {
                remoto = (Interfaz) Naming.lookup("//127.0.0.1/info");
            } catch (MalformedURLException | RemoteException | NotBoundException e1) {
            }
            if (remoto != null) {
                try {
                    sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    this.cliente.escribirEntradaVehiculos(remoto.getVehiculosEntrada());
                    this.cliente.escribirSurtidores(remoto.getVehiculosEnSurtidor());
                    this.cliente.escribirOperarios(remoto.getOperariosEnSurtidor());
                } catch (Exception e2) {
                    System.out.println("Error" + e2.getMessage());
                }
            }
        }
    }
}
