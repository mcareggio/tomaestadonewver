package com.careggio.marcos.tomaestado;

import android.content.ContentValues;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Created by marcos on 27/9/2018.
 */

public class Orden_usuario {
    Integer orden,ruta,folio;
    Context context;
    String tipo_toma_estado="";
    public static String getTableStructQuery(String tipo_toma_estado){

        return "create table orden_"+tipo_toma_estado+"(id integer  primary key AUTOINCREMENT,ruta integer,folio integer,nro_orden integer);";
    }
    public String[] cargar_orden(Context context,String uri,String tipo_toma_estado){

        this.context=context;
        Bbdd bd=new Bbdd();

     String falloOutput="";
     int linea=0;
     boolean carga=true;
     boolean fallo=false;
     String s="";
        if(tipo_toma_estado=="energia")
            this.tipo_toma_estado="energia";
         else
            this.tipo_toma_estado="agua";

        bd.vaciar_tabla(context,"orden_"+this.tipo_toma_estado);
        try {
            // Creamos un objeto InputStreamReader, que será el que nos permita
            // leer el contenido del archivo de texto.
            InputStreamReader archivo;


            System.out.println("Archivo "+uri);
            File file=new File(uri);

            archivo = new InputStreamReader(new FileInputStream(file));
            // Creamos un objeto buffer, en el que iremos almacenando el contenido
            // del archivo.
            BufferedReader br = new BufferedReader(archivo);

            String [][] array=new String[10][10];



            while(br.ready()&&carga) {
                s=new String(br.readLine());
                String [] reslt;
                reslt=s.split(";");
                System.out.println(s+"_resl len"+reslt.length);
                    //insetar orden en tabla
                  if(!Calculo.isRuta(reslt[0]))
                  {
                      carga=false;
                      falloOutput+="fallo ruta ";
                  }
                  if(!Calculo.isFolio(reslt[1])){
                      carga=false;
                      falloOutput+="fallo folio ";
                  }
                  if(!Calculo.isOrden(reslt[2])){
                      carga=false;
                      falloOutput+="fallo orden ";
                  }
                  if(carga) {
                      this.ruta = Integer.parseInt(reslt[0]);
                      this.folio = Integer.parseInt(reslt[1]);
                      this.orden = Integer.parseInt(reslt[2]);
                      this.guardarOrden();
                      linea++;
                  }
                  else
                      fallo=true;



            }

            System.out.println("fin lectura");
        } catch (Exception e) {e.printStackTrace();
            System.out.println("Error al Cargar Archivo de estados");
            fallo=true;
        }
        String [] res=new String[4];
        if(fallo){
            res[0]="1";
            res[1]=String.valueOf(linea);
            res[2]=falloOutput;
            res[3]=s;

            bd.vaciar_tabla(context,"orden_"+this.tipo_toma_estado);
        }
        else {
            res[0]="0";
        }
        return res;
    }

