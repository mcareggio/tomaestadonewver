package com.careggio.marcos.tomaestado;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class Actualizar_Datos extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private boolean abrir_cargador_archivos;
    private int tipo_boton;
    private static final int REQUEST_INTERNET = 1;
    private static String[] PERMISSIONS_INTERNET = {
            Manifest.permission.INTERNET,

    };
    private int [] iditems;
    private Usuario usr;
    private  ProgressDialog progressDialog;
    private Actualizar_Datos actualizar_datos;
    private Button cargar_archivo_actualizaciones,quitar_usuarios,actualizar_estados_agua,actualizar_estados_energia,actualizar_orden_agua,actualizar_orden_energia;
    private Uri uri;
    private int VALOR_RETORNO = 1;
    private String tipo_toma_estado;
    Actualizar_Datos act;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar__datos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Actualizar_Datos.verifyStoragePermissions(this);
        cargar_archivo_actualizaciones=(Button)findViewById(R.id.cargar_archivo_actualizaciones);
        cargar_archivo_actualizaciones.setOnClickListener(this);
        quitar_usuarios=(Button)findViewById(R.id.quitar_usuarios);
        quitar_usuarios.setOnClickListener(this);
        actualizar_estados_agua=(Button)findViewById(R.id.actualizar_estados_agua);
        actualizar_estados_agua.setOnClickListener(this);
        actualizar_estados_energia=(Button)findViewById(R.id.actualizar_estados_energia);
        actualizar_estados_energia.setOnClickListener(this);
        actualizar_orden_agua=(Button)findViewById(R.id.actualizar_orden_agua);
        actualizar_orden_agua.setOnClickListener(this);
        actualizar_orden_energia=(Button)findViewById(R.id.actualizar_orden_energia);
        actualizar_orden_energia.setOnClickListener(this);
        act=this;

    }
    public void onClick(View view) {
        abrir_cargador_archivos=false;
        switch (view.getId()) {

            case R.id.quitar_usuarios:
                this.mostrarDialogoConElementosAEliminar();
                break;

            case R.id.cargar_archivo_actualizaciones:
                tipo_boton=1;
                abrir_cargador_archivos=true;
                break;
            case R.id.actualizar_estados_agua:
                tipo_boton=2;
                abrir_cargador_archivos=true;
                break;
            case R.id.actualizar_estados_energia:
                tipo_boton=3;
                abrir_cargador_archivos=true;
                break;
            case R.id.actualizar_orden_agua:
                tipo_boton=4;
                abrir_cargador_archivos=true;
                break;
            case R.id.actualizar_orden_energia:
                tipo_boton=5;
                abrir_cargador_archivos=true;
                break;





        }
        if(abrir_cargador_archivos){
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(Intent.createChooser(intent, "Choose File"), VALOR_RETORNO);

        }

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            //Cancelado por el usuario
        }
        if ((resultCode == RESULT_OK) && (requestCode == VALOR_RETORNO)) {

            String[] res;
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Cargando..."); // Setting Message
            progressDialog.setTitle("Cargando Archivo"); // Setting Title
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
            // Display Progress Dialog
            progressDialog.setCancelable(false);

            progressDialog.show();
            actualizar_datos=this;
            //Intent data2=data;
            uri = data.getData();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Looper.prepare();
                         //obtener el uri content
            System.out.println("Antes del switch" + tipo_boton);
            switch(tipo_boton) {
                case 1:
                ActualizarRutayFolio actryf = new ActualizarRutayFolio();
                actryf.cargarTablaTemporal(actualizar_datos, Calculo.getPath(actualizar_datos, uri));
                actryf.actualizarNombreDireNrommed(actualizar_datos);
                actryf.cargarLosQueEstanEnFoxYnNoAca(actualizar_datos);
                    break;

                case 2:
                    tipo_toma_estado="agua";
                    break;
                case 3:
                    tipo_toma_estado="energia";
                    break;



            }           if(tipo_boton==2||tipo_boton==3) {
                            Periodo per = new Periodo();
                            String periodo = per.getPeriodoActual(actualizar_datos);
                            ActualizarEstados act_est = new ActualizarEstados();
                            act_est.actualizarEstados(actualizar_datos, Calculo.getPath(actualizar_datos, uri), periodo, tipo_toma_estado);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();

                }
            }).start();
        }
    }
        public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permisioninternet= ActivityCompat.checkSelfPermission(activity,Manifest.permission.INTERNET);
        if(permisioninternet!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,PERMISSIONS_INTERNET,REQUEST_INTERNET);
        }
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    public void mostrarDialogoConElementosAEliminar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        LinearLayout ll=new LinearLayout(this);
        ScrollView sv = new ScrollView(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        sv.addView(ll);
        builder.setView(sv);
        actualizar_datos=this;
        ActualizarRutayFolio actryf=new ActualizarRutayFolio();
        String[][] res=actryf.obtenerUsuariosAEliminar(this);
        final CheckBox [] checkBoxesarr=new CheckBox[res.length];
        for(int i=0;i<res.length;i++) {
          //  System.out.println(res[i][3]+" "+res[i][4]);
            checkBoxesarr[i]=new CheckBox(this);
            checkBoxesarr[i].setText(res[i][1]+"-"+res[i][2]+" "+res[i][3]+" "+res[i][4]);
            checkBoxesarr[i].setId(Integer.valueOf(res[i][0]));
            ll.addView(checkBoxesarr[i]);
        }

       // estado_conf = (EditText) dialogview.findViewById(R.id.estado_confirm);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                usr=new Usuario();
               int j=0;
                for (int i=0;i<checkBoxesarr.length;i++){
                    if(checkBoxesarr[i].isChecked())
                    j++;
                }
                System.out.println("j---->"+j);
                iditems=new int [j];
                j=0;
                for (int i=0;i<checkBoxesarr.length;i++){

                        if(checkBoxesarr[i].isChecked()) {
                            iditems[j] = checkBoxesarr[i].getId();
                            j++;
                        }
                    }

                progressDialog = new ProgressDialog(actualizar_datos);
                progressDialog.setMessage("Eliminando..."); // Setting Message
                progressDialog.setTitle("Eliminando"); // Setting Title
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                // Display Progress Dialog
                progressDialog.setCancelable(false);

                progressDialog.show();

                //Intent data2=data;

                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Looper.prepare();
                usr.eliminarUsuariosById(act,iditems);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();

                    }
                }).start();

            }
        })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        }
                );
        builder.create();
        builder.show();
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
