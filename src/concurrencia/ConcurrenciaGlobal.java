/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrencia;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Adri
 */
public class ConcurrenciaGlobal {

    private boolean cerrado = false;
    private Lock lock = new ReentrantLock();
    private Condition parar = lock.newCondition();

    public void abrir_global() {
        try {
            lock.lock();
            cerrado = false;
            parar.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void cerrar_global() {
        try {
            lock.lock();
            cerrado = true;
        } finally {
            lock.unlock();
        }
    }

    public void mirar_global() {
        try {
            lock.lock();
            while (cerrado) {
                try {
                    parar.await();
                } catch (InterruptedException ie) {
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
