package com.careggio.marcos.tomaestado;

import android.content.ContentValues;
import android.content.Context;

/**
 * Created by marcos on 30/9/2018.
 */

public class Opciones
{
    public void Opciones(){

    }
    public static String getTableStructQuery(){
        String consulta="create table opciones (id integer  primary key AUTOINCREMENT,agua_porcentaje_control integer,agua_minimo_control integer,agua_maximo_control integer,energia_porcentaje_control integer);";
        return consulta;
    }
    public static ContentValues getValoresDefault(){
        ContentValues registro=new ContentValues();
        registro.put("id",1);
        registro.put("agua_porcentaje_control",45);
        registro.put("agua_minimo_control",9000);
        registro.put("agua_maximo_control",20000);
        registro.put("energia_porcentaje_control",45);
        return registro;
    }
    public void cambiarValorInt(Context context,String nombre_campo,String valor){
        ContentValues registro=new ContentValues();
        String[][] str_args=new String[10][10];
        Bbdd bd=new Bbdd();
        str_args[0][0]=nombre_campo;
        str_args[0][1]=valor;
        bd.actualizar(context,"opciones","id=1",str_args);
    }
    public int getAguaPorcentaje(Context context){
        Bbdd bd=new Bbdd();
        String [][] s=bd.consutlar(context,"SELECT  agua_porcentaje_control from opciones where id=1");
        return Integer.parseInt(s[0][0]);
    }
    public int getEnergiaPorcentaje(Context context){
        Bbdd bd=new Bbdd();
        String [][] s=bd.consutlar(context,"SELECT  energia_porcentaje_control from opciones where id=1");
        return Integer.parseInt(s[0][0]);
    }
    public int getAguaMinimo(Context context){
        Bbdd bd=new Bbdd();
        String [][] s=bd.consutlar(context,"SELECT  agua_minimo_control from opciones where id=1");
        return Integer.parseInt(s[0][0]);
    }
    public int getAguaMaximo(Context context){
        Bbdd bd=new Bbdd();
        String [][] s=bd.consutlar(context,"SELECT  agua_maximo_control from opciones where id=1");
        return Integer.parseInt(s[0][0]);
    }
}
