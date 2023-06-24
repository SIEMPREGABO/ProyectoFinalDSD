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

/*

package com.mycompany.servidorprincipal;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

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

        CompletableFuture<String> future = new CompletableFuture<>();

        if ( metodo.equals( "GET" ) ) {

            HttpRequest req = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .header("X-Debug", "true")
                .build();

            cliente.sendAsync( req, HttpResponse.BodyHandlers.ofString())
            .whenComplete((respuesta, excepcion) -> {
                if (excepcion != null) {
                    // Manejar la excepción cuando el servidor no está disponible
                    future.completeExceptionally(excepcion);
                } else {
                    future.complete(respuesta.body());
                }
            });
        }

        if ( metodo.equals( "POST") ) {

            HttpRequest req = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofByteArray(requestPayload))
                .uri(URI.create(url))
                .header("X-Debug", "true")
                .build();

            cliente.sendAsync( req, HttpResponse.BodyHandlers.ofString())
            .whenComplete((respuesta, excepcion) -> {
                if (excepcion != null) {
                    // Manejar la excepción cuando el servidor no está disponible
                    future.completeExceptionally(excepcion);
                } else {
                    future.complete(respuesta.body());
                }
            });
        }

        // return cliente.sendAsync(req, HttpResponse.BodyHandlers.ofString())
        //         .thenApply(respuesta -> {
        //             return respuesta.body().toString();
        //         });

        return future;
    }
}




package com.mycompany.servidorprincipal;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

        CompletableFuture<String> future = new CompletableFuture<>();
        Duration timeoutDuration = Duration.ofSeconds(10);

        if ( metodo.equals( "GET" ) ) {

            HttpRequest req = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .header("X-Debug", "true")
                .build();

            CompletableFuture<HttpResponse<String>> responseFuture = cliente.sendAsync(req, HttpResponse.BodyHandlers.ofString());

               long timeOutValue = 10;
               
    CompletableFuture<String> timeoutFuture = CompletableFuture.anyOf(responseFuture)
            .completeOnTimeout("TimeOut", timeOutValue, TimeUnit.SECONDS)
            .thenApply(result -> {
                if (result instanceof HttpResponse) {
                    HttpResponse<String> response = (HttpResponse<String>) result;
                    return response.body();
                } else {
                    return (String) result;
                }
            })
            .exceptionally(ex -> {
                if (ex instanceof CompletionException && ex.getCause() instanceof TimeoutException) {
                    future.completeExceptionally(new TimeoutException("Tiempo de espera agotado"));
                } else {
                    future.completeExceptionally(ex);
                }
                return "Tiempo de espera agotado";
            });

    timeoutFuture.thenAccept(future::complete);
        }

        // if ( metodo.equals( "POST") ) {

        //     HttpRequest req = HttpRequest.newBuilder()
        //         .POST(HttpRequest.BodyPublishers.ofByteArray(requestPayload))
        //         .uri(URI.create(url))
        //         .header("X-Debug", "true")
        //         .build();

        //     CompletableFuture<HttpResponse<String>> responseFuture = cliente.sendAsync(req, HttpResponse.BodyHandlers.ofString());

        //     CompletableFuture<String> timeoutFuture = responseFuture.thenApply(HttpResponse::body)
        //     .applyToEither(CompletableFuture.completedFuture("Valor predeterminado en caso de tiempo de espera"), result -> result)
        //     .exceptionally(ex -> {
        //         if (ex instanceof CompletionException && ex.getCause() instanceof TimeoutException) {
        //             future.completeExceptionally(new TimeoutException("Tiempo de espera agotado"));
        //         } else {
        //             future.completeExceptionally(ex);
        //         }
        //         return null;
        //     });

        //     timeoutFuture.thenAccept(future::complete);
        // }

        // return cliente.sendAsync(req, HttpResponse.BodyHandlers.ofString())
        //         .thenApply(respuesta -> {
        //             return respuesta.body().toString();
        //         });

        return future;
    }
}


*/