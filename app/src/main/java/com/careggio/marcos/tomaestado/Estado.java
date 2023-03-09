package com.careggio.marcos.tomaestado;

import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

/**
 * Created by marcos on 25/3/2018.
 */

public class Estado {
    Context context;
    String ruta,folio,estado_nuevo,periodo,campo_estado,tabla_toma_estado,tipo_toma_estado,observacion,obserbacion_sist,geolocalizacion;
    int resultado_verificacion_est;



    public  Estado(Context context,String ruta,String folio,String estado_nuevo,String periodo,String tipo_t_estado){
        this.context=context;
        this.ruta=ruta;
        this.folio=folio;
        this.estado_nuevo=estado_nuevo;
        this.periodo=periodo;
        this.campo_estado="estado";
        this.tipo_toma_estado=tipo_t_estado;
        this.tabla_toma_estado="estado_"+periodo+"_"+tipo_t_estado;
        this.observacion="";
        this.obserbacion_sist="";
        this.resultado_verificacion_est=0;
        this.geolocalizacion="";
    }
    public Estado(){

    }
    public Estado(String ruta,String folio){
        this.ruta=ruta;
        this.folio=folio;
    }
    public  Estado(Context context,String ruta,String folio,String estado_nuevo,String periodo,String tipo_t_estado,String obs,String obs_sistema){
        this.context=context;
        this.ruta=ruta;
        this.folio=folio;
        this.estado_nuevo=estado_nuevo;
        this.periodo=periodo;
        this.campo_estado="estado";
        this.tipo_toma_estado=tipo_t_estado;
        this.tabla_toma_estado="estado_"+periodo+"_"+tipo_t_estado;
        this.observacion=obs;
        this.obserbacion_sist=obs_sistema;
        this.geolocalizacion="";
    }
    public boolean verificacion_estadistica(Context context,String tipo_toma_estado,String estado_nuevo_sc){
        int resultado=0;
        boolean resp=false;
        if(tipo_toma_estado.compareTo("agua")==0)
            resultado=this.verificacionEstadisticaAgua(context,estado_nuevo_sc);
        else
            resultado=this.verificacionEstadisticaEnergia(context,estado_nuevo_sc);
        this.resultado_verificacion_est=resultado;

        if(resultado==0)
        resp=true;

        switch (this.resultado_verificacion_est){
            case -4:
                this.obserbacion_sist="Estado Negativo";
                break;
            case -3:
                this.obserbacion_sist="Demasiado bajo conrespecto al mismo mes del año anterior";
            break;
            case -2:
                this.obserbacion_sist="Demasiado alto conrespecto al mismo mes del año anterior";
            break;
            case -1:
                this.obserbacion_sist="Mayor al maximo establecido como alto";
            break;
        }

        return resp;
    }
    private int verificacionEstadisticaAgua(Context context,String estado_nuevo_sc){
        Opciones opt=new Opciones();

        String consumo_ano_anterior=this.getConsumoXperiodo(context,Calculo.getPeriodoAnoAnterior(periodo),tipo_toma_estado);
        int consumo_ano_anterior_int,tope_arriba,tope_abajo,consumo_actual_int;
        consumo_ano_anterior_int=Integer.parseInt(consumo_ano_anterior);
        consumo_actual_int=Integer.parseInt(estado_nuevo_sc)-Integer.parseInt(this.getEstadoByPeriodoyRyF(context,Calculo.getPeriodoAnterior(periodo),tipo_toma_estado,ruta,folio));
        double agua_porcentaje=((double)opt.getAguaPorcentaje(context)/100)+1;
        tope_arriba= (int) (consumo_ano_anterior_int*agua_porcentaje);
        tope_abajo=(int)(consumo_ano_anterior_int/agua_porcentaje);
        int respuesta=0;
        int agua_minimo=opt.getAguaMinimo(context);
        int agua_maximo=opt.getAguaMaximo(context);
        if(consumo_actual_int>=0&&consumo_actual_int<agua_minimo)
        respuesta=0;
        else
            if(consumo_actual_int<0)
                respuesta=-4;
            else
                if(consumo_actual_int>agua_minimo&&consumo_actual_int<=tope_abajo)
                    respuesta=-3;
            else
                if(consumo_actual_int>agua_minimo&&consumo_actual_int>=tope_arriba)
                    respuesta=-2;
            else
                    if(consumo_actual_int>agua_maximo)
                    respuesta=-1;
             else
                 respuesta=0;
             //0ok, -4 negativo, -3 menor al tope -2 mayor al tope, -1 mayor a 20000
        return respuesta;
    }
    public int verificacionEstadisticaEnergia(Context context,String estado_nuevo_sc){
        Opciones opt=new Opciones();
        String consumo_ano_anterior=this.getConsumoXperiodo(context,Calculo.getPeriodoAnoAnterior(periodo),tipo_toma_estado);
        int consumo_ano_anterior_int,tope_arriba,tope_abajo,consumo_actual_int;
        consumo_ano_anterior_int=Integer.parseInt(consumo_ano_anterior);
        consumo_actual_int=Integer.parseInt(estado_nuevo_sc)-Integer.parseInt(this.getEstadoByPeriodoyRyF(context,Calculo.getPeriodoAnterior(periodo),tipo_toma_estado,ruta,folio));
        double energia_porcentaje=((double)opt.getEnergiaPorcentaje(context)/100)+1;
        tope_arriba= (int) (consumo_ano_anterior_int*energia_porcentaje);
        tope_abajo=(int)(consumo_ano_anterior_int/energia_porcentaje);
        int respuesta=0;

        if(consumo_actual_int<0)
            respuesta=-4;
        else
            if(consumo_actual_int<=60&&consumo_ano_anterior_int<=60)//saco los que son menores a 60
            respuesta=0;
        else
            if(consumo_actual_int<=tope_abajo)
             respuesta=-3;
        else
            if(consumo_actual_int>=tope_arriba)
                respuesta=-2;
        System.out.println(consumo_ano_anterior+"  "+Calculo.getPeriodoAnoAnterior(periodo)+" "+tipo_toma_estado+" "+consumo_ano_anterior_int);
        System.out.println(tope_abajo+"  "+tope_arriba+"  "+consumo_actual_int+"  "+energia_porcentaje);
        //-4 negativo, -3 menor al x procentaje anno ante, -2 mayor a lo esperado
        return respuesta;
    }
    public boolean verificacion_numerica(){
        boolean resultado=true;
        return resultado;
    }
    public boolean verificacion_vuelta_medidor(){
        boolean resulado=true;
        return resulado;
    }
    public void guardar_estado(){
        Bbdd bd=new Bbdd();
        ContentValues registro=new ContentValues();
        String[][] array=new String[10][10];

        array[0][1]="";
        array[1][0]="ruta";
        array[1][1]=ruta;
        array[2][0]="folio";
        array[2][1]=folio;
        array[3][0]=campo_estado;
        array[3][1]=estado_nuevo;
        array[4][0]="geolocalizacion";
        array[4][1]=geolocalizacion;
        registro.put("ruta",Integer.parseInt(ruta));
        registro.put("folio",Integer.parseInt(folio));
        registro.put(campo_estado,Integer.parseInt(estado_nuevo));
        registro.put("fecha_hora",Calculo.getTimeStamp());
        registro.put("observacion",this.observacion);
        registro.put("observacion_sist",this.obserbacion_sist);
        registro.put("geolocalizacion",this.geolocalizacion);
        bd.insertar(context,tabla_toma_estado,registro);
    }
    public void mostrar_tabla(){
        Bbdd bd=new Bbdd();
        bd.consutlar(context,"SELECT * FROM "+tabla_toma_estado);
    }
    public static String getTableStructQuery(String periodo,String tipo_toma_estado){
        return "CREATE TABLE estado_"+periodo+"_"+tipo_toma_estado+" (id integer  primary key AUTOINCREMENT,ruta integer,folio integer,estado integer,fecha_hora text,observacion text,observacion_sist text,geolocalizacion text);";
    }
    public void eliminarEstadosPeriodo(Context context,String periodo){
        Bbdd bd=new Bbdd();
        bd.eliminarTabla(context,"estado_"+periodo+"_energia");
        bd.eliminarTabla(context,"estado_"+periodo+"_agua");
    }
    public String[] importarEstados(Context context,String uri){
        String s="";
        int i=0;
        boolean carga=true;
        boolean fallo=false;
        String fallooutput="";
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
            Bbdd bd=new Bbdd();
            String [][] array=new String[10][10];
            Estado est;
            Periodo per;
            per=new Periodo();
            String str_periodo="";
            String ultima_tabla_e="";
            String ultima_tabla_a="";
            String tabla_toma_estado_e="";
            String tabla_toma_estado_a="";

            while(br.ready()&&carga) {
                s=new String(br.readLine());
                String [] reslt;
                boolean existetabla;

                reslt=s.split(";");

                System.out.println("resl len"+reslt.length);
                System.out.println("line_>"+s.toString());
                    if(reslt.length>14) {

                        if (!Calculo.isRuta(reslt[1])) {
                            carga = false;
                            fallooutput += "Fallo ruta ";
                        }
                        if (!Calculo.isFolio(reslt[2])) {
                            carga = false;
                            fallooutput += "Fallo folio ";
                        }

                        str_periodo = Calculo.convertPeriodoFormatFox(reslt[4]);
                        if (str_periodo == "") {
                            carga = false;
                            fallooutput += "Fallo periodo ";
                        }
                        if (!Calculo.isNumeric(reslt[8])) {
                            carga = false;
                            fallooutput += "Fallo estado_energia ";
                        }
                        if (!Calculo.isNumeric(reslt[12])) {
                            carga = false;
                            fallooutput += "Fallo esado_agua";
                        }
                    }
                    else
                    {
                        carga=false;
                        fallooutput+="Fallo cantidad columnas";
                    }
                    if (carga) {

                        per.guardarPeriodo(context, str_periodo);
                        tabla_toma_estado_e = "estado_" + str_periodo +"_"+"energia";
                        tabla_toma_estado_a = "estado_" + str_periodo +"_"+"agua";
                        if (tabla_toma_estado_e.compareTo(ultima_tabla_e)!=0&&!bd.isExistTabla(context, tabla_toma_estado_e)) {
                            bd.ejecutarConsulta(context, Estado.getTableStructQuery(str_periodo, "energia"));
                            ultima_tabla_e="estado_"+str_periodo+"_"+"energia";
                        }
                        if (tabla_toma_estado_a.compareTo(ultima_tabla_a)!=0&&!bd.isExistTabla(context, tabla_toma_estado_a)) {
                            bd.ejecutarConsulta(context, Estado.getTableStructQuery(str_periodo, "agua"));
                            ultima_tabla_a="estado_"+str_periodo+"_"+"agua";
                        }


                        est = new Estado(context, reslt[1], reslt[2], reslt[8], str_periodo,"energia");
                        est.guardar_estado();
                        est = new Estado(context, reslt[1], reslt[2], reslt[12], str_periodo,"agua");
                        est.guardar_estado();

                    }
                    else
                        fallo=true;

                    i++;

            }

            System.out.println("fin lectura");
        } catch (Exception e) {e.printStackTrace();
                System.out.println("Error al Cargar Archivo de estados");
        }
        String [] res=new String[4];
        if(fallo) {
            res[0] = "1";
            res[1] = fallooutput;
            res[2]=String.valueOf(i);
            res[3]=s;
        }
        else {
            res[0] = "0";
            res[1]="";
        }
        return res;
    }
    public String[] importarEstadosSistemaPropio(Context context,String uri,String tipo_toma_estado) {
        String s="";
        int i=0;
        boolean carga=true;
        boolean fallo=false;
        String fallooutput="";
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
            Bbdd bd=new Bbdd();
            String [][] array=new String[10][10];
            Estado est;
            Periodo per;
            per=new Periodo();
            String str_periodo="";
            String ultima_tabla="";
            s=new String(br.readLine());
            String [] reslt;
            reslt=s.split(",");
            if(reslt.length==1)
                reslt=s.split(";");
            System.out.println(reslt[6]);
            String []tmp=reslt[6].split(" ");
            str_periodo =tmp[1];
            System.out.println(str_periodo);
            while(br.ready()&&carga) {

                s=new String(br.readLine());

                boolean existetabla;


                System.out.println(reslt.length);
                    reslt=s.split(";");
                System.out.println(reslt.length);
                System.out.println("resl len"+reslt.length);
                System.out.println("line_>"+s.toString());
                if(reslt.length>7) {

                    if (!Calculo.isRuta(reslt[0])) {
                        carga = false;
                        fallooutput += "Fallo ruta ";
                    }
                    if (!Calculo.isFolio(reslt[1])) {
                        carga = false;
                        fallooutput += "Fallo folio ";
                    }


                    if (str_periodo == "") {
                        carga = false;
                        fallooutput += "Fallo periodo ";
                    }
                    if (!Calculo.isNumeric(reslt[6])) {
                        if(reslt[6].compareTo("-----")!=0) {
                            carga = false;
                            fallooutput += "Fallo estado";
                        }
                        }

                }
                else
                {
                    carga=false;
                    fallooutput+="Fallo cantidad columnas";
                }
                if (carga) {

                    per.guardarPeriodo(context, str_periodo);
                    tabla_toma_estado = "estado_" + str_periodo +"_"+tipo_toma_estado;;

                    if (tabla_toma_estado.compareTo(ultima_tabla)!=0&&!bd.isExistTabla(context, tabla_toma_estado)) {
                        System.out.println("tabla"+bd.isExistTabla(context, tabla_toma_estado));
                        bd.ejecutarConsulta(context, Estado.getTableStructQuery(str_periodo, tipo_toma_estado));
                        ultima_tabla="estado_"+str_periodo+"_"+tipo_toma_estado;
                    }


                    if(reslt[6].compareTo("-----")!=0) {
                        est = new Estado(context, reslt[0], reslt[1], reslt[6], str_periodo, tipo_toma_estado);
                        est.guardar_estado();
                    }
                }
                else
                    fallo=true;

                i++;

            }
            System.out.println(fallooutput);
            System.out.println("fin lectura");
        } catch (Exception e) {e.printStackTrace();
            System.out.println("Error al Cargar Archivo de estados");
        }
        String [] res=new String[4];
        if(fallo) {
            res[0] = "1";
            res[1] = fallooutput;
            res[2]=String.valueOf(i);
            res[3]=s;
        }
        else {
            res[0] = "0";
            res[1]="";
        }
        return res;
    }

            public File exportarEstados(Context context,String periodo,String tipo_toma_estado){
        FileOutputStream outputStream;
        Bbdd bd=new Bbdd();
        String tabla="estado_"+periodo+"_"+tipo_toma_estado;
        String tabla_e_ant="estado_"+Calculo.getPeriodoAnterior(periodo)+"_"+tipo_toma_estado;
        String tabla_orden="orden_"+tipo_toma_estado;
        String consulta="SELECT usuarios.ruta,usuarios.folio,usuarios.nombre_apellido,usuarios.direccion,"+tabla_orden+".nro_orden,"+tabla_e_ant+".estado,"+tabla+".estado,"+tabla+".observacion,"+tabla+".observacion_sist,"+tabla+".fecha_hora,usuarios.med_"+tipo_toma_estado+ " from usuarios left join "+tabla+" on usuarios.ruta="+tabla+".ruta and usuarios.folio="+tabla+".folio left join "+tabla_e_ant+" on "+tabla+".ruta="+tabla_e_ant+".ruta and "+tabla+".folio="+tabla_e_ant+".folio left join "+tabla_orden+" on usuarios.ruta="+tabla_orden+".ruta and usuarios.folio="+tabla_orden+".folio group by usuarios.id order by usuarios.ruta,usuarios.folio;";
        System.out.println(consulta);
        String string="";
        int i=0;
        String array[][]=bd.consutlar(context,consulta);
        String path=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File tempFile = new File(path+"/export_"+tabla+"-"+Calculo.getDateTimeforFile()+".csv");


        //System.out.println(tempFile.toString());
        try {
            tempFile.createNewFile();
            outputStream= new FileOutputStream(tempFile);

            //outputStream = context.openFileOutput("prueba_export.txt", Context.MODE_PRIVATE);
            string="Ruta;Folio;Nombre y Apellido;Direccion;Orden Toma Estado;Estado "+Calculo.getPeriodoAnterior(periodo)+";Estado "+periodo+";Consumo;Observacion;Obsevacion Sistema;Fecha y Hora\n";
            outputStream.write(string.getBytes());
            outputStream.flush();

            System.out.println(array.length);
            int consumo=0;
            String consumo_str="";
            for(i=0;i<array.length;i++) {
                if(array[i][4]==null)
                array[i][4]="Sin Orden Cargado";
                if(array[i][5]==null)
                array[i][5]="0";
                if(array[i][6]==null)
                array[i][6]="0";

                 consumo=Integer.parseInt(array[i][6])-Integer.parseInt(array[i][5]);
                consumo_str=String.valueOf(consumo);
                if(array[i][9]==null)
                array[i][8]="Estado no Tomado por Usuario";

                if(array[i][10].compareTo("1")!=0) {
                    array[i][5] = "-----";
                    array[i][6] = "-----";
                    array[i][8]="Sin Servicio";
                    array[i][9]="Sin Servicio";
                    consumo_str=  "-----";
                }

                string=array[i][0]+";"+array[i][1]+";"+array[i][2]+";"+array[i][3]+";"+array[i][4]+";"+array[i][5]+";"+array[i][6]+";"+consumo_str+";"+array[i][7]+";"+array[i][8]+";"+array[i][9]+"\n";
                System.out.println(string);

                outputStream.write(string.getBytes());
                outputStream.flush();

            }


            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
        */
        return tempFile;

    }
    public File exportarEstadosAmbosServicios(Context context,String periodo){
        FileOutputStream outputStream;
        Bbdd bd=new Bbdd();
        String tabla_enrgia="estado_"+periodo+"_energia";
        String tabla_energia_ant="estado_"+Calculo.getPeriodoAnterior(periodo)+"_energia";
        String tabla_agua="estado_"+periodo+"_agua";
        String tabla_agua_ant="estado_"+Calculo.getPeriodoAnterior(periodo)+"_agua";
        String consulta="SELECT usuarios.ruta,usuarios.folio,usuarios.nombre_apellido,usuarios.direccion,"+tabla_energia_ant+".estado,"+tabla_enrgia+".estado,"+tabla_enrgia+".observacion,"+tabla_enrgia+".observacion_sist,usuarios.med_energia,"+tabla_agua_ant+".estado,"+tabla_agua+".estado,"+tabla_agua+".observacion,"+tabla_agua+".observacion_sist,usuarios.med_agua,"+tabla_enrgia+".geolocalizacion from usuarios left join "+tabla_enrgia+" on usuarios.ruta="+tabla_enrgia+".ruta and usuarios.folio="+tabla_enrgia+".folio left join "+tabla_energia_ant+" on "+tabla_enrgia+".ruta="+tabla_energia_ant+".ruta and "+tabla_enrgia+".folio="+tabla_energia_ant+".folio left join "+tabla_agua+" on usuarios.ruta="+tabla_agua+".ruta and usuarios.folio="+tabla_agua+".folio left join "+tabla_agua_ant+" on "+tabla_agua+".ruta="+tabla_agua_ant+".ruta and "+tabla_agua+".folio="+tabla_agua_ant+".folio  group by usuarios.id order by usuarios.ruta,usuarios.folio;";
        System.out.println(consulta);
        String string="";
        int i=0;
        String array[][]=bd.consutlar(context,consulta);
        String path=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File tempFile = new File(path+"/export_ambos-"+Calculo.getDateTimeforFile()+".csv");
        String periodo_ant="";
        String periodo_ant_ant="";
        String periodomismo_anioant="";
        String periodomismo_anioant_ant="";
        String [][] res_mesant=new String[1][1];
        String [][] res_anioant=new String[1][1];
        //System.out.println(tempFile.toString());
        try {
            tempFile.createNewFile();
            tempFile.setReadable(true);
            tempFile.setWritable(true);
            System.out.println("thiisss");
            outputStream= new FileOutputStream(tempFile);

            //outputStream = context.openFileOutput("prueba_export.txt", Context.MODE_PRIVATE);
            string="Ruta;Folio;Nombre y Apellido;Direccion;Estado Energia "+Calculo.getPeriodoAnterior(periodo)+";Estado Energia "+periodo+";Consumo Energia;Observacion;Obsevacion Sistema;Consumo mes Anterior;Consumo anio Anterior;Estado Agua "+Calculo.getPeriodoAnterior(periodo)+";Estado Agua "+periodo+";Consumo Agua;Observacion;Obsevacion Sistema;Consumo mes Anterior;Consumo anio Anterior;Geolocalizacion\n";

            outputStream.write(string.getBytes());
            outputStream.flush();

            System.out.println(array.length);
            int consumo=0,consumo2=0;

            String consumo_str="",consumo2_str="";

            periodo_ant=Calculo.getPeriodoAnterior(periodo);
            String consumo_mes_ant_energia="";
            //periodo_ant_ant=Calculo.getPeriodoAnterior(periodo_ant);
            periodomismo_anioant=Calculo.getPeriodoAnoAnterior(periodo);
            String consumo_anio_ant_energia="";
            String consumo_mes_ant_agua="";
            String consumo_anio_ant_agua="";
            //periodomismo_anioant_ant=Calculo.getPeriodoAnterior(periodomismo_anioant);
            for(i=0;i<array.length;i++) {

                if(array[i][4]==null)
                    array[i][4]="0";
                if(array[i][5]==null)
                    array[i][5]="0";
                if(array[i][9]==null)
                    array[i][9]="0";
                if(array[i][10]==null)
                    array[i][10]="0";

                consumo=Integer.parseInt(array[i][5])-Integer.parseInt(array[i][4]);
                consumo_str=String.valueOf(consumo);
                consumo2=Integer.parseInt(array[i][10])-Integer.parseInt(array[i][9]);
                consumo2_str=String.valueOf(consumo2);

                if(array[i][8].compareTo("1")!=0) {
                    array[i][4] = "-----";
                    array[i][5] = "-----";
                    array[i][6]="Sin Servicio";
                    array[i][7]="Sin Servicio";
                    consumo_str=  "-----";
                }
                if(array[i][13].compareTo("1")!=0) {
                    array[i][9] = "-----";
                    array[i][10] = "-----";
                    array[i][11]="Sin Servicio";
                    array[i][12]="Sin Servicio";
                    consumo2_str=  "-----";
                }


                consumo_mes_ant_agua=this.getConsumoXPeriodo(context,periodo_ant,array[i][0],array[i][1],"agua");
                consumo_anio_ant_agua=this.getConsumoXPeriodo(context,periodomismo_anioant,array[i][0],array[i][1],"agua");
                consumo_mes_ant_energia=this.getConsumoXPeriodo(context,periodo_ant,array[i][0],array[i][1],"energia");
                consumo_anio_ant_energia=this.getConsumoXPeriodo(context,periodomismo_anioant,array[i][0],array[i][1],"energia");
                string=array[i][0]+";"+array[i][1]+";"+array[i][2]+";"+array[i][3]+";"+array[i][4]+";"+array[i][5]+";"+consumo_str+";"+array[i][6]+";"+array[i][7]+";"+consumo_mes_ant_energia+";"+consumo_anio_ant_energia+";"+array[i][9]+";"+array[i][10]+";"+consumo2_str+";"+array[i][11]+";"+array[i][12]+";"+consumo_mes_ant_agua+";"+consumo_anio_ant_agua+ ";"+array[i][14]+"\n";
                System.out.println(string);

                outputStream.write(string.getBytes());
                outputStream.flush();

            }


            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
        */
        return tempFile;

    }
    public String getEstadoByPeriodoyRyF(Context context,String periodo,String tipo_toma_estado, String ruta, String folio){
        String consulta="",resultado="0";

        Bbdd bd=new Bbdd();
        consulta="SELECT estado from estado_"+periodo+"_"+tipo_toma_estado+" WHERE ruta="+ruta+" and folio="+folio;
        String[][] array_res=new String[1][1];
        array_res[0][0]=null;
        try {
            array_res = bd.consutlar(context, consulta);
        } catch (Exception e) {e.printStackTrace();
            System.out.println("Error al Cargar Consultar estado");
        }
         if(array_res.length>0)
            resultado=array_res[0][0];

            return resultado;
    }
    public String getConsumoXperiodo(Context context,String periodo,String tipo_toma_estado){
        String consumo="";
        int estado_act=0,estado_ant=0;
        Bbdd bd=new Bbdd();

        if(bd.isExistTabla(context,"estado_"+periodo+"_"+tipo_toma_estado)&&bd.isExistTabla(context,"estado_"+Calculo.getPeriodoAnterior(periodo)+"_"+tipo_toma_estado)) {
            String consulta = "SELECT estado from estado_" + periodo + "_" + tipo_toma_estado + " WHERE ruta=" + ruta + " and folio=" + folio;
            String[][] array_res = bd.consutlar(context, consulta);
            try{
                if (array_res[0][0] != null)
                    estado_act = Integer.valueOf(array_res[0][0]);
            }catch (Exception e){e.printStackTrace();}

            consulta = "SELECT estado from estado_" + Calculo.getPeriodoAnterior(periodo) + "_" + tipo_toma_estado + " WHERE ruta=" + ruta + " and folio=" + folio;
            array_res = bd.consutlar(context, consulta);
            try{
            if (array_res[0][0] != null)
                estado_ant = Integer.valueOf(array_res[0][0]);
            }catch (Exception e){e.printStackTrace();}

            int consumo_int = estado_act - estado_ant;
            consumo = String.valueOf(consumo_int);
        }
        else
        consumo="0";
        return consumo;
    }
    public boolean IsEstadoCargado(Context context,String ruta,String folio,String tipo_toma_estado,String periodo){
        Bbdd bd=new Bbdd();
        boolean result=false;
        String consulta="SELECT COUNT(*) FROM estado_"+periodo+"_"+tipo_toma_estado+" where ruta="+ruta+" and folio="+folio;
        String[][] res_cons=bd.consutlar(context,consulta);

        if(res_cons[0][0].compareTo("0")==0)
        result=true;

        return result;
    }
    public void eliminarEstadoByRyF(Context context,String ruta,String folio,String tipo_toma_estado,String periodo){
        Bbdd bd=new Bbdd();
        String consulta="DELETE FROM estado_"+periodo+"_"+tipo_toma_estado+" WHERE ruta="+ruta+" and folio="+folio;
        bd.consutlar(context,consulta);

    }
    public void setGeolocalizacion(String geologalizacion){
        this.geolocalizacion=geologalizacion;
        //solo cargo la geo en una varitable

    }
    private String  getConsumoXPeriodo(Context context,String periodo,String ruta,String folio,String tipo_toma_estado){
        Bbdd bd=new Bbdd();
        String [][] res=new String[1][1];
        String consulta="";
        String periodo_ant=Calculo.getPeriodoAnterior(periodo);
        try {

            consulta = "SELECT estado_" + periodo + "_"+tipo_toma_estado+".estado-estado_" + periodo_ant + "_"+tipo_toma_estado+".estado FROM estado_" + periodo + "_"+tipo_toma_estado+" LEFT JOIN estado_" + periodo_ant + "_"+tipo_toma_estado+" on estado_" + periodo + "_"+tipo_toma_estado+".ruta=estado_" + periodo_ant + "_"+tipo_toma_estado+".ruta and estado_" + periodo + "_"+tipo_toma_estado+".folio=estado_" + periodo_ant + "_"+tipo_toma_estado+".folio WHERE estado_" + periodo +"_"+tipo_toma_estado+".folio="+folio+" and estado_"+periodo +"_"+tipo_toma_estado+".ruta="+ruta;
            res = bd.consutlar(context, consulta);
        }catch (Exception e){e.printStackTrace();
            res=new String[1][1];
            res[0][0]="no data";
        }
        if(res.length==0){
            res=new String[1][1];
            res[0][0]="no data";
        }
        return res[0][0];
    }
}
