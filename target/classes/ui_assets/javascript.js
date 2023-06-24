$( document ).ready(function() { 

    console.log( "ready!" );

    var button = $("#submit_button");   
    var searchBox = $("#search_text"); 
    var resultsTable = $("#results table tbody"); 
    var resultsWrapper = $("#results"); 

    button.on("click", function(){

        $.ajax({
          method : "POST",
          contentType: "text/plain",
          data: createRequest(),
          url: "procesar_datos",
          dataType: "text",
          success: onHttpResponse
          });
      });

    function createRequest() { return searchBox.val(); }

    function onHttpResponse( data, status ) {

        if (status === "success" ) { addResults( data ); } 
        else { alert("Error al conectarse al servidor: " + status); }
    }

    function addResults( data ) {

        resultsTable.empty();
        resultsWrapper.show();
        resultsTable.append("<thead><tr><th><h1 class=\"white-text\">Libros con mejores resultados</h1></th></tr></thead>");
        resultsTable.append("<tr><td>Titulo</td><td>Año publicacion</td><td>Autor</td></tr>");

        const librosNoTxt = data.split( ".txt\n" );
        var librosNoTxtU = "";

        for ( const libro of librosNoTxt )
             librosNoTxtU += libro + "-\n";

        const librosSeparador = librosNoTxtU.split( "-" );

        for ( var i = 0; i < librosSeparador.length - 2; i+=3 )
            resultsTable.append("<tr><td>" + librosSeparador[i+2] + "</td><td>" + librosSeparador[i+1] + "</td><td>" + librosSeparador[i] + "</td></tr>");
    }
});

