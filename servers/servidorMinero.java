//////////////////////////////////////////////////
//  PROYECTO FINAL - SISTEMAS DISTRIBUIDOS      //
//                                              //
//  DIAZ MALDONADO JESUS RENATO         - 4CM13 //
//  MIRON AREVALO GABRIEL OMAR          - 4CM14 //
//  TORRES CARRILLO JOSEHF MIGUEL ANGEL - 4CM11 //
//////////////////////////////////////////////////

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import java.math.BigInteger;

import java.net.InetSocketAddress;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.Random;

import java.lang.StringBuilder;


public class servidorMinero {

	private static final String ENDPOINT = "/tarea";
	private static final String STATUS = "/status";
	private final int port;
	private HttpServer server;

	private int rangoInt, rangoExt, numLibros;

	public servidorMinero ( int port ) { this.port = port; }

	public static void main( String[] args ) {
		
		int serverPort = 8080;

		if ( args.length == 1 ) serverPort = Integer.parseInt( args[0] );

		servidorMinero webServer = new servidorMinero( serverPort );
		webServer.startServer();

		System.out.println( "Servidor escuchando en el puerto " + serverPort );
	}

	public void startServer() {

		try{ this.server = HttpServer.create( new InetSocketAddress( port ), 0 ); }
		catch( IOException e ){ e.printStackTrace(); return; }

		HttpContext statusContext = server.createContext( STATUS );
		statusContext.setHandler( this::handleStatus );

		HttpContext taskContext = server.createContext( ENDPOINT );
		taskContext.setHandler( this::handleTask );

		server.setExecutor( Executors.newFixedThreadPool( 8 ) );
		server.start();
	}

	private void handleStatus ( HttpExchange exchange ) throws IOException {

		if ( !exchange.getRequestMethod().equalsIgnoreCase( "get" ) ) {
			exchange.close();
			return;			
		}

		String response = "El servidor " + port + " esta en Linea";
		enviarRespuesta( response.getBytes(), exchange );
		System.out.println( "GET enviado" );
	}

	private void handleTask ( HttpExchange exchange ) throws IOException {

		if ( !exchange.getRequestMethod().equalsIgnoreCase( "post" ) ) {
			exchange.close();
			return;			
		}

		byte[] requestBytes = exchange.getRequestBody().readAllBytes();
		String buscar = new String( requestBytes );

		String[] parametro = buscar.split( " " );
		String[] rangos = parametro[ parametro.length - 1 ].split( "/" );

		String palabras = "";

		for ( int i = 0; i < parametro.length - 1; i++)
			palabras += parametro[i] + " ";

		rangoInt = Integer.parseInt( rangos[0] );
		numLibros = Integer.parseInt( rangos[1] );

		byte[] responseBytes = obtenerRespuesta( palabras );

		enviarRespuesta( responseBytes, exchange );

		System.out.println( "POST enviado" );
	}

	private byte[] obtenerRespuesta ( String buscar ){

		//String buscar = new String( requestBytes );
		System.out.println( "Buscar: " + buscar );

		File carpeta = new File( "libros/" );
        File[] archivos = carpeta.listFiles();

		int cantidadArchivos = archivos.length;

		String[] palabrasBuscar = buscar.split(" ");
		float[] numPalabras = new float[ palabrasBuscar.length ];

		String respuesta = "";
		String respuestaParcial = "";

        try {
         
            for ( int i = 0; i < numLibros ; i++ ) {

            	Scanner scanner = new Scanner( archivos[ i + rangoInt ] );           
	            scanner.useDelimiter("[\\s\\p{Punct}«»¡¿—“”]+");

	            int numPalabrasTotales = 0;
	            
	            while (scanner.hasNext()) {

	                String palabra = scanner.next();
	                palabra = palabra.toLowerCase();
	                numPalabrasTotales++;

	                for ( int j = 0; j < numPalabras.length; j++ )
		                if ( palabra.equalsIgnoreCase( palabrasBuscar[j] ) )
		                	numPalabras[j]++;
	            }
	            
	            System.out.println( "Libro " + (i+rangoInt) +": " + archivos[i+rangoInt].getName() );
	            respuestaParcial += archivos[i + rangoInt].getName() + "|";
	            
	            for ( int z = 0; z < numPalabras.length; z++ ) {
	            	System.out.println( "Número de coincidencias: " + palabrasBuscar[z] + " - " + numPalabras[z] );
	            	numPalabras[z] = numPalabras[z]/numPalabrasTotales;
	            	respuestaParcial += numPalabras[z] + "|";
	            	numPalabras[z] = 0;
	            }

	            respuestaParcial += "\n";

	            System.out.println( "Número total de palabras: " + numPalabrasTotales);
	            System.out.println( "" );

	            scanner.close();

            	//System.out.println( respuestaParcial );

	            respuesta += respuestaParcial;
	            respuestaParcial = "";

            }

	        // System.out.println( "\n\n" + respuesta );
			return String.format("%s\n", respuesta ).getBytes();

		} catch ( Exception e) { 
			System.out.println("El archivo no fue encontrado.");
			return String.format("ERROR\n").getBytes();
		}
	}

	private void enviarRespuesta ( byte[] respBytes, HttpExchange exchange ) throws IOException {

		exchange.sendResponseHeaders( 200, respBytes.length );
		
		OutputStream stream = exchange.getResponseBody();
		stream.write( respBytes );
		stream.flush();
		stream.close();
		exchange.close();
	}
}