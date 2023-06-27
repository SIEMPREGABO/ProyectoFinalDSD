//////////////////////////////////////////////////
//  PROYECTO FINAL - SISTEMAS DISTRIBUIDOS      //
//                                              //
//  DIAZ MALDONADO JESUS RENATO         - 4CM13 //
//  MIRON AREVALO GABRIEL OMAR          - 4CM14 //
//  TORRES CARRILLO JOSEHF MIGUEL ANGEL - 4CM11 //
//////////////////////////////////////////////////

package com.mycompany.servidorprincipal;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Executors;

import java.net.InetSocketAddress;

import java.lang.Math;

public class ServidorPrincipal {

    private static final String STATUS_ENDPOINT = "/status";
    private static final String HOME_PAGE_ENDPOINT = "/";
    private static final String HOME_PAGE_UI_ASSETS_BASE_DIR = "/ui_assets/";
    private static final String ENDPOINT_PROCESS = "/procesar_datos";

    private final int port;
    private HttpServer server;
    private int localHost = 0;

    public ServidorPrincipal(int port) {
        this.port = port;
    }

    public void startServer() {
        try {
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        HttpContext statusContext = server.createContext(STATUS_ENDPOINT);
        HttpContext taskContext = server.createContext(ENDPOINT_PROCESS);
        HttpContext homePageContext = server.createContext(HOME_PAGE_ENDPOINT);
        statusContext.setHandler(this::handleStatusCheckRequest);
        taskContext.setHandler(this::handleTaskRequest);
        homePageContext.setHandler(this::handleRequestForAsset);

        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();
    }

    private void handleRequestForAsset(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }

        byte[] response;

        String asset = exchange.getRequestURI().getPath();

        if (asset.equals(HOME_PAGE_ENDPOINT)) {
            response = readUiAsset(HOME_PAGE_UI_ASSETS_BASE_DIR + "index.html");
        } else {
            response = readUiAsset(asset);
        }
        addContentType(asset, exchange);
        sendResponse(response, exchange);
    }

    private byte[] readUiAsset(String asset) throws IOException {
        InputStream assetStream = getClass().getResourceAsStream(asset);

        if (assetStream == null) {
            return new byte[]{};
        }
        return assetStream.readAllBytes();
    }

    private static void addContentType(String asset, HttpExchange exchange) {

        String contentType = "text/html";
        if (asset.endsWith("js")) {
            contentType = "text/javascript";
        } else if (asset.endsWith("css")) {
            contentType = "text/css";
        } else if (asset.endsWith("png")) {
            contentType = "image/png";
        }
        exchange.getResponseHeaders().add("Content-Type", contentType);
    }

    private void handleTaskRequest(HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
            exchange.close();
            return;
        }

        //Variables compartidas
//          localHost = 1;
//          String[] directoriosGET = {"http://localhost:8082/status", "http://localhost:8083/status", "http://localhost:8084/status"};
//          String[] directoriosPOST = {"http://localhost:8082/tarea", "http://localhost:8083/tarea", "http://localhost:8084/tarea"};
//        
      String[] directoriosGET = {"http://34.125.28.136:80/status", "http://34.125.104.201:80/status", "http://34.125.92.153:80/status"};
      String[] directoriosPOST = {"http://34.125.28.136:80/tarea", "http://34.125.104.201:80/tarea", "http://34.125.92.153:80/tarea"};
        
        String[] paramPalabras = { " ", " ", " " }; 
        
        byte[] requestBytes = exchange.getRequestBody().readAllBytes();
        String parametro = new String(requestBytes);
        
        String rango8082 = " ", rango8083 = " ", rango8084 = " ";
        String r1 = "", r2 = "", r3 = "";
        String r4 = "", r5 = "", r6 = "";
        String response = "OK";

        int bandera8082 = 0, bandera8083 = 0, bandera8084 = 0;

        int contador = 0;
        
        //Enviar peticion  GET
//        Aggregator aggregatorGET = new Aggregator( "GET" );
//        List< String> results = aggregatorGET.sendTaskToWorkers( Arrays.asList( directoriosGET ), Arrays.asList( paramPalabras ) );
//
//        for (String result : results) {
//            if (contador == 0) r1 = result.trim();
//            if (contador == 1) r2 = result.trim();
//            if (contador == 2) r3 = result.trim();
//            contador++;
//        }
//
//        response = r1 + "\n" + r2 + "\n" + r3;
//        System.out.println( "\n" + response + "\n" );
//
//        //Metodo entre envio de peticiones
//        
//        bandera8082 = validacionServer( r1, 1 );
//        bandera8083 = validacionServer( r2, 2 );
//        bandera8084 = validacionServer( r3, 3 );

        rango8082 = parametro + " 0/15";
        rango8083 = parametro + " 15/15"; 
        rango8084 = parametro + " 30/16";

//        if ( bandera8082 == 1 ) {
//            
//            rango8083 = parametro + " 0/30";
//            rango8084 = parametro + " 30/16"; 
//        }
//
//        if ( bandera8083 == 1 ) {
//            
//            rango8082 = parametro + " 0/15";
//            rango8084 = parametro + " 15/31"; 
//        }
//
//        if ( bandera8084 == 1 ) {
//            
//            rango8082 = parametro + " 0/15";
//            rango8083 = parametro + " 15/31";
//        }
//
//        if ( bandera8082 == 1 && bandera8083 == 1 ) rango8084 = parametro + " 0/46";
//        if ( bandera8083 == 1 && bandera8084 == 1 ) rango8082 = parametro + " 0/46";
//        if ( bandera8082 == 1 && bandera8084 == 1 ) rango8083 = parametro + " 0/46";

