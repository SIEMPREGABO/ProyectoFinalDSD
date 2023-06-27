//////////////////////////////////////////////////
//  PROYECTO FINAL - SISTEMAS DISTRIBUIDOS      //
//                                              //
//  DIAZ MALDONADO JESUS RENATO         - 4CM13 //
//  MIRON AREVALO GABRIEL OMAR          - 4CM14 //
//  TORRES CARRILLO JOSEHF MIGUEL ANGEL - 4CM11 //
//////////////////////////////////////////////////

package com.mycompany.servidorprincipal;

import java.io.IOException;

public class App {

    public static void main(String[] args) throws IOException, InterruptedException {
        int currentServerPort = 3000;
        if (args.length == 1) {
            currentServerPort = Integer.parseInt(args[0]);
        }
        App application = new App();

        ServidorPrincipal webServer = new ServidorPrincipal(currentServerPort);
        webServer.startServer();

        System.out.println("Servidor escuchando en el puerto: " + currentServerPort);
    }
}