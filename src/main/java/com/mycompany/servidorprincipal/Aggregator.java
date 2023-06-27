//////////////////////////////////////////////////
//  PROYECTO FINAL - SISTEMAS DISTRIBUIDOS      //
//                                              //
//  DIAZ MALDONADO JESUS RENATO         - 4CM13 //
//  MIRON AREVALO GABRIEL OMAR          - 4CM14 //
//  TORRES CARRILLO JOSEHF MIGUEL ANGEL - 4CM11 //
//////////////////////////////////////////////////

package com.mycompany.servidorprincipal;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class Aggregator {

    private WebClient clienteWeb; 
    private WebClient confirmar; 
    private int taskCount;
    private boolean error;

    public Aggregator( String metodo ) {
        this.clienteWeb = new WebClient( metodo );
        this.confirmar = new WebClient("GET");
        this.taskCount = 0;
        this.error = false;
    }

    public List< String> sendTaskToWorkers(List< String> addresses, List< String > tasks ) {

        CompletableFuture< String>[] futuros = new CompletableFuture[addresses.size()];
        Map<Integer, Boolean> taskErrors = new HashMap<Integer, Boolean>();
        for (int i = 0; i < tasks.size(); i++) {
            taskErrors.put(i, false);
        }
        
        for ( int i = 0; i < addresses.size(); i++ ) {

            String address = addresses.get(i);
            String task = tasks.get ( i );

            // System.out.println( "Enviando: " + task + " a " + address );
       
            byte[] reqPayload = task.getBytes();
            futuros[i] = clienteWeb.sendTask(address, reqPayload);
        }

        List< String> results = new ArrayList();


        while(taskCount<tasks.size()){//Verifica que todas las tareas se hayan completado exitosamente
            for (int i = 0; i < futuros.length; i++) {
                if(i==0)
                    taskCount = 0;//Reinicia el contador de tareas para asegurarnos de que todas las tareas se completen
                if (futuros[i].isDone()){                                     
                    taskCount++;                    
                    if(futuros[i].join().contains("Error")){//Verifica si alguna tarea no se pudo completar                                                        
                        if(error){//Entra solo si hay dos servidores que no funcionan                         
                            if(futuros[0].isDone()&&futuros[1].isDone()&&futuros[2].isDone()){//Verifica que todas las tareas se hayan completado                              
                                for (int j = 0; j < futuros.length; j++) {//Busca el unico servidor que funciona y lo guarda para mandar ahí la tarea
                                    if(i!=j){                                      
                                        if(!confirmar.sendTask(addresses.get(j).replace("tarea","status"), null).join().contains("Error"))
                                            addresses.set(i, addresses.get(j));                                    
                                    }
                                }                                                                                    
                                futuros[i]=clienteWeb.sendTask(addresses.get(i), tasks.get(i).getBytes());//Envia la tarea al ultimo servidor funcional
                                taskCount = 0;//Fuerza una nueva validacion de que todas las tareas esten completas               
                            }
                        }
                        else{//Entra si hay un error en alguno de los servidores
                            boolean bandera=false;//Sirve para no repetir el proceso de manera inecesaria
                            for (int j = 0; j < futuros.length; j++) {//Busca un servidor que si haya terminado su tarea exitosamente para enviar la tarea fallida
                                if (futuros[j].isDone()&&!bandera&&j!=i){//Verifica que la tarea se haya completado, que no se haya enviado la tarea ya y que sea diferente de la tarea que fallo                                      
                                    if(!confirmar.sendTask(addresses.get(j).replace("tarea","status"), null).join().contains("Error")){//Verifica que no se envie a otro servidor que tambien este fallando
                                        bandera=true;
                                        error=true;//Indica que hay almenos una tarea que salio mal                       
                                        addresses.set(i,addresses.get(j));//Sustituye la direccion que no esta funcionando                                                             
                                        futuros[i]=clienteWeb.sendTask(addresses.get(j), tasks.get(i).getBytes());//Envia la tarea al servidor que si esta funcionando                            
                                    }
                                }                
                            } 
                        }
                        taskCount = 0;//Fuerza una nueva validacion de que todas las tareas esten completas               
                    }    
                      
                }
            }
        }

        for (int i = 0; i < futuros.length; i++) {
            //System.out.println("//////////////"+i+"//////////////");
            //System.out.println(futuros[i].getNow("Error"));
            results.add(futuros[i].getNow("Error"));
        }


        return results;
    }
}
