/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrencia;

import java.util.ArrayList;

/**
 *
 * @author Adri
 */
public class Surtidor {

    private int id;
    private boolean ocupado = false; //Proporciona la información para saber si se debe elegir a la hora de escoger el surtidor 
                                     //(Si esta ocupado, no debe elegirse)
    private ArrayList<String> espacioVehiculo; //Array donde se guarda el vehiculo que esta siendo atendido
    private ArrayList<String> operarioSurtidor; //Array donde se guarda el operario que atiende el surtidor
    private Gestor gestor;

    public Surtidor(int id, Gestor gestor) {
        this.id = id;
        this.ocupado = ocupado;
        this.espacioVehiculo = new ArrayList<>();
        this.operarioSurtidor = new ArrayList<>();
        this.gestor = gestor;
    }

    //Método que introducuce al vehiculo en el surtidor y lo bloquea
    public void entrar(String idVehiculo) throws InterruptedException {
        ocupado = true;
        espacioVehiculo.add(idVehiculo);
    }

    //Método que da salida al vehículo y desbloquea el surtidor
    public void salir() throws InterruptedException {
        espacioVehiculo.remove(0);
        operarioSurtidor.remove(0);
        ocupado = false;
    }

    public int getId() {
        return id;
    }

    public boolean isOcupado() {
        return ocupado;
    }

    public ArrayList<String> getEspacioVehiculo() {
        return espacioVehiculo;
    }

    public ArrayList<String> getOperarioSurtidor() {
        return operarioSurtidor;
    }
}
