/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrencia;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Adri
 */
public class Gestor {

    private MainInterfaz main;
    private ConcurrenciaGlobal parada;
    private Log log;

    //ArrayList donde se guradan los vehiculos
    private ArrayList<String> listaEntradaVehiculos; //Lista donde se guardan los vehículos tras ser creados
    private ArrayList<Surtidor> surtidoresParaAtender; //Lista para organizar los surtidores para su atención
    private ArrayList<Surtidor> surtidoresTerminados; //Lista para organizar los surtidores que ya han sido atendidos

    //Locks que controlan el funcionamiento de la gasolinera
    private Lock cerrojo = new ReentrantLock();
    private Condition lleno = cerrojo.newCondition();
    private Condition vacio = cerrojo.newCondition();

    //Contador de surtidores disponibles
    private int contadorSurtidores = 0;
    private int contadorVehiculos = 0;

    public Gestor(MainInterfaz main, ConcurrenciaGlobal parada, Log log) {
        this.main = main;
        this.parada = parada;
        this.log = log;
        this.listaEntradaVehiculos = new ArrayList<>();
        this.surtidoresParaAtender = new ArrayList<>();
        this.surtidoresTerminados = new ArrayList<>();
    }

    //Gestiona la llegada de vehículos a la gasolinera antes de entrar a los surtidores
    public void cola_Gasolinera(String idVehiculo, int tiempo) throws InterruptedException {
        try {
            this.parada.mirar_global();
            cerrojo.lock();
            listaEntradaVehiculos.add(idVehiculo); //Añade el id del vehiculo a la cola de espera de la gasolinera
            this.main.escribirEntradaVehiculos(); //Muestra por la interfaz la lista de coches de en espera

            String txtLog = "El " + idVehiculo + " ha llegado a la gasolinera en " + (double) tiempo / 1000 + " segundos.";
            this.log.escribirEnLog(txtLog, "INFO");
        } finally {
            cerrojo.unlock();
            this.parada.mirar_global();
        }
    }

    //Gestiona la entrada de vehículos en los surtidores
    public void entrar_a_surtidor(String idVehiculo) throws InterruptedException {
        try {
            this.parada.mirar_global();
            cerrojo.lock();
            while (contadorSurtidores == 8) { //Mientras que los surtidores estén llenos, no podrá entrar ningún vehículo
                lleno.await();
            }
            contadorSurtidores++; //
            Surtidor surtidorConVehiculo = elegirSurtidorVehiculo(); //Guarda el surtidor cada vez que un vehiculo entra
            surtidoresParaAtender.add(surtidorConVehiculo);  //Guarda el surtidor con vehiculo en un array para ordenar la futura atención del operario
            surtidorConVehiculo.entrar(listaEntradaVehiculos.get(0)); //Metodo que extrae el vehiculo de la lista de vehiculos en espera y lo inserta en el surtidor
            listaEntradaVehiculos.remove(0); //Elimina del array de coches de entrada al primero de la cola
            this.main.escribirEntradaVehiculos(); //Actualiza la cola de vehiculos de espera en la interfaz
            this.main.escribirSurtidores(surtidorConVehiculo.getEspacioVehiculo().get(0), surtidorConVehiculo.getId()); //Recoge el ID del vehiculo y surtidor para gestionar la interfaz 
            vacio.signal(); //Permite que los operarios que esperaban entrar a repostar los vehiculos entren a los surtidores con vehiculos

            String txtLog = "El " + idVehiculo + " entra en el surtidor " + surtidorConVehiculo.getId();
            this.log.escribirEnLog(txtLog, "INFO");
        } finally {
            cerrojo.unlock();
            this.parada.mirar_global();
        }
    }

