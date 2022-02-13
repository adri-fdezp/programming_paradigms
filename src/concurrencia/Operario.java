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
public class Operario extends Thread {

    private String idOperario;
    private Log log;
    private Gestor gestor;

    public Operario(int ID, Log log, Gestor gestor) {
        this.idOperario = "Operario" + String.valueOf(ID);
        this.log = log;
        this.gestor = gestor;
    }

    public String getIdOperario() {
        return idOperario;
    }

    @Override
    public void run() {
        int repostajes = 5;
        while (true) {
            try {

                if (repostajes == 0) { //Cuando un operario hace 5 respostajes descansa 5 segundos y después continua con su rutina
                    String txtLog = "El " + this.getIdOperario() + " está descansando tras hacer 5 repostajes.";
                    this.log.escribirEnLog(txtLog, "INFO");
                    sleep(5000);
                    repostajes = 5;
                } else {  //Rutina de los operarios
                    gestor.repostar_Vehiculo(this);
                    int tiempoAtencion = this.gestor.obtenerAleatorio(4000, 8000);
                    sleep(tiempoAtencion);
                    gestor.salir_del_surtidor(this, tiempoAtencion);
                    repostajes--;
                }
            } catch (Exception e) {
                String textoLog = "Error con el " + this.getIdOperario() + ".";
                this.log.escribirEnLog(textoLog, "ERROR");
            }
        }
    }
}
