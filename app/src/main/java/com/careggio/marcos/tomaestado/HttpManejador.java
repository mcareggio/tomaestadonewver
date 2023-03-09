package com.careggio.marcos.tomaestado;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadStatusDelegate;

import java.io.File;
import java.util.UUID;

/**
 * Created by marcos on 20/10/2018.
 */

public class HttpManejador {
    private String ip;
    private String usuario;
    private String password;
    private final String web_peticiones="php_peticiones.php";
    private final String archivo_php_recibidor="recibir_archivo.php";

    public  HttpManejador(String ip,String usuario,String password){
        this.ip=ip;
        this.usuario=usuario;
        this.password=password;

    }
    public void enviarPeticion(Context context, String peticion){


// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url ="http://"+ip+"/"+web_peticiones+"?usuario="+usuario+"&pass="+password+"&peticion="+peticion;

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        System.out.println("Respuesta"+response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Fallo Peticion");
            }
        });

// Add the request to the RequestQueue.
        System.out.println("Enviando peticion a "+url);
        queue.add(stringRequest);

    }
    public void subirArchivoAServer(final Context context,File archivo_a_subir){
        final File ruta_archivo_a_subir=archivo_a_subir;
        archivo_a_subir.setReadable(true);
        archivo_a_subir.setExecutable(true);

        String URL_SUBIRPICTURE="http://"+ip+"/"+archivo_php_recibidor;
        try {

            final String filenameGaleria = ruta_archivo_a_subir.getName();
            String uploadId = UUID.randomUUID().toString();
            new MultipartUploadRequest(context, uploadId, URL_SUBIRPICTURE)
                    .addFileToUpload(ruta_archivo_a_subir.getPath(), "param_ruta")
                    .addParameter("filename", filenameGaleria)
                    .addParameter("extra","testing extra parameter valor")
                    .setMaxRetries(2)
                    .setDelegate(new UploadStatusDelegate() {
                        @Override
                        public void onProgress(UploadInfo uploadInfo) {
                            System.out.println(filenameGaleria+" "+ruta_archivo_a_subir.getPath());
                        }

                        @Override
                        public void onError(UploadInfo uploadInfo, Exception e) {
                            System.out.println("Error Subida");
                        }

                        @Override
                        public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {
                            //ELiminar imagen
                            /*
                            File eliminar = new File(ruta_archivo_a_subir.getPath());
                            if (eliminar.exists()) {
                                if (eliminar.delete()) {
                                    System.out.println("archivo eliminado:" + ruta_archivo_a_subir.getPath());
                                } else {
                                    System.out.println("archivo no eliminado" + ruta_archivo_a_subir.getPath());
                                }
                            }*/
                            System.out.println(serverResponse.getBodyAsString());
                            Toast.makeText(context,"Archivo subido exitosamente",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(UploadInfo uploadInfo) {}
                    })
                    .startUpload();

        } catch (Exception exc) {
            System.out.println(exc.getMessage()+" "+exc.getLocalizedMessage());
        }
    }

}
