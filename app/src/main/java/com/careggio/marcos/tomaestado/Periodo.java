package com.careggio.marcos.tomaestado;

import android.content.Context;

/**
 * Created by marcos on 3/4/2018.
 */

public class Periodo {
    public static String getTableStructQuery(){
        String consulta="CREATE TABLE periodos (id integer  primary key AUTOINCREMENT,periodo text,actual boolean,mes integer,ano integer);";
        return consulta;
    }
    public String[][] getPeriodosArray(Context context){
        Bbdd bd=new Bbdd();
        String consulta="SELECT * from periodos where 1 order by ano desc,mes desc;";
        String [][] array=bd.consutlar(context,consulta);
        return array;
    }
    public void nuevoPeriodo(Context context,String def_periodo){
        Bbdd bd=new Bbdd();
        String [][] array=new String[4][4];
        int mes=0,ano=0;
        String [][] array_result=bd.consutlar(context,"SELECT periodo from periodos where 1 order by ano desc,mes desc;");

        if(array_result.length>0) {
            String x = new String(array_result[0][0]);

            String[] array_mm_aaaa = x.split("_");

            mes= Integer.parseInt(array_mm_aaaa[0]);
            ano= Integer.parseInt(array_mm_aaaa[1]);
            mes++;

            String periodo = "";
            if (mes > 12) {
                periodo = "01_" + String.valueOf((ano + 1));

            } else {
                if(mes<10)
                periodo = "0"+String.valueOf(mes) + "_" + String.valueOf((ano));
                else
                    periodo = String.valueOf(mes) + "_" + String.valueOf((ano));
            }
            System.out.println("resss "+periodo);
            array[0][0] = "periodo";
            array[0][1] = periodo;
        }
        else {
            array[0][0] = "periodo";
            array[0][1] = def_periodo;
            String mya[]=def_periodo.split("_");
            mes=Integer.parseInt(mya[0]);
            ano=Integer.parseInt(mya[1]);
        }

        array[1][0]="mes";
        array[1][1]=String.valueOf(mes);
        array[2][0]="ano";
        array[2][1]=String.valueOf(ano);
        bd.insertar(context,"periodos",array);
        bd.ejecutarConsulta(context,Estado.getTableStructQuery(array[0][1],"energia"));
        bd.ejecutarConsulta(context,Estado.getTableStructQuery(array[0][1],"agua"));

    }
    public void guardarPeriodo(Context context,String periodo){
        Bbdd bd=new Bbdd();
        String array[][]=new String[4][4];
        array[0][0]="periodo";
        array[0][1]=periodo;

        String [] aym=periodo.split("_");
        array[1][0]="mes";
        array[1][1]=aym[0];
        array[2][0]="ano";
        array[2][1]=aym[1];
        String [][] res_ex_per=bd.consutlar(context,"SELECT COUNT(*) FROM periodos where periodo='"+periodo+"'");
        if(res_ex_per[0][0].compareTo("0")==0)
        bd.insertar(context,"periodos",array);

    }
    public void setPeriodoActual(Context context,String periodo_nuevo) {
        Bbdd bd=new Bbdd();
        String[][] array=new String[2][2];
        array[0][0]="actual";
        array[0][1]="0";
        bd.actualizar(context,"periodos","1",array);
        array[0][0]="actual";
        array[0][1]="1";
        bd.actualizar(context,"periodos","periodo='"+periodo_nuevo+"'",array);

    }
    public String getPeriodoActual(Context context){
        Bbdd bd=new Bbdd();

        String arg_rest[][]=bd.consutlar(context,"SELECT periodo FROM periodos where actual=1");
        String res="";

        if(arg_rest.length>0)
            res=arg_rest[0][0];
        else
            res="00_0000";
        return res;

    }

    public void eliminar(Context context, String tabla) {
        Bbdd bd=new Bbdd();

        bd.eliminar(context,"periodos","periodo='"+tabla+"'");
        Estado est=new Estado();
        est.eliminarEstadosPeriodo(context,tabla);
    }

}
