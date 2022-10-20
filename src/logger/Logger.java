package logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    /**
     * Directorio de los logs del cliente
     */
    private final static String DIR_LOG_CLIENTE = "Logs" + File.separator + "cliente";
    /**
     * Directorio de los logs del servidor
     */
    private final static String DIR_LOG_SERVIDOR = "Logs" + File.separator + "servidor";

    /**
     * Nombre del archivo log en concreto.
     */
    private String nomLog;

    /**
     * Crea un nuevo archivo de log con la fecha y hora actual.
     */
    public Logger(boolean servidor) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        LocalDateTime ahora = LocalDateTime.now();

        String directorio = (servidor) ? DIR_LOG_SERVIDOR : DIR_LOG_CLIENTE;
        this.nomLog = directorio + File.separator + dtf.format(ahora)+".txt";
        new File(this.nomLog);
    }

    /**
     * Se encarga de escribir en el archivo log de la clase
     * @param mensaje Mensaje a escribir.
     */
    public synchronized void log(String mensaje) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ahora = LocalDateTime.now();
        String strAhora = dtf.format(ahora);

        try {
            FileWriter escritorLog = new FileWriter(nomLog, true);
            escritorLog.append(strAhora + " - " + mensaje + "\r\n");
            escritorLog.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
