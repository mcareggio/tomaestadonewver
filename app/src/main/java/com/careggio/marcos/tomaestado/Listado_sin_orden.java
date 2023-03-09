package com.careggio.marcos.tomaestado;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Listado_sin_orden extends AppCompatActivity {
    Listado_sin_orden listado_sin_orden;
    ListView listview_sin_orden;
    List<Map<String, String>> elementos ;
    String tipo_toma_estado;
    private Modificar_orden modificar_orden;
    private SimpleAdapter adaptador1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_sin_orden);
        listado_sin_orden = this;
        listview_sin_orden = (ListView) findViewById(R.id.listado_sin_orden_usr);
        elementos = new ArrayList<Map<String, String>>();
        adaptador1 = new SimpleAdapter(this, elementos,
                android.R.layout.simple_list_item_2,
                new String[]{"titulo", "direccion"},
                new int[]{android.R.id.text1,
                        android.R.id.text2});
        listview_sin_orden.setAdapter(adaptador1);
        tipo_toma_estado = getIntent().getExtras().getString("tipotomaestado");
        cargarListado();

        listview_sin_orden.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override

            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String, String> datum = elementos.get(i);
                final String ruta = datum.get("ruta");
                final String folio = datum.get("folio");

                Intent i1 = new Intent(listado_sin_orden, Modificar_orden.class);

                i1.putExtra("tipotomaestado", tipo_toma_estado);
                i1.putExtra("agregarnuevo", "1");
                i1.putExtra("ruta", ruta);
                i1.putExtra("folio", folio);
                startActivity(i1);
                listado_sin_orden.finish();
                return false;


            }
        });
    }
    private void cargarListado(){
        elementos.clear();

        Orden_usuario ord=new Orden_usuario();
        String [][] array_encontrados=ord.revisarSiTodosTienenOrden(listado_sin_orden,tipo_toma_estado);


        for(int i=0;i<array_encontrados.length;i++){
            Map<String, String> datum = new HashMap<String, String>(3);
            datum.put("titulo", array_encontrados[i][3]);
            datum.put("direccion", array_encontrados[i][4]+" R y F:("+array_encontrados[i][1]+"-"+array_encontrados[i][2]+")");
            datum.put("ruta", array_encontrados[i][1]);
            datum.put("folio", array_encontrados[i][2]);
            elementos.add(datum);
            System.out.println("Cargando elemento "+array_encontrados[i][0]);

        }

        adaptador1= new SimpleAdapter(this, elementos,
                android.R.layout.simple_list_item_2,
                new String[] {"titulo", "direccion"},
                new int[] {android.R.id.text1,
                        android.R.id.text2});
        listview_sin_orden.setAdapter(adaptador1);
    }

}
