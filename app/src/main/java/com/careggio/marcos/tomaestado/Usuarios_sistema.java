package com.careggio.marcos.tomaestado;

import android.content.ContentValues;
import android.content.Context;

/**
 * Created by marcos on 5/10/2018.
 */

public class Usuarios_sistema {
    private Context context;
    private String nombre_usuario,password,toma_estado,tipo_usuario;
    public Usuarios_sistema(Context context,String nombre_usuario, String password){
        this.context=context;
        this.nombre_usuario=nombre_usuario;
        this.password=password;

    }
    public static String getTableStructQuery(){
        return "create table usuarios_sistema(id integer  primary key AUTOINCREMENT,nombre_usuario text,password text,tipo_toma_estado text,tipo_usuario integer);";
    }
        public static ContentValues getValoresDefault(int i){
            ContentValues registro=new ContentValues();
            switch (i){
                case 0:

                    registro.put("nombre_usuario","marcos");
                    registro.put("password","12345");
                    registro.put("tipo_toma_estado","energia");
                    registro.put("tipo_usuario",1);
                    break;
                case 1:

                    registro.put("nombre_usuario","guille");
                    registro.put("password","1234");
                    registro.put("tipo_toma_estado","agua");
                    registro.put("tipo_usuario",2);
                    break;
                case 2:

                    registro.put("nombre_usuario","walter");
                    registro.put("password","4567");
                    registro.put("tipo_toma_estado","energia");
                    registro.put("tipo_usuario",2);
                    break;

            }
            return registro;
        }

    public boolean IsValidAutentication(){
        boolean result=false;
        String consulta="SELECT COUNT(*) FROM usuarios_sistema WHERE nombre_usuario = '"+nombre_usuario+"' and password = '"+password+"';";
        Bbdd bd=new Bbdd();
        String res_cons[][]=bd.consutlar(context,consulta);

        if(res_cons[0][0].compareTo("1")==0)
            result=true;

        return result;
    }

    public String getTipoTomaEstado(){
        String consulta="SELECT tipo_toma_estado from usuarios_sistema where nombre_usuario='"+nombre_usuario+"' and password='"+password+"'";
        Bbdd bd=new Bbdd();
        String res_cons[][]=bd.consutlar(context,consulta);
        return res_cons[0][0];
    }
    private int getTipoUsuario(){
        String consulta="SELECT tipo_usuario from usuarios_sistema where nombre_usuario='"+nombre_usuario+"' and password='"+password+"'";
        Bbdd bd=new Bbdd();
        String res_cons[][]=bd.consutlar(context,consulta);
        return Integer.parseInt(res_cons[0][0]);
    }
    public boolean IsAdmin(){
        boolean res=false;
        if(this.getTipoUsuario()==1)
        res=true;

        return res;
    }
}