        paramPalabras[0] = rango8082;
        paramPalabras[1] = rango8083;
        paramPalabras[2] = rango8084;

        // for ( String result : paramPalabras )
        //     System.out.println( result );

        try { Thread.sleep(1000); }
        catch (InterruptedException e) { e.printStackTrace(); }
        
        //Enviar peticion POST
        Aggregator aggregatorPOST = new Aggregator( "POST" );
        List< String > results2  = aggregatorPOST.sendTaskToWorkers( Arrays.asList( directoriosPOST ), Arrays.asList( paramPalabras ) );

        contador = 0;

        for (String result : results2) {
            if (contador == 0) r4 = result.trim();
            if (contador == 1) r5 = result.trim();
            if (contador == 2) r6 = result.trim();
            contador++;
        }

        response = r4 + "\n" + r5 + "\n" + r6;
        
        //System.out.println( "\n\n\n" + response + "\n\n\n" );

        String[] comprobacionR4 = r4.split( " " );
        String[] comprobacionR5 = r5.split( " " );
        String[] comprobacionR6 = r6.split( " " );

        if ( comprobacionR4[0].equals( "Error" ) ) response = r5 + "\n" + r6;
        if ( comprobacionR5[0].equals( "Error" ) ) response = r4 + "\n" + r6;
        if ( comprobacionR6[0].equals( "Error" ) ) response = r4 + "\n" + r5;

        if ( comprobacionR4[0].equals( "Error" ) && comprobacionR5[0].equals( "Error" ) ) response = r6;
        if ( comprobacionR5[0].equals( "Error" ) && comprobacionR6[0].equals( "Error" ) ) response = r4;
        if ( comprobacionR4[0].equals( "Error" ) && comprobacionR6[0].equals( "Error" ) ) response = r5;



        response = obtenerTFITF( response, parametro );
        
        System.out.println( "\n" + response + "\n" );
        sendResponse(response.getBytes(), exchange);
    }

    public int validacionServer ( String cadena, int numServidor ) {

        String[] validacion = cadena.split( " " );

        if ( localHost == 1 ) {
            
            if ( numServidor == 1 )
                if ( validacion[ 2 ].equals( "8082" ) ) return 0;
                else return 1;

            if ( numServidor == 2 )
                if ( validacion[ 2 ].equals( "8083" ) ) return 0;
                else return 1;

            if ( numServidor == 3 )
                if ( validacion[ 2 ].equals( "8084" ) ) return 0;
                else return 1;
        }
        else {

            if ( validacion[ 2 ].equals( "80" ) ) return 0;
            else return 1;
        }

        return 5;
    }

    public String obtenerTFITF ( String response, String parametro ) {

        String[] palabrasBuscar = parametro.split(" ");
        float[] coincidenciasPB = new float[palabrasBuscar.length];
        String[] libroTF = response.split("\n");
        String[] libroITF = new String[ libroTF.length ];
        String[] libroTFITF = new String[ libroTF.length ];
        
        String[] datosLibro;
        float[] palabraITF = new float[ palabrasBuscar.length ];

        Map< String, Float > map = new HashMap<>();

        int aux = 0;

        //Ciclo para calcular en cuantos libros se encontro las palabras
        for (String libro : libroTF) {

            datosLibro = libro.split("\\|");

            for (int i = 0; i < palabrasBuscar.length; i++)
                if ( Float.parseFloat(datosLibro[i + 1]) > 0 )
                    coincidenciasPB[i]++;
        }

        //Ciclo para calcular el ITF de las palabras de los libros
        for ( String libro : libroTF ) {

            datosLibro = libro.split("\\|");
            libroITF[ aux ] = datosLibro[0];

            for ( int i = 0; i < palabrasBuscar.length; i++ ) {
                
                palabraITF[i] = Float.parseFloat( datosLibro[i + 1] );
                palabraITF[i] = (float)Math.log10( 46 / coincidenciasPB[i] );
                
                libroITF[ aux ] = libroITF[ aux ] + "|" + palabraITF[i];
            }
            
            aux++;             
        }

        // System.out.println("LIBROTF\n");
        // for ( String libro : libroTF ){
        //     System.out.println(libro);
        // }

        // System.out.println("LIBROITF\n");
        // for ( String libro : libroITF ){
        //     System.out.println(libro);
        // }

        aux = 0;
        String[] datosLibroTF;
        String[] datosLibroITF;
        
        //Ciclo para calcular el PF final del libro
        //TF+ITF + TF*ITF + ...
        for ( String libro : libroTF ) {

            float tFITF = 0;

            datosLibroTF = libro.split( "\\|" );
            datosLibroITF = libroITF[ aux ].split( "\\|" );

            for ( int i = 0; i < palabrasBuscar.length; i++ )
                tFITF = tFITF + ( Float.parseFloat(datosLibroTF[i+1])*Float.parseFloat(datosLibroITF[i+1]) );

            map.put( datosLibroTF[0], tFITF );

            aux++;
        }

        List< Map.Entry< String, Float > > list = new ArrayList<>(map.entrySet());
        Collections.sort(list, (Map.Entry< String, Float > o1, Map.Entry< String, Float > o2) -> o2.getValue().compareTo(o1.getValue()));

        response = "";

        for (Map.Entry< String, Float > entry : list){
            response += entry.getKey() + "\n";
            //System.out.println( entry.getKey() + " --- " + entry.getValue() );
        }

        return response;
    }

    private void handleStatusCheckRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }

        String responseMessage = "El servidor está vivo\n";
        sendResponse(responseMessage.getBytes(), exchange);
    }

    private void sendResponse(byte[] responseBytes, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, responseBytes.length);

        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(responseBytes);
        outputStream.flush();
        outputStream.close();
        exchange.close();
    }
}
