/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrencia;

/**
 *
 * @author Adri
 */
public class Vehiculo extends Thread {

    private String idVehiculo;
    private Log log;
    private Gestor gestor;
    private int tiempoLlegada;

    public Vehiculo(int ID, Log log, Gestor gestor, int tiempoLlegada) {
        this.idVehiculo = "Vehículo" + String.valueOf(ID);
        this.log = log;
        this.gestor = gestor;
        this.tiempoLlegada = tiempoLlegada;
    }

    public String getIdVehiculo() {
        return idVehiculo;
    }

    /*
    La rutina de los vehiculos consiste en llegar a la gasolinera, esperando en un
    Array a que puedan ser introducidos en un surtidor, después de que se les atienda,
    salen del surtidor y son eliminados.
     */
    @Override
    public void run() {
        try {
            sleep(tiempoLlegada);
            gestor.cola_Gasolinera(idVehiculo, tiempoLlegada);
            gestor.entrar_a_surtidor(idVehiculo);
        } catch (Exception e) {
            String textoLog = "Error con el " + this.getIdVehiculo() + ".";
            this.log.escribirEnLog(textoLog, "ERROR");
        }
    }
}
