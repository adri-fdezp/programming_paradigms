/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distribucion;

import concurrencia.MainInterfaz;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.rmi.RemoteException;

/**
 *
 * @author Adri
 */
public class ClienteInformacion extends UnicastRemoteObject implements Interfaz{
    
    private MainInterfaz main;
    private ArrayList<String> vehiculosEntrada;
    private ArrayList<String> vehiculosEnSurtidor;
    private ArrayList<String> operariosEnSurtidor;

    public ClienteInformacion(MainInterfaz main) throws RemoteException{
        this.main = main;
    }
    
    @Override
    public ArrayList<String> getVehiculosEntrada() throws RemoteException{
        this.vehiculosEntrada = this.main.getArrayVehiculosEntrada();
        return vehiculosEntrada;
    }
    
    @Override
    public ArrayList<String> getVehiculosEnSurtidor() throws RemoteException{
        this.vehiculosEnSurtidor = this.main.getArrayVehiculosEnSurtidores();
        return vehiculosEnSurtidor;
    }
    
    @Override
    public ArrayList<String> getOperariosEnSurtidor() throws RemoteException{
        this.operariosEnSurtidor = this.main.getArrayOperariosEnSurtidores();
        return operariosEnSurtidor;
    }
}
