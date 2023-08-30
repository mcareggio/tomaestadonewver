package com.careggio.marcos.tomaestado;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class ActualizarEstados {

    public static String getTableStructQuery(){
        return "create table estados_tmp (id integer  primary key AUTOINCREMENT,ruta integer,folio integer, estado integer, fecha_hora, observacion, observasion_sist,geolocalizacion);";
    }

    public void actualizarEstados(Context context, String uri,String periodo,String tipo_toma_estado){
        Bbdd bd=new Bbdd();
        String tabla="estado_"+periodo+"_"+tipo_toma_estado;
        bd.vaciar_tabla(context,tabla);
        try {

            InputStreamReader archivo;
            System.out.println("Archivo " + uri);
            File file = new File(uri);
            archivo = new InputStreamReader(new FileInputStream(file));

            BufferedReader br = new BufferedReader(archivo);

            bd.resetearTabla(context, tabla);
            //id,ruta,folio,estado,fecha_hora,observacion,observacion_sis,geoloc

            String[][] array = new String[11][11];

            array[0][0]="ruta";
            array[1][0]="folio";
            array[2][0]="estado";
            array[3][0]="fecha_hora";
            array[4][0]="observacion";
            array[5][0]="observacion_sist";
            array[6][0]="geolocalizacion";


            String [] reslt;
            String s="";
            br.readLine();
            while(br.ready()) {

                s = new String(br.readLine());
                reslt=s.split(";");
                System.out.println(s.toString()+reslt.length);

                array[0][1]=reslt[0];
                array[1][1]=reslt[1];;
                array[2][1]=reslt[6];;
                array[3][1]=reslt[10];;
                array[4][1]=reslt[8];;
                array[5][1]=reslt[9];;
                if(reslt.length<12)
                array[6][1]="";
                else
                array[6][1]=reslt[11];
                bd.insertar(context,tabla,array);
            }
        }catch(Exception e){e.printStackTrace();}
    }
}
