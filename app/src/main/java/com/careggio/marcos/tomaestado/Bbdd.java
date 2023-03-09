package com.careggio.marcos.tomaestado;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by marcos on 24/3/2018.
 */

public class Bbdd extends Activity{
    public void Bbdd(){

    }
    public void insertar(Context context,String tabla,String[][] argumentos){
        Adminsqltite admin = new Adminsqltite(context,"administracion", null, Adminsqltite.version);
        SQLiteDatabase bd = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();
        int i=0;
        while(argumentos[i][0]!=null) {
            registro.put(argumentos[i][0],argumentos[i][1]);
            System.out.println("insersion:"+argumentos[i][0]+"  "+argumentos[i][1]);
            i++;
        }
        bd.insert(tabla, null, registro);
        bd.close();
    }
    public void insertar(Context context,String tabla,ContentValues registro){
        Adminsqltite admin = new Adminsqltite(context,"administracion", null, Adminsqltite.version);

        SQLiteDatabase bd = admin.getWritableDatabase();




        bd.insert(tabla, null, registro);

        bd.close();
    }
    public String[][] consutlar(Context context,String Consulta){
        Adminsqltite admin = new Adminsqltite(context,"administracion", null, Adminsqltite.version);

        SQLiteDatabase bd = admin.getWritableDatabase();

        Cursor fila=bd.rawQuery(Consulta,null);

        int col=fila.getColumnCount();
        int fil=fila.getCount();
        String[][] array= new String[fil][col];

        //System.out.println("Ejecutando-> "+Consulta);
        System.out.println("filas"+fila.getCount()+" columnas"+col);
        int i=0;
        while(fila.moveToNext()) {

                for(int j=0;j<col;j++) {
                    array[i][j] = fila.getString(j);
                    //System.out.println(i+"_"+"_"+j+"_fila: "+(i+1)+"_"+fila.getString(j));

                }
            i++;

        }
        bd.close();
        return array;
        }
    public void vaciar_tabla(Context context, String tabla){
        Adminsqltite admin = new Adminsqltite(context,"administracion", null, Adminsqltite.version);

        SQLiteDatabase bd = admin.getWritableDatabase();
        bd.execSQL("DELETE FROM "+tabla);

    }
    public void inicializarBBDD(Context context){
        String consulta;
/*
        Adminsqltite admin = new Adminsqltite(context,"administracion", null, Adminsqltite.version);

        SQLiteDatabase bd = admin.getWritableDatabase();
        consulta="DROP TABLE estado_03_2018_energia";
        bd.execSQL(consulta);
        consulta="CREATE TABLE estado_03_2018_energia(id integer  primary key AUTOINCREMENT,ruta integer,folio integer,estado integer,fecha_hora text);";
        bd.execSQL(consulta);
*/
    }
    public void mostrar_tablas(Context context){

        Adminsqltite admin = new Adminsqltite(context,"administracion", null, Adminsqltite.version);

        SQLiteDatabase bd = admin.getWritableDatabase();

        String consulta="select name from sqlite_master where type = 'table'";
        String[][] tablas=this.consutlar(context,consulta);

       for(int i=0;i<tablas.length;i++){
             consulta="select *from "+tablas[i][0];
            this.consutlar(context,consulta);

        }
        bd.close();
    }
    public void resetearTabla(Context context,String tabla){
        Adminsqltite admin = new Adminsqltite(context,"administracion", null, Adminsqltite.version);

        SQLiteDatabase bd = admin.getWritableDatabase();
        //String consulta="DROP TABLE IF EXISTS "+tabla;
        String consulta="DELETE FROM "+tabla;
        bd.execSQL(consulta);
        consulta="delete from sqlite_sequence where name='"+tabla+"'";
        bd.execSQL(consulta);
        bd.close();

    }

 public void ejecutarConsulta(Context context,String consulta){
     Adminsqltite admin = new Adminsqltite(context,"administracion", null, Adminsqltite.version);
     SQLiteDatabase bd = admin.getWritableDatabase();
     bd.execSQL(consulta);
     bd.close();
 }
 public void actualizar(Context context,String tabla,String clausula,String [][] registros){
     Adminsqltite admin = new Adminsqltite(context,"administracion", null, Adminsqltite.version);
     SQLiteDatabase bd = admin.getWritableDatabase();
     int i=0;
     boolean coma_flag=false;
     String set_str="";
     while(registros[i][0]!=null){
             if(coma_flag) {
                 set_str += ",";
                coma_flag=true;
             }
             set_str+=registros[i][0]+"="+registros[i][1];

         i++;
     }
     String consulta="UPDATE "+tabla+" SET "+set_str+"  WHERE "+clausula;
     //System.out.println("Ejecutando ->"+consulta);
     bd.execSQL(consulta);
     bd.close();
 }

    public void eliminar(Context context, String tabla, String clausula) {
        Adminsqltite admin = new Adminsqltite(context,"administracion", null, Adminsqltite.version);
        SQLiteDatabase bd = admin.getWritableDatabase();
        String consulta="DELETE FROM "+tabla+" WHERE "+clausula;
        bd.execSQL(consulta);

        bd.close();
    }

    public void eliminarTabla(Context context, String tabla) {
        Adminsqltite admin = new Adminsqltite(context,"administracion", null, Adminsqltite.version);
        SQLiteDatabase bd = admin.getWritableDatabase();
        String consulta="DROP TABLE IF EXISTS "+tabla;
        bd.execSQL(consulta);

        bd.close();
    }

    public boolean isExistTabla(Context context, String tabla) {
        String consulta="SELECT count(*) FROM sqlite_sequence where name='"+tabla+"'";
        String[][] array_res=this.consutlar(context,consulta);
        boolean res=false;
        System.out.println(array_res[0][0]+"_____"+array_res[0][0].compareTo("1"));
        if(array_res[0][0].compareTo("1")==0)
            res=true;

        return res;

    }
    public void insertar_reg(Context context,String tabla,ContentValues registro){
        Adminsqltite admin = new Adminsqltite(context,"administracion", null, Adminsqltite.version);

        SQLiteDatabase db = admin.getWritableDatabase();
        db.insert(tabla,null,registro);
        db.close();
    }
    public void actualizar_reg(Context context,String tabla,ContentValues registro,String clausula,String[] argumentos_clausula){
        Adminsqltite admin = new Adminsqltite(context,"administracion", null, Adminsqltite.version);

        SQLiteDatabase db = admin.getWritableDatabase();
        db.update(tabla,registro,clausula,argumentos_clausula);

        db.close();
    }
    public ContentValues consultarRegistro(Context context,String consulta){
        Adminsqltite admin = new Adminsqltite(context,"administracion", null, Adminsqltite.version);

        SQLiteDatabase bd = admin.getWritableDatabase();

        Cursor fila=bd.rawQuery(consulta,null);

        int col=fila.getColumnCount();
        ContentValues registro=new ContentValues();
        System.out.println(consulta);
            for(int j=0;j<col;j++) {
                registro.put(fila.getColumnName(1),fila.getString(j));

            }

        bd.close();
        return registro;

    }
}
