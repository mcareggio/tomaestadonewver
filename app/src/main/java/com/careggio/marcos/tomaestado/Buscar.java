package com.careggio.marcos.tomaestado;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Buscar extends AppCompatActivity {
    ListView lista_encontrados;
    List<Map<String, String>> elementos ;
    private Buscar buscar;
    private SimpleAdapter adaptador1;
    private ListView lista_estados;
    EditText campo_busqueda;
    private boolean admin;
    String str_busqueda,tipo_toma_estado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        buscar=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        campo_busqueda=(EditText)findViewById(R.id.campo_busqueda);
        lista_encontrados = (ListView)findViewById(R.id.lista_encontrados);
        elementos= new ArrayList<Map<String, String>>();
        adaptador1= new SimpleAdapter(this, elementos,
                android.R.layout.simple_list_item_2,
                new String[] {"titulo", "direccion"},
                new int[] {android.R.id.text1,
                        android.R.id.text2});
        lista_encontrados.setAdapter(adaptador1);
        this.tipo_toma_estado=getIntent().getExtras().getString("tipo_toma_estado");
        this.admin=getIntent().getExtras().getBoolean("admin");

        lista_encontrados.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                int item = position;
                Map<String, String> datum=elementos.get(position);
                final String ruta=datum.get("ruta");
                final String folio=datum.get("folio");


                AlertDialog.Builder dialogo_incio = new AlertDialog.Builder(buscar);
                dialogo_incio.setTitle("Opciones");
                dialogo_incio.setMessage("Elija una opcion:");
                dialogo_incio.setPositiveButton("Iniciar Carga de Estado", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialogo_inicio, int position) {
                        Periodo per=new Periodo();
                        String periodo=per.getPeriodoActual(buscar);

                if(periodo.compareTo("00_0000")!=0)
                {
                //Toast.makeText(getApplicationContext(), "Position: "+ item+" - Valor: "+itemval, Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), position+" "+id+" Ruta "+ruta+" "+folio+"seleccionado", Toast.LENGTH_SHORT).show();
                    Intent i;
                    i = new Intent(buscar, Recorrido.class);
                    //this.tipo_toma_estado="energia";
                    i.putExtra("tipo_toma_estado", tipo_toma_estado);
                    i.putExtra("ruta",ruta);
                    i.putExtra("folio",folio);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "No selecciono ningun periodo", Toast.LENGTH_LONG).show();
                }
                    }
                });

                dialogo_incio.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {

                    }
                });
                dialogo_incio.setNegativeButton("Listado de Estados", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                    Intent i=new Intent(buscar,ListadoEstados.class);
                    i.putExtra("tipo_toma_estado", tipo_toma_estado);
                    i.putExtra("ruta",ruta);
                    i.putExtra("folio",folio);
                    startActivity(i);

                    }
                });
                
                    dialogo_incio.show();



            }

        });
        lista_encontrados.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Periodo per=new Periodo();
                final String periodo=per.getPeriodoActual(buscar);

                if(periodo.compareTo("00_0000")!=0) {
                    Map<String, String> datum = elementos.get(i);
                    final String ruta = datum.get("ruta");
                    final String folio = datum.get("folio");
                    final Estado est = new Estado();


                    if (est.IsEstadoCargado(buscar, ruta, folio, tipo_toma_estado, periodo) != true) {

                        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(buscar);
                        dialogo1.setTitle("Advertencia");
                        dialogo1.setMessage("El estado de (" + datum.get("titulo") + " " + datum.get("direccion") + ")  Ya fue cargado, desea eliminarlo ?");
                        dialogo1.setCancelable(false);

                        dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo1, int id) {
                                est.eliminarEstadoByRyF(buscar, ruta, folio, tipo_toma_estado, periodo);
                                Toast.makeText(getApplicationContext(), "Estado Eliminado", Toast.LENGTH_SHORT).show();

                            }
                        });

                        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo1, int id) {

                            }
                        });

                        dialogo1.show();

                    }

                }
                else
                    Toast.makeText(getApplicationContext(), "No selecciono ningun periodo", Toast.LENGTH_LONG).show();
                return true;}
            });
        campo_busqueda.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                System.out.println(s);
                cargarEncontrados();
            }
        });

    }
    public void cargarEncontrados(){
        elementos.clear();

        Usuario usr=new Usuario();
        str_busqueda=this.campo_busqueda.getText().toString();
        String [][] array_encontrados=usr.buscarUsuarios(this,str_busqueda,tipo_toma_estado);

        for(int i=0;i<array_encontrados.length;i++){
            Map<String, String> datum = new HashMap<String, String>(3);
            datum.put("titulo", array_encontrados[i][0]);
            datum.put("direccion", array_encontrados[i][1]+" Nro Med:"+array_encontrados[i][2]);
            datum.put("ruta", array_encontrados[i][3]);
            datum.put("folio", array_encontrados[i][4]);
            elementos.add(datum);
            System.out.println("Cargando elemento "+array_encontrados[i][0]);

        }

        adaptador1= new SimpleAdapter(this, elementos,
                android.R.layout.simple_list_item_2,
                new String[] {"titulo", "direccion"},
                new int[] {android.R.id.text1,
                        android.R.id.text2});
        lista_encontrados.setAdapter(adaptador1);
    }
}
