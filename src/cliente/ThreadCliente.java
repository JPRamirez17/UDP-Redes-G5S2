package cliente;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

import logger.Logger;

/**
 * Clase que representa un cliente especifico UDP 
 */
public class ThreadCliente extends Thread {
    
    /**
     * Directorio de los archivos recibidos
     */
    private final static String DIR_RECIBIDOS = "ArchivosRecibidos";
    
    /**
     * Extension de los archivos recibidos
     */
    private final static String EXT_ARCHIVO = ".txt";

    /**
     * Identificador del cliente
     */
    private int id;
    
    /**
     * Cantidad de clientes concurrentes
     */
    private int cantConexiones;

    /**
     * Identificador del archivo a solicitar al servidor.
     */
    private long idArchivo;

    /**
     * Logger para almacenar el log del usuario.
     */
    private Logger logger;

    /**
     * Crea un nuevo cliente UDP delegado e inicializa sus atributos.
     * @param pId Identificador del cliente delegado. pId>=0.
     * @param pCantConexiones Cantidad de clientes concurrentes.
     * @param pIdArchivo Identificador del archivo.
     * @param pLogger Objeto para registrar acciones y estado del cliente delegado. pLogger != null.
     */
    public ThreadCliente(int pId, int pCantConexiones, long pIdArchivo, Logger pLogger) {
        this.id = pId;
        this.idArchivo = pIdArchivo;
        this.cantConexiones = pCantConexiones;
        this.logger = pLogger;
    }
    
    /**
     * CLiente delegado se encarga de crear un socket de datagramas en un puerto disponible de la máquina local.
     * Recibe la transferencia de archivo por medio de UDP y se encarga de escribirlo localmente.
     * Cierra el socket de datagramas.
     */
    @Override
    public void run() {
        try {
            // Crea el socket en el lado cliente
            DatagramSocket dsocket = new DatagramSocket();
            // Timeout para esperar la recepción de un paquete 1s
            dsocket.setSoTimeout(1000);
            // Maximo buffer de recepción para archivos grandes 10MiB -> 6MiB (máx OS)
            dsocket.setReceiveBufferSize(10*1024*1024);

            // Nombre del archivo a escribir cuando se reciban datos del servidor
            String nomArchivo = DIR_RECIBIDOS + File.separator + (id+1) + "-Prueba-" + cantConexiones + EXT_ARCHIVO;
            //Crear el file handle donde se va guardar el archivo recibido
            new File(nomArchivo);
            FileOutputStream escritorArchivo = new FileOutputStream(nomArchivo);
            
            // Se ejecuta el protocolo UDP en el lado cliente
            procesar(dsocket, escritorArchivo);

            // Se cierra el socket
            dsocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Se encarga de recibir un archivo por medio del protocolo UDP.
     * Servidor - Cliente
     * id archivo <-
     * tamanio archivo ->
     * transferencia archivo ->
     * Cliente se da cuenta de pérdida de paquetes si hay algún timeout.
     * @param dsocket Socket de datagramas usado por el cliente para la comunicación en red. dsocket!=null.
     * @param escritorArchivo Flujo de escritura para escribir el archivo recibido por el servidor. escritorArchivo!=null.
     * @throws IOException si existe algun error en los flujos de entrada o salida.
     */
    public void procesar(DatagramSocket dsocket, FileOutputStream escritorArchivo) throws IOException {
        InetAddress ipServidor = InetAddress.getByName(Cliente.SERVIDOR);

        //Envia el id del archivo que quiere descargar
        ByteBuffer buf = ByteBuffer.allocate(Long.BYTES);
        buf.putLong(idArchivo);
        DatagramPacket conexion = new DatagramPacket(buf.array(), buf.array().length, ipServidor, Cliente.PUERTO);
        dsocket.send(conexion);

        logger.log("Cliente " + (id+1) + " conectando a " + Cliente.SERVIDOR + ":" + Cliente.PUERTO);

        //Recibe el tamanio del archivo requerido
        DatagramPacket recepcion = new DatagramPacket(new byte[Long.BYTES], Long.BYTES);
        try {
            dsocket.receive(recepcion);
        } catch (SocketTimeoutException e) {
            logger.log("Cliente " + (id+1) + " paquete perdido no hubo contacto con " + Cliente.SERVIDOR + ":" + Cliente.PUERTO);
            return;
        }
        buf = ByteBuffer.allocate(Long.BYTES);
        buf.put(recepcion.getData());
        buf.flip();
        long size = buf.getLong();

        // Crea el tamanio del payload de cada paquete a recibir 32KiB
        byte[] buffer = new byte[32*1024];

        logger.log("Cliente " + (id+1) + " preparado para recibir " + (size/1000) + "KB");

        // Recibe el archivo por trozos
        int bytes = 0;
        boolean exito = true;
        long tInicio = System.currentTimeMillis();
        while (size > 0) {
            recepcion = new DatagramPacket(buffer, (int)Math.min(buffer.length, size));
            try {
                dsocket.receive(recepcion);
                bytes = recepcion.getData().length;
                escritorArchivo.write(buffer,0,bytes); // Escribe el archivo recibido localmente
                size -= bytes;
            } catch (SocketTimeoutException e) {
                exito = false; // Se perdieron paquetes
                break;
            }
        }
        long tTot = System.currentTimeMillis() - tInicio;
        logger.log("Cliente " + (id+1) + " tiempo total de transferencia " + tTot + "ms");

        // Verificar si archivo recibido es del mismo tamaño que el enviado
        logger.log("Cliente " + (id+1) + " transferencia completa - transferencia exitosa (sin perdida): " + exito);

        escritorArchivo.close();
    }
}
