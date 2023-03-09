package com.careggio.marcos.tomaestado;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Configuracion extends AppCompatActivity implements View.OnClickListener{
    private ArrayList<String> periodos;
    //String [] periodos;
    private ArrayAdapter<String> adaptador1;
    private ListView lista_periodos;
    private Button nuevo_periodo_bnt;
    private TextView periodo_actual;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        lista_periodos=(ListView)findViewById(R.id.lista_periodos);
        periodo_actual=(TextView)findViewById(R.id.periodo_actual);


        periodos=new ArrayList<String>();

        adaptador1=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,periodos);
        lista_periodos.setAdapter(adaptador1);
        this.cargarPeriodos();

        context=this;

        nuevo_periodo_bnt=(Button)findViewById(R.id.nuevo_periodo);
        nuevo_periodo_bnt.setOnClickListener(this);
        Periodo per=new Periodo();
        periodo_actual.setText("Periodo Actual ("+per.getPeriodoActual(context)+")");
        lista_periodos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int posicion=i;
                final String itemval = (String)lista_periodos.getItemAtPosition(i);
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(context);
                dialogo1.setTitle("Importante");
                dialogo1.setMessage("¿ Elimina este periodo ?");
                dialogo1.setCancelable(false);

                dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        Periodo per=new Periodo();
                        Toast.makeText(getApplicationContext(), "Periodo "+itemval+" eliminado", Toast.LENGTH_SHORT).show();
                        if(itemval.compareTo(per.getPeriodoActual(context))==0)
                            periodo_actual.setText("Periodo Actual ()");
                        per.eliminar(context,itemval);
                        cargarPeriodos();
                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                    }
                });
                dialogo1.show();

                return false;
            }
        });
        lista_periodos.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                int item = position;
                String itemval = (String)lista_periodos.getItemAtPosition(position);
                //Toast.makeText(getApplicationContext(), "Position: "+ item+" - Valor: "+itemval, Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Periodo "+itemval+" seleccionado", Toast.LENGTH_SHORT).show();
                Periodo per=new Periodo();
                per.setPeriodoActual(context,itemval);
                periodo_actual.setText("Periodo Actual ("+per.getPeriodoActual(context)+")");
            }

        });






    }


    public void cargarPeriodos(){
        periodos.clear();
        //periodos=new ArrayList<String>();
        Periodo per=new Periodo();
        String [][] array_periodos=per.getPeriodosArray(this);

        for(int i=0;i<array_periodos.length;i++){
            periodos.add(array_periodos[i][1]);
            System.out.println("Cargando periodo a la lista "+array_periodos[i][1]);

        }
        adaptador1=new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,periodos);
        lista_periodos.setAdapter(adaptador1);
    }

    public void onClick(View view) {
    if(view.getId()==R.id.nuevo_periodo){
        Periodo per=new Periodo();
        per.nuevoPeriodo(this,"03_2018");
        this.cargarPeriodos();
    }
    }
}
