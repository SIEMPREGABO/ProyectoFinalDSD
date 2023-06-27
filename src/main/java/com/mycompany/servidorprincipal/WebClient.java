//////////////////////////////////////////////////
//  PROYECTO FINAL - SISTEMAS DISTRIBUIDOS      //
//                                              //
//  DIAZ MALDONADO JESUS RENATO         - 4CM13 //
//  MIRON AREVALO GABRIEL OMAR          - 4CM14 //
//  TORRES CARRILLO JOSEHF MIGUEL ANGEL - 4CM11 //
//////////////////////////////////////////////////

package com.mycompany.servidorprincipal;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class WebClient {

    private HttpClient cliente;
    private String metodo;

    public WebClient( String metodo ) {
        this.cliente = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        this.metodo = metodo;
    }

    public CompletableFuture< String> sendTask( String url, byte[] requestPayload ) {

        CompletableFuture<String> resultFuture = null;

        if ( metodo.equals( "GET" ) ) {
        
            // System.out.println( "ENTRE GET" );

            HttpRequest req = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .header("X-Debug", "true")
                .build();

            CompletableFuture<HttpResponse<String>> responseFuture = cliente.sendAsync(req, HttpResponse.BodyHandlers.ofString());
            resultFuture = responseFuture.thenApply(HttpResponse::body)
                .orTimeout( (long)1 , TimeUnit.SECONDS )
                .exceptionally( ex -> {
                    if (ex instanceof java.util.concurrent.TimeoutException)
                        return "Tiempo de espera agotado para la solicitud a " + url;
                    else {

                        String[] respuestaSplit1 = url.split( ":" );
                        String[] respuestaSplit2 = respuestaSplit1[2].split( "/" );
                        return "Error al enviar la solicitud a " + respuestaSplit2[0] + " en GET";
                    }
                });                
        }

        if ( metodo.equals( "POST" ) ) {

            // System.out.println( "ENTRE POST" );

            HttpRequest req = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofByteArray(requestPayload))
                .uri(URI.create(url))
                .header("X-Debug", "true")
                .build();

            CompletableFuture<HttpResponse<String>> responseFuture = cliente.sendAsync(req, HttpResponse.BodyHandlers.ofString());
            resultFuture = responseFuture.thenApply(HttpResponse::body)
                .orTimeout( (long)5 , TimeUnit.SECONDS )
                .exceptionally( ex -> {
                    if (ex instanceof java.util.concurrent.TimeoutException)
                        return "Tiempo de espera agotado para la solicitud a " + url;
                    else {

                        String[] respuestaSplit1 = url.split( ":" );
                        String[] respuestaSplit2 = respuestaSplit1[2].split( "/" );
                        return "Error al enviar la solicitud a " + respuestaSplit2[0] + " en POST";
                    }
                });           
        }

        return resultFuture;    
    }

}
