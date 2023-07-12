package com.careggio.marcos.tomaestado;

import android.content.ContentValues;
import android.content.Context;

/**
 * Created by marcos on 25/3/2018.
 */

public class Usuario {
    private String nombreyapellido;
    private String direccion;
    private String ruta;
    private String folio;
    private String nro_medidor;

    public void Usuario(){}
    public String[][] cargarUsuario(Context context,String ruta, String folio){
        Bbdd bd=new Bbdd();
        String [][] array=bd.consutlar(context,"SELECT * FROM usuarios WHERE ruta="+ruta+" and folio="+folio+" LIMIT 1");
        this.nombreyapellido=array[0][3];
        this.direccion=array[0][4];
        this.ruta=array[0][1];
        this.folio=array[0][2];
        this.nro_medidor=array[0][5];
        return array;
    }
    public String [][] proximo_usuario_vacio(Context context,String tipo_t_estado,String periodo){
        String[][] array=proximo_usuario_vacio(context,tipo_t_estado,periodo,"0");
        return array;
    }
    public String [][] proximo_usuario_vacio(Context context,String tipo_t_estado,String periodo, String orden_actual){
        Bbdd bd=new Bbdd();
        String tabla_toma_estado="estado_"+periodo+"_"+tipo_t_estado;
        String orden_toma_estado="orden_"+tipo_t_estado;
        String campo_estado="estado";
        String tabla_orden="orden_"+tipo_t_estado;
        String[][] array;
        String comp=new String(orden_actual);
        if(comp.equals(""))
            orden_actual="0";

        String consulta="SELECT usuarios.id,usuarios.ruta,usuarios.folio,usuarios.nombre_apellido,usuarios.direccion,"+tabla_orden+".nro_orden,usuarios.nro_med_"+tipo_t_estado+" FROM usuarios LEFT JOIN  "+tabla_toma_estado+" ON usuarios.ruta="+tabla_toma_estado+".ruta and usuarios.folio="+tabla_toma_estado+".folio LEFT JOIN  "+tabla_orden+" ON usuarios.ruta="+tabla_orden+".ruta and usuarios.folio="+tabla_orden+".folio where "+tabla_toma_estado+"."+campo_estado+" is null  and "+tabla_orden+".nro_orden"+">"+orden_actual+" ORDER BY "+tabla_orden+".nro_orden";
        //String consulta="SELECT usuarios.* FROM usuarios LEFT JOIN  "+tabla_toma_estado+" ON usuarios.ruta="+tabla_toma_estado+".ruta and usuarios.folio="+tabla_toma_estado+".folio where "+tabla_toma_estado+"."+campo_estado+" is null  and usuarios."+orden_toma_estado+">"+orden_actual+" ORDER BY usuarios."+orden_toma_estado;
        // consulta="SELECT * FROM usuarios LEFT  JOIN  "+tabla_toma_estado+" ON usuarios.ruta="+tabla_toma_estado+".ruta and usuarios.folio="+tabla_toma_estado+".folio";
        //consulta="SELECT * FROM estado_03_2018 WHERE estado_03_2018.estado_energia is null";
      // consulta="SELECT * from usuarios";
        //bd.inicializarBBDD(context);
        //System.out.println(" th"+consulta);
        array=bd.consutlar(context,consulta);
        //array=bd.consutlar(context,"SELECT * FROM "+periodo+" WHERE orden_"+tipo_t_estado+">"+orden_actual+" and estado_"+tipo_t_estado);
        return array;
    }
    public String [][]buscarUsuarios(Context context,String busqueda,String tipo_toma_estado) {
        Bbdd bd = new Bbdd();
        String[] str_spliteado = new String[10];
        String tabla_orden="orden_"+tipo_toma_estado;
        String pre_consulta="SELECT nombre_apellido,direccion,nro_med_" + tipo_toma_estado + ",usuarios.ruta,usuarios.folio,"+tabla_orden+".nro_orden from usuarios join "+tabla_orden+" on usuarios.ruta="+tabla_orden+".ruta and usuarios.folio="+tabla_orden+".folio";
        String consulta = pre_consulta+" where nombre_apellido like '%" + busqueda + "%'  or direccion like '%" + busqueda + "%' or nro_med_" + tipo_toma_estado + " like '%" + busqueda + "%' limit 10;";
        if (Calculo.isNumeric(busqueda))
            consulta = pre_consulta+" where nombre_apellido like '%"+busqueda+"%' or direccion like '%"+busqueda+"%' or nro_med_" + tipo_toma_estado + " like '" + busqueda + "%'";
        else {
            str_spliteado = busqueda.split(" ");
            if (str_spliteado.length > 1 && str_spliteado[1] != null && str_spliteado[1].compareTo("") != 0) {
                consulta = pre_consulta+" where (nombre_apellido like '%" + str_spliteado[0] + "%' and nombre_apellido like '%" + str_spliteado[1] + "%')  or (direccion like '%" + str_spliteado[0] + "%' and direccion like '%" + str_spliteado[1] + "%')  or (nro_med_" + tipo_toma_estado + " like '%" + busqueda + "%') limit 10;";
                System.out.println(str_spliteado.length + " " + str_spliteado[0] + " " + str_spliteado[1]);

            }
            else
                if(str_spliteado.length!=0)
                consulta = pre_consulta+" where nombre_apellido like '%"+str_spliteado[0]+"%' or direccion like '%"+str_spliteado[0]+"%' or nro_med_" + tipo_toma_estado + " like '" + str_spliteado[0] + "%'";
        }
        consulta=consulta+" order by "+tabla_orden+".nro_orden";
        String [][] res=bd.consutlar(context,consulta);
        return res;
    }
    public String [][]buscarUsuariosTodos(Context context,String busqueda) {
        Bbdd bd = new Bbdd();
        String[] str_spliteado = new String[10];
        String consulta;
        String pre_consulta="SELECT nombre_apellido,direccion,ruta,folio,nro_med_energia,nro_med_agua,med_energia,med_agua from usuarios";
        if(busqueda.compareTo("")==0){
        consulta=pre_consulta+" order by ruta,folio";
        }
        else
        consulta= pre_consulta+" where nombre_apellido like '%" + busqueda + "%'  or direccion like '%" + busqueda + "%' or ruta like '%" + busqueda + "%' or folio like '%" + busqueda + "%'  order by ruta,folio limit 10 ;";

        String [][] res=bd.consutlar(context,consulta);
        return res;
    }
    public void guardarNuevoUsr(Context context,String nombreyapellido,String ruta,String folio,String direccion,String nro_med_energia,Boolean med_energia,String nro_med_agua,Boolean med_agua){
        Bbdd bd = new Bbdd();
        ContentValues registro=new ContentValues();
        registro.put("ruta",ruta);
        registro.put("folio",folio);
        registro.put("nombre_apellido",nombreyapellido);
        registro.put("direccion",direccion);
        registro.put("nro_med_energia",nro_med_energia);
        registro.put("nro_med_agua",nro_med_agua);
        registro.put("med_energia",med_energia);
        registro.put("med_agua",med_agua);

        bd.insertar_reg(context,"usuarios",registro);
    }
    public void modificarUsr(Context context,String nombreyapellido,String ruta,String folio,String direccion,String nro_med_energia,Boolean med_energia,String nro_med_agua,Boolean med_agua){
        Bbdd bd = new Bbdd();
        ContentValues registro=new ContentValues();
        registro.put("ruta",ruta);
        registro.put("folio",folio);
        registro.put("nombre_apellido",nombreyapellido);
        registro.put("direccion",direccion);
        registro.put("nro_med_energia",nro_med_energia);
        registro.put("nro_med_agua",nro_med_agua);
        registro.put("med_energia",med_energia);
        registro.put("med_agua",med_agua);
        String clausula="ruta=? and folio=?";
        String [] argumentos_clausula=new String[2];
        argumentos_clausula[0]=ruta;
        argumentos_clausula[1]=folio;
        bd.actualizar_reg(context,"usuarios",registro,clausula,argumentos_clausula);

    }
    public void eliminarUsuario(Context context,String ruta,String folio){
        Bbdd bd = new Bbdd();

        bd.eliminar(context,"usuarios","ruta="+ruta+" and folio="+folio);
        Orden_usuario ord;
        ord=new Orden_usuario();
        ord.eliminarOrden(context,ruta,folio,"energia");
        ord.eliminarOrden(context,ruta,folio,"agua");



    }
    public boolean existeRutaFolio(Context context,String ruta, String folio){
        Bbdd bd = new Bbdd();
        boolean rta=true;
        String consulta="SELECT COUNT(*) from usuarios WHERE ruta="+ruta+" and folio="+folio;

        String [][] res=bd.consutlar(context,consulta);
        if(res[0][0].compareTo("0")==0){
            rta=false;
        }
        return rta;
    }
    public String[][] getDatosUsuario(Context context,String ruta,String folio){
        Bbdd bd = new Bbdd();

        String consulta="SELECT * FROM usuarios WHERE ruta="+ruta+" "+"and folio="+folio;
        String[][] res=bd.consutlar(context,consulta);

        return res;
    }
    public void eliminarUsuariosById(Context context,int [] id){
        Bbdd bd = new Bbdd();
        String[][] res;
        Orden_usuario ord;
        ord=new Orden_usuario();

        for (int i=0;i<id.length;i++) {
            res = bd.consutlar(context, "SELECT ruta,folio from usuarios where usuarios.id=" + id[i]);
            bd.eliminar(context,"usuarios","ruta="+res[0][0]+" and folio="+res[0][1]);
            ord.eliminarOrden(context,res[0][0],res[0][1],"energia");
            ord.eliminarOrden(context,res[0][0],res[0][1],"agua");
        }
    }
    public void guardarGeolocSiNoExiste(Context context,String ruta, String folio,String geolocalizacion){
        Bbdd bd = new Bbdd();
        String consulta="";
        if(geolocalizacion!=""&&!isGeolocCargada(context,ruta,folio)){
            consulta="UPDATE usuarios set geolocalizacion='"+geolocalizacion+"' where ruta="+ruta+" and folio="+folio;
            bd.consutlar(context,consulta);
        }

    }
    private  boolean isGeolocCargada(Context context,String ruta,String folio){
        boolean rta=true;
        Bbdd bd = new Bbdd();
        String res[][]=bd.consutlar(context,"SELECT geolocalizacion FROM usuarios where ruta="+ruta+" and folio="+folio);
        if(res[0][0]==null||res[0][0]=="")
        rta=false;

        return  rta;
    }

    public String getNombreyapellido() {
        return nombreyapellido;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getFolio() {
        return folio;
    }

    public String getNro_medidor() {
        return nro_medidor;
    }

    public String getRuta() {
        return ruta;
    }
}
