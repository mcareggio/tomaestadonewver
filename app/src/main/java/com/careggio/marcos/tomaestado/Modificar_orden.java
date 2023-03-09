package com.careggio.marcos.tomaestado;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
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

public class Modificar_orden extends AppCompatActivity {
    ListView listado_en_orden;
    List<Map<String, String>> elementos ;
    private Modificar_orden modificar_orden;
    private SimpleAdapter adaptador1;
    EditText campo_busqueda;
     boolean item_seleccionado=false;
     String ruta_anterior="";
     String folio_anterior="";
    String str_busqueda,tipo_toma_estado;
    ProgressDialog progressDialog;
    String [][] array_encontrados;
    String ruta_para_agregar="";
    String folio_para_agregar="";

    Boolean item_para_agregar_orden;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        modificar_orden = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_orden);
        setTitle("Modificar Orden");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(modificar_orden,Listado_sin_orden.class);
                i.putExtra("tipotomaestado",tipo_toma_estado);
                startActivity(i);
                modificar_orden.finish();

            }
        });


        campo_busqueda = (EditText) findViewById(R.id.campo_busqueda_mover);
        listado_en_orden = (ListView) findViewById(R.id.listado_orden);
        elementos = new ArrayList<Map<String, String>>();
        adaptador1 = new SimpleAdapter(this, elementos,
                android.R.layout.simple_list_item_2,
                new String[]{"titulo", "direccion"},
                new int[]{android.R.id.text1,
                        android.R.id.text2});
        listado_en_orden.setAdapter(adaptador1);
        item_para_agregar_orden=false;
        final AlertDialog.Builder dialogo = new AlertDialog.Builder(modificar_orden);
        dialogo.setTitle("Elegir un Tipo de Toma Estado");
        dialogo.setCancelable(false);

        dialogo.setPositiveButton("Energia", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
            tipo_toma_estado="energia";
                cargarListado();
            }
        });
        dialogo.setNegativeButton("Agua", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                tipo_toma_estado="agua";
                cargarListado();
            }
        });
        if(getIntent().getExtras().getString("tipotomaestado").compareTo("")!=0) {
            tipo_toma_estado = getIntent().getExtras().getString("tipotomaestado");
            if(getIntent().getExtras().getString("agregarnuevo").compareTo("1")==0){
                item_para_agregar_orden=true;
                ruta_para_agregar=getIntent().getExtras().getString("ruta");
                folio_para_agregar=getIntent().getExtras().getString("folio");

            }
            cargarListado();
        }
        else{
            dialogo.show();
        }





        listado_en_orden.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(item_para_agregar_orden){
                    Map<String, String> datum = elementos.get(i);
                    final String ruta_ant = datum.get("ruta");
                    final String folio_ant = datum.get("folio");
                    AlertDialog.Builder dialogoagregarOrdenNuevo= new AlertDialog.Builder(modificar_orden);
                    dialogoagregarOrdenNuevo.setTitle("Advertencia!");
                    dialogoagregarOrdenNuevo.setMessage("Desea Agregar Orden Nuevo?");
                    dialogoagregarOrdenNuevo.setPositiveButton("Confirmar",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            progressDialog = new ProgressDialog(modificar_orden);
                            progressDialog.setMessage("Cargando..."); // Setting Message
                            progressDialog.setTitle("Indexando"); // Setting Title
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                            // Display Progress Dialog
                            progressDialog.setCancelable(false);
                            campo_busqueda.setText("");
                            progressDialog.show();

                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        Looper.prepare();
                                        Orden_usuario ord_usr=new Orden_usuario();
                                        ord_usr.ponerdebajo_ordenNuevo(modificar_orden,ruta_para_agregar,folio_para_agregar,ruta_ant,folio_ant,tipo_toma_estado);
                                        Intent refresh = new Intent(modificar_orden, Modificar_orden.class);
                                        refresh.putExtra("tipotomaestado",tipo_toma_estado);
                                        refresh.putExtra("agregarnuevo","0");
                                        startActivity(refresh);
                                        modificar_orden.finish();
                                        Toast.makeText(getApplicationContext(), "Orden Modificado", Toast.LENGTH_SHORT).show();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    progressDialog.dismiss();
                                    //cargarListado();//fallaaaaaaaaaaaaaaa
                                }
                            }).start();

                        }
                    });
                    dialogoagregarOrdenNuevo.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            item_para_agregar_orden=false;
                        }
                    });
                    dialogoagregarOrdenNuevo.show();
                }
            }


        });

        listado_en_orden.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override

            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(modificar_orden);
                Map<String, String> datum = elementos.get(i);
                final String ruta = datum.get("ruta");
                final String folio = datum.get("folio");

                if(item_seleccionado){

                    {
                        dialogo1.setTitle("Advertencia");
                        dialogo1.setMessage("Cambiar orden de " + ruta_anterior + "-" + folio_anterior + " debajo de " + ruta + "-" + folio);
                        dialogo1.setCancelable(false);

                    }
                }
                else {
                    ruta_anterior=ruta;
                    folio_anterior=folio;
                    dialogo1.setTitle("Advertencia");
                    dialogo1.setMessage("Desea Cambiar el Orden de Toma Estado?");
                    dialogo1.setCancelable(false);

                }
                dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        AlertDialog.Builder dialogo2 = new AlertDialog.Builder(modificar_orden);
                        if(item_seleccionado)
                        {
                         item_seleccionado=false;
                            if(!(ruta.compareTo(ruta_anterior)==0&&folio.compareTo(folio_anterior)==0)) {
                                //modificar orden

                                progressDialog = new ProgressDialog(modificar_orden);
                                progressDialog.setMessage("Cargando..."); // Setting Message
                                progressDialog.setTitle("Indexando"); // Setting Title
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                                 // Display Progress Dialog
                                progressDialog.setCancelable(false);
                                campo_busqueda.setText("");
                                progressDialog.show();

                                new Thread(new Runnable() {
                                    public void run() {
                                        try {
                                            Looper.prepare();
                                            Orden_usuario ord_usr=new Orden_usuario();
                                            ord_usr.ponerdebajo_orden(modificar_orden,ruta_anterior,folio_anterior,ruta,folio,tipo_toma_estado);
                                            Intent refresh = new Intent(modificar_orden, Modificar_orden.class);
                                            refresh.putExtra("tipotomaestado",tipo_toma_estado);
                                            refresh.putExtra("agregarnuevo","0");
                                            startActivity(refresh);
                                            modificar_orden.finish();
                                            Toast.makeText(getApplicationContext(), "Orden Modificado", Toast.LENGTH_SHORT).show();

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        progressDialog.dismiss();
                                        //cargarListado();//fallaaaaaaaaaaaaaaa
                                    }
                                }).start();
                                //progressDialog.dismiss();



                            }

                        }else
                        {
                            item_seleccionado = true;
                            dialogo2.setTitle("Advertencia");
                            dialogo2.setMessage("Seleccione el item posterior donde ubicar el orden");
                            dialogo2.show();
                        }
                        //

                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {

                    }
                });
                dialogo1.setNeutralButton("Eliminar Orden", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        progressDialog = new ProgressDialog(modificar_orden);
                        progressDialog.setMessage("Cargando..."); // Setting Message
                        progressDialog.setTitle("Indexando"); // Setting Title
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                        // Display Progress Dialog
                        progressDialog.setCancelable(false);
                        campo_busqueda.setText("");
                        progressDialog.show();

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    Looper.prepare();
                                    Orden_usuario ord_usr=new Orden_usuario();
                                    ord_usr.eliminarOrden(modificar_orden,ruta,folio,tipo_toma_estado);
                                    Intent refresh = new Intent(modificar_orden, Modificar_orden.class);
                                    refresh.putExtra("tipotomaestado",tipo_toma_estado);
                                    refresh.putExtra("agregarnuevo","0");
                                    startActivity(refresh);
                                    modificar_orden.finish();
                                    Toast.makeText(getApplicationContext(), "Orden Modificado", Toast.LENGTH_SHORT).show();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                progressDialog.dismiss();
                                //cargarListado();//fallaaaaaaaaaaaaaaa
                            }
                        }).start();
                    }
                });
                dialogo1.show();

                return true;
            }
        });

        campo_busqueda.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                System.out.println(s);
                cargarListado();
            }
        });
    }

    private void cargarListado(){
        elementos.clear();
        //Orden_usuario orden_usuario=new Orden_usuario();
        //
        Usuario usr=new Usuario();
        str_busqueda=campo_busqueda.getText().toString();
        array_encontrados=usr.buscarUsuarios(this,str_busqueda,tipo_toma_estado);



        //String [][] array_encontrados=orden_usuario.getListadoCompletoOrden(this,tipo_toma_estado);

        for(int i=0;i<array_encontrados.length;i++){
            Map<String, String> datum = new HashMap<String, String>(3);
            datum.put("titulo", array_encontrados[i][0]+ "(Orden:"+array_encontrados[i][5]+")");
            datum.put("direccion", array_encontrados[i][1]+" R y F:("+array_encontrados[i][3]+"-"+array_encontrados[i][4]+")");
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
        listado_en_orden.setAdapter(adaptador1);
    }
}
