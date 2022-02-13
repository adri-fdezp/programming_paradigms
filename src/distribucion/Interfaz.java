/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distribucion;

import java.util.ArrayList;
import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 *
 * @author Adri
 */
public interface Interfaz extends Remote{
    
    ArrayList<String> getVehiculosEntrada() throws RemoteException;
    ArrayList<String> getVehiculosEnSurtidor() throws RemoteException;
    ArrayList<String> getOperariosEnSurtidor() throws RemoteException;
   
}
