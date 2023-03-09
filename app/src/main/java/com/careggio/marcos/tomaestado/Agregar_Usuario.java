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

public class Agregar_Usuario extends AppCompatActivity{
    Agregar_Usuario agregar_usuario;
    ListView listado_en_orden;
    List<Map<String, String>> elementos ;
    private Modificar_orden modificar_orden;
    private SimpleAdapter adaptador1;
    EditText campo_busqueda;
    boolean item_seleccionado=false;

    String str_busqueda;
    ProgressDialog progressDialog;
    String [][] array_encontrados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar__usuario);
        //setContentView(R.layout.view_nuevo_usuario);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        agregar_usuario=this;


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             Intent i=new Intent(agregar_usuario,Agregar_usr.class);
             i.putExtra("modificar","0");
             startActivity(i);
             agregar_usuario.finish();
            }
        });

        campo_busqueda = (EditText) findViewById(R.id.campo_busqueda_agr_usr);
        listado_en_orden = (ListView) findViewById(R.id.listado_orden_agr_usr);
        elementos = new ArrayList<Map<String, String>>();
        adaptador1 = new SimpleAdapter(this, elementos,
                android.R.layout.simple_list_item_2,
                new String[]{"titulo", "direccion y RyF"},
                new int[]{android.R.id.text1,
                        android.R.id.text2});
        listado_en_orden.setAdapter(adaptador1);
        cargarListado();

        listado_en_orden.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override

            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(agregar_usuario);
                Map<String, String> datum = elementos.get(i);
                final String nombreyapellido = datum.get("titulo");
                final String direccion = datum.get("direccion");
                final String ruta = datum.get("ruta");
                final String folio = datum.get("folio");
                final String nro_med_energia = datum.get("nro_med_energia");
                final String nro_med_agua = datum.get("nro_med_agua");
                final String med_energia = datum.get("med_energia");
                final String med_agua = datum.get("med_agua");
                dialogo1.setTitle("Seleccione una accion");
                dialogo1.setPositiveButton("Eliminar Usuario", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {


                        progressDialog = new ProgressDialog(agregar_usuario);
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
                                    Usuario usr=new Usuario();
                                    usr.eliminarUsuario(agregar_usuario,ruta,folio);
                                    Intent refresh = new Intent(agregar_usuario, Agregar_Usuario.class);
                                    startActivity(refresh);
                                    agregar_usuario.finish();
                                    Toast.makeText(getApplicationContext(), "Usuario Eliminado", Toast.LENGTH_SHORT).show();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                progressDialog.dismiss();

                            }
                        }).start();

                    }
                });
                dialogo1.setNeutralButton("Modificar Usuario", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        Intent editusr = new Intent(agregar_usuario, Agregar_usr.class);
                        editusr.putExtra("modificar","1");
                        editusr.putExtra("nombreyapellido",nombreyapellido);
                        editusr.putExtra("direccion",direccion);
                        editusr.putExtra("ruta",ruta);
                        editusr.putExtra("folio",folio);
                        editusr.putExtra("nro_med_energia",nro_med_energia);
                        editusr.putExtra("nro_med_agua",nro_med_agua);
                        editusr.putExtra("med_energia",med_energia);
                        editusr.putExtra("med_agua",med_agua);

                        startActivity(editusr);
                        agregar_usuario.finish();

                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {

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
        array_encontrados=usr.buscarUsuariosTodos(this,str_busqueda);



        //String [][] array_encontrados=orden_usuario.getListadoCompletoOrden(this,tipo_toma_estado);

        for(int i=0;i<array_encontrados.length;i++){
            Map<String, String> datum = new HashMap<String, String>(3);
            datum.put("titulo", array_encontrados[i][0]);
            datum.put("direccion y RyF", array_encontrados[i][1]+" R y F:("+array_encontrados[i][2]+"-"+array_encontrados[i][3]+")");
            datum.put("direccion", array_encontrados[i][1]);
            datum.put("ruta", array_encontrados[i][2]);
            datum.put("folio", array_encontrados[i][3]);
            datum.put("nro_med_energia", array_encontrados[i][4]);
            datum.put("nro_med_agua", array_encontrados[i][5]);
            datum.put("med_energia", array_encontrados[i][6]);
            datum.put("med_agua", array_encontrados[i][7]);
            elementos.add(datum);
            System.out.println("Cargando elemento "+array_encontrados[i][0]);

        }

        adaptador1= new SimpleAdapter(this, elementos,
                android.R.layout.simple_list_item_2,
                new String[] {"titulo", "direccion y RyF"},
                new int[] {android.R.id.text1,
                        android.R.id.text2});
        listado_en_orden.setAdapter(adaptador1);
    }


}
