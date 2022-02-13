/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrencia;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Log {

    private final String directorio = System.getProperty("user.dir");
    private final File log = new File(directorio + "/evolucionGasolinera.txt");
    private final Lock lockLog = new ReentrantLock();

    public Log() {

        if (!log.exists()) {
            this.crearLog(false);
        } else {
            this.crearLog(true);
        }
    }
    
    //Método que crea el archivo log, si existía uno anteriormente, lo reemplaza
    private void crearLog(boolean creado) {
        if (creado) {
            try {
                File archivoLog = new File(this.directorio + "/evolucionGasolinera.txt");
                archivoLog.delete();
                File nuevoArchivoLog = new File(this.directorio + "/evolucionGasolinera.txt");
                nuevoArchivoLog.createNewFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                File archivoLog = new File(this.directorio + "/evolucionGasolinera.txt");
                archivoLog.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //Método que escribe en el log dependiendo de la naturaleza del mensaje
    public synchronized void escribirEnLog(String texto, String tipo) {
        this.lockLog.lock();
        try {
            try {

                BufferedWriter escribir = new BufferedWriter(new FileWriter(directorio + "/evolucionGasolinera.txt", true));
                Timestamp tiempo = new Timestamp(System.currentTimeMillis());

                switch (tipo) {
                    case "INFO":
                        texto = tiempo + " " + texto;
                        break;
                    case "ERROR":
                        texto = tiempo + "  ERROR: " + texto;
                        break;
                }
                System.out.println(texto);
                escribir.append(texto + "\n");
                escribir.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            this.lockLog.unlock();
        }
    }
}
