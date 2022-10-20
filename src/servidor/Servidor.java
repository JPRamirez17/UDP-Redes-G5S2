package servidor;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import logger.Logger;

/**
 * Clase que representa el servidor principal UDP
 */
public class Servidor {

    /**
     * Archivos disponibles de 100MB y 150MB para transferencia
     */
    public final static String ARCHIVO_0 = "ArchivosEnviar" + File.separator + "100MB.txt";
    public final static String ARCHIVO_1 = "ArchivosEnviar" + File.separator + "250MB.txt";

    /**
     * Numero de puerto donde se van a escuchar peticiones
     */
    public final static int PUERTO = 3400;

    public static void main(String[] args) throws IOException{
        DatagramSocket ds = null;
        boolean continuar = true;
        int numeroThreads = 0; // Multithread

        System.out.println("Servidor UDP ...");

        // Crear el socket UDP en el lado del servidor
        try {
            ds = new DatagramSocket(PUERTO);
            ds.setSoTimeout(0);
        } catch (IOException e) {
            System.err.println("No se pudo crear el socket en el puerto: " + PUERTO);
            System.exit(-1);
        }
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        String strAntes = dtf.format(LocalDateTime.now());
        Logger logger = new Logger(true);
        while (continuar) {
            // Queda bloqueado esperando que llegue un cliente
            DatagramPacket conexion = new DatagramPacket(new byte[Long.BYTES], Long.BYTES);
            ds.receive(conexion);

            String strAhora = dtf.format(LocalDateTime.now());
            if(!strAhora.equals(strAntes)) {
                logger = new Logger(true);
                strAntes = strAhora;
            }

            // Crea el servidor delegado con el id del archivo a enviar, la dirección del socket cliente y el id del thread
            ByteBuffer buf = ByteBuffer.allocate(Long.BYTES);
            buf.put(conexion.getData());
            buf.flip();
            ThreadServidor thread = new ThreadServidor(ds, buf.getLong(), conexion.getAddress(), conexion.getPort(), numeroThreads, logger);
            numeroThreads ++;

            // Start
            thread.start();
        }
        ds.close();
    }

}