    //Vincula al operario con el surtidor donde reposta el vehiculo
    public void repostar_Vehiculo(Operario op) throws InterruptedException {
        try {
            this.parada.mirar_global();
            cerrojo.lock();
            while (surtidoresParaAtender.isEmpty()) {
                vacio.await();
            }
            Surtidor surtidorElegido = surtidoresParaAtender.get(0);
            surtidorElegido.getOperarioSurtidor().add(op.getIdOperario()); //El operarario es vinculado con el surtidor
            surtidoresTerminados.add(surtidoresParaAtender.get(0)); //Se añade el surtidor a la lista de surtidores terminados para su futura gestión
            this.main.escribirOperarios(op.getIdOperario(), surtidoresParaAtender.get(0).getId()); //Recoge el ID del operario y surtidor para gestionar la interfaz
            surtidoresParaAtender.remove(0); //Se elimina el surtidor de la cola de surtidores a atender

            String txtLog = "El " + op.getIdOperario() + " está atendiendo el surtidor " + surtidorElegido.getId();
            this.log.escribirEnLog(txtLog, "INFO");
        } finally {
            cerrojo.unlock();
            this.parada.mirar_global();
        }
    }

    //Gestiona la salida de vehículos de los surtidores
    public void salir_del_surtidor(Operario op, int tiempo) throws InterruptedException {
        try {
            this.parada.mirar_global();
            cerrojo.lock();
            Surtidor surtFinalizado = seleccionarSurtidorTerminado(op); //Se elige el surtidor en el cual el operario ya ha finalizado
            String vehiculoFinalizado = surtFinalizado.getEspacioVehiculo().get(0); //Se guarda en variable el vehiculo del surtidor que ya ha finalizado
            surtFinalizado.salir(); //Se llama a la función del surtidor 'salir' que vacía el surtidor
            this.main.escribirOperarios("", surtFinalizado.getId()); //Se vacía el espacio de operario en la interfaz
            this.main.escribirSurtidores("", surtFinalizado.getId()); //Se vacía el espacio de vehiculo en la interfaz
            contadorSurtidores--; //Se resta 1 al contador que permite entrar a los vehiculos en surtidores vacíos
            lleno.signal(); //Se envía una señal al lock para que los vehiculos puedan entrar a los surtidores vacíos

            String txtLog = "El " + op.getIdOperario() + " ha terminado de atender el surtidor " + surtFinalizado.getId() + " tardando " + (double) tiempo / 1000 + " segundos";
            this.log.escribirEnLog(txtLog, "INFO");
            txtLog = "El " + vehiculoFinalizado + " ha salido del surtidor " + surtFinalizado.getId();
            this.log.escribirEnLog(txtLog, "INFO");

            contadorVehiculos++;
            if (this.main.getNumeroVehiculos() == contadorVehiculos) {
                txtLog = "No existen más vehículos para etender, el programa ha terminado.";
                this.log.escribirEnLog(txtLog, "INFO");
            }

        } finally {
            cerrojo.unlock();
            this.parada.mirar_global();
        }
    }
//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    /*
    MÉTODOS AUXILIARES
    Los métodos mostrados a continuación no atienden directamente a la idea de negocio,
    ayudan a los métodos principales para su correcta gestión.
     */
    //Método que devuelve el surtidor con numeración más baja (id) y que esté libre
    public Surtidor elegirSurtidorVehiculo() {
        Surtidor surtElegido = null;
        for (int i = 0; i < this.main.getNumeroSurtidores() + 1; i++) {
            if (!this.main.getArraySurtidores().get(i).isOcupado()) {
                surtElegido = this.main.getArraySurtidores().get(i);
                break;
            }
        }
        return surtElegido;
    }

    //Método que devuelve el surtidor que ha sido atendido con éxito por un opeario
    public Surtidor seleccionarSurtidorTerminado(Operario op) {
        Surtidor surtTerminado = null;
        for (int i = 0; i < surtidoresTerminados.size() + 1; i++) {
            if (surtidoresTerminados.get(i).getOperarioSurtidor().get(0).equals(op.getIdOperario())) {
                surtTerminado = surtidoresTerminados.get(i);
                surtidoresTerminados.remove(i);
                break;
            }
        }
        return surtTerminado;
    }

    // Las instancias de esta clase permiten crear números aleatorios si se especifica un rango
    public int obtenerAleatorio(int min, int max) {
        return (int) ((Math.random() * (max - min + 1)) + min);
    }

    public ArrayList<String> getListaEntradaVehiculos() {
        return listaEntradaVehiculos;
    }
}
