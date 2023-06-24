package com.mycompany.servidorprincipal;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class Aggregator {

    private WebClient clienteWeb;

    public Aggregator( String metodo ) {
        this.clienteWeb = new WebClient( metodo );
    }

    public List< String> sendTaskToWorkers(List< String> addresses, List< String > tasks ) {

        CompletableFuture< String>[] futuros = new CompletableFuture[addresses.size()];

        for ( int i = 0; i < addresses.size(); i++ ) {

            String address = addresses.get(i);
            String task = tasks.get ( i );

            // System.out.println( "Enviando: " + task + " a " + address );
       
            byte[] reqPayload = task.getBytes();
            futuros[i] = clienteWeb.sendTask(address, reqPayload);
        }

        List< String> results = new ArrayList();

        for (int j = 0; j < addresses.size(); j++) {
            results.add(futuros[j].join());
        }

        return results;
    }
}
