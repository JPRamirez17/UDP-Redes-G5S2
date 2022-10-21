# UDP-Redes-G5S2

Gabriela García - 201912531

Juan Pablo Ramirez - 201921728

## Prerrequisitos
- Installar Java JRE/JDK
- Usar servidor UbuntuServer 20.04
- Usar cliente en cualquier OS

## Servidor (UbuntuServer 20.04)
### Generar archivos de prueba para enviar
1. Ir al directorio ```ArchivosEnviar/```
2. Ejecutar comando ```sudo bash generarArchivos.sh```
3. Volver al directorio raíz ```cd ..```


### Ejecutar servidor
1. Ejecutar comandos ```javac -cp src/ src/servidor/Servidor.java``` y ```java -cp src/ src/servidor/Servidor.java```
3. Para interrumpir escribir comando ```Ctrl+C```

- **Resultados en Logs/servidor/**

## Cliente
### Cambiar IP servidor
1. Ejecutar comando ```sudo nano src/cliente/Cliente.java```
2. Cambiar la variable global ```SERVIDOR``` en la línea 21 con la IP del servidor a conectarse (e.g. ```"192.168.0.1"```).
3. Guardar y salir ```Ctrl+X``` y ```Enter```

### Ejecutar clientes
1. Ejecutar comandos ```javac -cp src/ src/cliente/Cliente.java``` y ```java -cp src/ src/cliente/Cliente.java```
3. Ingresar el número de clientes concurrentes
3. Ingresar id del archivo a recibir

- **Resultados en ArchivosRecibidos/ y Logs/cliente/**

## Si falla al correr el Servidor o Cliente por la versión de Java
1. Ejecutar comandos ```javac -d ./bin -cp src/ src/servidor/*.java```, ```javac -d ./bin -cp src/ src/cliente/*.java``` y ```javac -d ./bin -cp src/ src/logger/*.java```
2. Ejecutar comando ```java -cp bin/ servidor.Servidor``` o ```java -cp bin/ cliente.Cliente```
