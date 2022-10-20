package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import logger.Logger;

/**
 * Clase que representa un Cliente maestro
 */
public class Cliente {
    /**
     * Número de puerto a donde se va a realizar la conexion al servidor
     */
    public final static int PUERTO = 3400;

    /**
     * IP a donde se va a realizar la conexion al servidor
     */
    public final static String SERVIDOR = "localhost";

    public static void main(String[] args) throws IOException {
        // Crea un flujo para leer lo que escribe el cliente por el teclado
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        // Solicita la cantidad de clientes concurrentes para hacer peticiones al servidor
        int clientesConcurrentes = -1;
        while(clientesConcurrentes < 1) {
            System.out.println("Escriba la cantidad de clientes concurrentes > 0:");
            try {
                clientesConcurrentes = Integer.parseInt(stdIn.readLine());
                if(clientesConcurrentes < 1)
                    throw new NumberFormatException();
            } catch (NumberFormatException e) {
                System.err.println("Escriba un entero mayor a cero");
            }
        }

        // Solicita el id del archivo a recibir por el servidor
        long idArchivo = -1;
        while(idArchivo > 1 || idArchivo < 0) {
            mensajeSeleccionArchivo();
            try {
                idArchivo = Long.parseLong(stdIn.readLine());
                if(idArchivo > 1 || idArchivo < 0)
                    throw new NumberFormatException();
            } catch (NumberFormatException e) {
                System.err.println("Escriba un id de archivo de las opciones");
            }
        }

        // Crea el logger para la prueba cliente
        Logger logger = new Logger(false);
        String archivoRecibir = (idArchivo == 0) ? "100MB" : "250MB";
        logger.log("Archivo a transferir: " + archivoRecibir + " id " + idArchivo);

        // Crea los clientes concurrentes
        for (int i = 0; i < clientesConcurrentes; i++) {
            new ThreadCliente(i, clientesConcurrentes, idArchivo, logger).start();
        }

        // Se cierra el flujo de entrada por teclado
        stdIn.close();
    }

    /**
     * Imprime el mensaje en consola con las opciones de archivo a descargar.
     */
    public static void mensajeSeleccionArchivo() {
        System.out.println("Archivos");
        System.out.println("0. 100MB");
        System.out.println("1. 250MB");
        System.out.println();
        System.out.println("Escriba el id del archivo a descargar: ");
    }
}