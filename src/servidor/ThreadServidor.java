package servidor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import logger.Logger;

/**
 * Clase que representa un servidor UDP delegado para cada cliente
 */
public class ThreadServidor extends Thread {

    /**
     * Socket del servidor
     */
    private DatagramSocket dsocket;
    
    /**
     * Dirección IP del cliente
     */
    private InetAddress ipCliente;

    /**
     * Puerto del cliente
     */
    private int puertoCliente;

    /**
     * Archivo a transferir
     */
    private String nomArchivo;

    /**
     * Identificador del servidor TCP delegado
     */
    private int id;

    /**
     * Logger para almacenar el log del servidor.
     */
    private Logger logger;

    /**
     * Crea un nuevo servidor UDP delegado e inicializa sus atributos.
     * @param pSocket Socket para transferencia de archivo. pSocket != null.
     * @param pIdrchivo Id del archivo que se debe transferir. pIdrchivo == 0|1.
     * @param pIpCliente Dirección de red del cliente. pIpCliente != null.
     * @param pPuertoCliente Puerto de conexion con el cliente. pPuertoCliente > 0.
     * @param pId Identificador del servidor delegado. pId >= 0.
     * @param pLogger Objeto para registrar acciones y estado del servidor delegado. pLogger != null.
     */
    public ThreadServidor(DatagramSocket pSocket, long pIdArchivo, InetAddress pIpCliente, int pPuertoCliente, int pId, Logger pLogger) {
        this.dsocket = pSocket;
        this.nomArchivo = (pIdArchivo == 0) ? Servidor.ARCHIVO_0 : Servidor.ARCHIVO_1;
        this.ipCliente = pIpCliente;
        this.puertoCliente = pPuertoCliente;
        this.id = pId;
        this.logger = pLogger;
    }

    /**
     * Realiza la transferencia de archivo por medio de UCP.
     */
    @Override
    public void run() {
        logger.log("Servidor delegado " + (id+1) + " conectado");
        try {
            procesar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Se encarga de transferir un archivo por medio del protocolo UDP.
     * Servidor - Cliente
     * id archivo <-
     * tamanio archivo ->
     * transferencia archivo ->
     * @throws IOException si existe algun error en los flujos de entrada o salida.
     */
    public void procesar() throws IOException {

        // Crear el file handle para leer el archivo solicitado
        File archivo = new File(nomArchivo);
        FileInputStream lectorArchivo = new FileInputStream(archivo);
            
        // Envía al cliente el tamanio del archivo a transferir
        long size  = archivo.length();
        ByteBuffer buf = ByteBuffer.allocate(Long.BYTES);
        buf.putLong(size);
        DatagramPacket respuesta = new DatagramPacket(buf.array(), buf.array().length, ipCliente, puertoCliente);
        dsocket.send(respuesta);

        // Crea el tamanio del payload de cada paquete a transferir 32KB
        byte[] buffer = new byte[32*1024];

        logger.log("Servidor delegado " + (id+1) + " preparado para enviar " + nomArchivo + " (" + (size/1000) + "KB) a Cliente " + ipCliente + ":" + puertoCliente);
            
        // Envia el archivo por trozos
        long enviados = 0;
        int bytes = 0;
        long tInicio = System.currentTimeMillis();
        while ((bytes=lectorArchivo.read(buffer))!=-1){
            respuesta = new DatagramPacket(buffer, bytes, ipCliente, puertoCliente);
            dsocket.send(respuesta); // Envia por la red
            enviados += bytes;
            // Espera por sobrecarga
            if (enviados >= 400*1024) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                enviados = 0;
            }
        }
        long tTot = System.currentTimeMillis() - tInicio;

        logger.log("Servidor delegado " + (id+1) + " tiempo total de transferencia " + tTot + "ms a Cliente " + ipCliente + ":" + puertoCliente);

        lectorArchivo.close();
    }
}