    public void guardarOrden(){
        Bbdd bd=new Bbdd();
        ContentValues registro=new ContentValues();
        String tabla_toma_estado="orden_"+this.tipo_toma_estado;
        registro.put("ruta",this.ruta);
        registro.put("folio",this.folio);
        registro.put("nro_orden",this.orden);
        bd.insertar(context,tabla_toma_estado,registro);
    }
    public String[][] revisarSiTodosTienenOrden(Context context,String tipo_toma_estado){
        String tabla_orden="orden_"+tipo_toma_estado;
        Bbdd bd=new Bbdd();

        String consulta="select usuarios.id,usuarios.ruta,usuarios.folio,usuarios.nombre_apellido,usuarios.direccion from usuarios left join "+tabla_orden+" on usuarios.ruta="+tabla_orden+".ruta and usuarios.folio="+tabla_orden+".folio where usuarios.med_"+tipo_toma_estado+"=1 and "+tabla_orden+".id is null ORDER BY usuarios.ruta,usuarios.folio";
        String[][] res=bd.consutlar(context,consulta);

        return res;
    }
    public int getOrdenByRyF(Context context,String ruta,String folio,String tipo_toma_estado){
        Bbdd bd=new Bbdd();

        String consulta="SELECT nro_orden from orden_"+tipo_toma_estado+" where ruta="+ruta+" and folio="+folio;
        String[][] res=bd.consutlar(context,consulta);
        return Integer.parseInt(res[0][0]);
    }
    public String [][] getListadoCompletoOrden(Context context, String tipo_toma_estado){
        Bbdd bd=new Bbdd();
        String tabla_orden="orden_"+tipo_toma_estado;
        String consulta="SELECT usuarios.ruta,usuarios.folio,"+tabla_orden+".nro_orden,usuarios.nombre_apellido,usuarios.direccion from "+tabla_orden+" left join usuarios on usuarios.ruta="+tabla_orden+".ruta and usuarios.folio="+tabla_orden+".folio where usuarios.ruta not null and usuarios.folio not null order by "+tabla_orden+".nro_orden";
        String[][] res=bd.consutlar(context,consulta);

        return res;
    }
    public void ponerdebajo_orden(Context context,String ruta1,String folio1,String ruta2,String folio2 ,String tipo_toma_estado){
        Bbdd bd=new Bbdd();
        String tabla_orden="orden_"+tipo_toma_estado;
        String consulta="";

        consulta="SELECT nro_orden FROM "+tabla_orden+" WHERE ruta="+ruta2+" and folio="+folio2;
        String [][]res=bd.consutlar(context,consulta);
        String orden2=res[0][0];
        int orden2_int=Integer.parseInt(orden2);
        orden2_int++;
        consulta="UPDATE "+tabla_orden+" SET nro_orden=nro_orden+1 WHERE nro_orden>="+orden2_int;
        bd.consutlar(context,consulta);
        consulta="UPDATE "+tabla_orden+" SET nro_orden="+orden2_int+" WHERE ruta="+ruta1+" and folio="+folio1;
        bd.consutlar(context,consulta);
        this.reindexar(context,tipo_toma_estado);
    }
    public void ponerdebajo_ordenNuevo(Context context,String ruta1,String folio1,String ruta2,String folio2 ,String tipo_toma_estado){
        Bbdd bd=new Bbdd();
        String tabla_orden="orden_"+tipo_toma_estado;
        String consulta="";

        consulta="SELECT nro_orden FROM "+tabla_orden+" WHERE ruta="+ruta2+" and folio="+folio2;
        String [][]res=bd.consutlar(context,consulta);
        String orden2=res[0][0];
        int orden2_int=Integer.parseInt(orden2);
        orden2_int++;
        consulta="UPDATE "+tabla_orden+" SET nro_orden=nro_orden+1 WHERE nro_orden>="+orden2_int;
        bd.consutlar(context,consulta);

        ContentValues registro=new ContentValues();
        registro.put("nro_orden",orden2_int);
        registro.put("ruta",ruta1);
        registro.put("folio",folio1);
        bd.insertar_reg(context,tabla_orden,registro);

        this.reindexar(context,tipo_toma_estado);
    }
    public void eliminarOrden(Context context,String ruta1,String folio1,String tipo_toma_estado){
        Bbdd bd=new Bbdd();
        String tabla_orden="orden_"+tipo_toma_estado;
        String consulta="DELETE FROM "+tabla_orden+" WHERE ruta="+ruta1+" and folio="+folio1;
       try {
           bd.consutlar(context, consulta);
           this.reindexar(context, tipo_toma_estado);
       }catch (Exception e){e.printStackTrace();};
    }
    public void eliminarOrdenSinReindexar(Context context,String ruta1,String folio1,String tipo_toma_estado){
        Bbdd bd=new Bbdd();
        String tabla_orden="orden_"+tipo_toma_estado;
        String consulta="DELETE FROM "+tabla_orden+" WHERE ruta="+ruta1+" and folio="+folio1;
        try {
            bd.consutlar(context, consulta);
            //this.reindexar(context, tipo_toma_estado);
        }catch (Exception e){e.printStackTrace();};
    }
    public void reindexarTodo(Context context){
        try {
            this.reindexar(context, "energia");
        }catch (Exception e){e.printStackTrace();};
        try {
            this.reindexar(context, "agua");
        }catch (Exception e){e.printStackTrace();};
    }
    private void reindexar(Context context,String tipo_toma_estado){
        Bbdd bd=new Bbdd();
        String tabla_orden="orden_"+tipo_toma_estado;
        String consulta="SELECT usuarios.ruta,usuarios.folio FROM "+tabla_orden+" left join usuarios on usuarios.ruta="+tabla_orden+".ruta and usuarios.folio="+tabla_orden+".folio where usuarios.ruta not null and usuarios.folio not null order by nro_orden";
        String [][]res=bd.consutlar(context,consulta);
        //consulta="UPDATE "+tabla_orden+" SET nro_orden="+(i+1)+" WHERE ruta="+res[i][0]+" and folio="+res[i][1];
        //sacar vaciar tabla
        bd.vaciar_tabla(context,tabla_orden);

        for(int i=0;i<res.length;i++){
            consulta="INSERT INTO "+tabla_orden+" (nro_orden,ruta,folio) values ("+(i+1)+","+res[i][0]+","+res[i][1]+")";
            bd.consutlar(context,consulta);

        }
    }

}
