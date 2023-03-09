package com.careggio.marcos.tomaestado;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Cargar_ruta_folio extends AppCompatActivity implements View.OnClickListener{
    private String tipo_toma_estado;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_INTERNET = 1;
    private static String[] PERMISSIONS_INTERNET = {
            Manifest.permission.INTERNET,

    };
    private int VALOR_RETORNO = 1;
    Button cargar_archivo_ryf,cargar_archivo_estados,exportar_estados,exportar_estados_agua,cargar_orden_agua,cargar_orden_energia;
    Button import_est_propio;
    private String tipo_boton="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargar_ruta_folio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Cargar_ruta_folio.verifyStoragePermissions(this);
        cargar_archivo_ryf=(Button)findViewById(R.id.cargar_archivo_ryf);
        cargar_archivo_ryf.setOnClickListener(this);
        cargar_archivo_estados=(Button)findViewById(R.id.cargar_archivo_estados);
        cargar_archivo_estados.setOnClickListener(this);
        exportar_estados=(Button)findViewById(R.id.exportar_estados);
        exportar_estados.setOnClickListener(this);
        exportar_estados_agua=(Button)findViewById(R.id.exportar_estados_agua);
        exportar_estados_agua.setOnClickListener(this);
        cargar_orden_agua=(Button)findViewById(R.id.cargar_orden_agua);
        cargar_orden_agua.setOnClickListener(this);
        cargar_orden_energia=(Button)findViewById(R.id.cargar_orden_energia);
        cargar_orden_energia.setOnClickListener(this);
        import_est_propio=(Button)findViewById(R.id.import_est_propio);
        import_est_propio.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cargar_archivo_ryf:
                tipo_boton="boton_rf";
                Intent intent;
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "Choose File"), VALOR_RETORNO);
            break;
            case R.id.cargar_archivo_estados:
                tipo_boton="boton_estados";
                Intent intent2;
                intent2 = new Intent(Intent.ACTION_GET_CONTENT);
                intent2.setType("*/*");
                startActivityForResult(Intent.createChooser(intent2, "Choose File"), VALOR_RETORNO);
                break;
            case R.id.cargar_orden_agua:
                tipo_boton="cargar_orden_agua";
                Intent intent3;
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "Choose File"), VALOR_RETORNO);
                break;
            case R.id.cargar_orden_energia:
                tipo_boton="cargar_orden_energia";
                Intent intent4;
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "Choose File"), VALOR_RETORNO);
                break;
            case R.id.exportar_estados:
                Estado est=new Estado();
                Periodo per=new Periodo();
                est.exportarEstados(this,per.getPeriodoActual(this),"energia");
                Toast.makeText(this,"Exportacion terminada! Archivo guardado en Descargas", Toast.LENGTH_LONG);
                Intent i12 = new Intent(Intent.ACTION_GET_CONTENT);
                Uri uri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
                i12.setDataAndType(uri, "*/*");
                this.startActivity(Intent.createChooser(i12, "Open folder"));
            break;
            case R.id.exportar_estados_agua:
                Estado est2=new Estado();
                Periodo per2=new Periodo();
                est2.exportarEstados(this,per2.getPeriodoActual(this),"agua");
                Toast.makeText(this,"Exportacion terminada! Archivo guardado en Descargas", Toast.LENGTH_LONG);
                Intent i13 = new Intent(Intent.ACTION_GET_CONTENT);
                Uri uri2 = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
                i13.setDataAndType(uri2, "*/*");
                this.startActivity(Intent.createChooser(i13, "Open folder"));
                break;
            case R.id.import_est_propio:
                /*
                HttpManejador httpo=new HttpManejador("192.168.0.105","marcos","1234");
                httpo.enviarPeticion(this,"Peticionnnn");
                */
                tipo_boton="imort_propio";
                Intent intent5;
                intent5 = new Intent(Intent.ACTION_GET_CONTENT);

                intent5.setType("*/*");
                startActivityForResult(Intent.createChooser(intent5, "Choose File"), VALOR_RETORNO);

                break;
        }
        }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            //Cancelado por el usuario
        }
        if ((resultCode == RESULT_OK) && (requestCode == VALOR_RETORNO )) {
            //Procesar el resultado

            Uri uri = data.getData(); //obtener el uri content

            System.out.println(uri);
            String [] res;
            switch (tipo_boton){
                case "boton_rf":
                    Ruta_folio ryf=new Ruta_folio();
                    res=ryf.importar_datos(this,Calculo.getPath(this,uri));
                    if(res[0].compareTo("1")==0) {
                        AlertDialog.Builder dialogoalerta = new AlertDialog.Builder(this);
                        dialogoalerta.setMessage(res[1] + " Linea(" + res[2] + ") - " + res[3]);
                        dialogoalerta.setTitle("Atencion");
                        dialogoalerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        dialogoalerta.create();
                        dialogoalerta.show();
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Carga Completa", Toast.LENGTH_LONG).show();
                    break;
                    case "boton_estados":
                    Estado est=new Estado();

                    res=est.importarEstados(this,Calculo.getPath(this,uri));

                        if(res[0].compareTo("1")==0) {
                            AlertDialog.Builder dialogoalerta = new AlertDialog.Builder(this);
                            dialogoalerta.setMessage(res[1] + " Linea(" + res[2] + ") - " + res[3]);
                            dialogoalerta.setTitle("Atencion");
                            dialogoalerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                            dialogoalerta.create();
                            dialogoalerta.show();
                        }
                        else
                            Toast.makeText(getApplicationContext(), "Carga Completa", Toast.LENGTH_LONG).show();

                    break;

                    case "cargar_orden_agua":
                    tipo_toma_estado="agua";
                    this.cargarOrdenes(uri,tipo_toma_estado);
                    break;
                    case "cargar_orden_energia":
                    tipo_toma_estado="energia";
                    this.cargarOrdenes(uri,tipo_toma_estado);
                    break;
                case "imort_propio":
                    Estado est1=new Estado();
                    String [] arch_nombre=Calculo.getPath(this,uri).split("/");
                    if(Calculo.getPath(this,uri).contains("agua"))
                        est1.importarEstadosSistemaPropio(this,Calculo.getPath(this,uri),"agua");
                    else
                        est1.importarEstadosSistemaPropio(this,Calculo.getPath(this,uri),"energia");


                    break;
            }


        }
}
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permisioninternet= ActivityCompat.checkSelfPermission(activity,Manifest.permission.INTERNET);
        if(permisioninternet!=PackageManager.PERMISSION_GRANTED){
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
    private void cargarOrdenes(Uri uri,String tipo_toma_estado){
        String [] res;
        Orden_usuario ord2=new Orden_usuario();
        res=ord2.cargar_orden(this,Calculo.getPath(this,uri),tipo_toma_estado);
        if(res[0].compareTo("1")==0) {
            AlertDialog.Builder dialogoalerta = new AlertDialog.Builder(this);
            dialogoalerta.setMessage(res[1] + " Linea(" + res[2] + ") - " + res[3]);
            dialogoalerta.setTitle("Atencion");
            dialogoalerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            dialogoalerta.create();
            dialogoalerta.show();
        }
        else {
            String[][] sin_orden=ord2.revisarSiTodosTienenOrden(this,tipo_toma_estado);
            if (sin_orden.length>0)
            {
                final AlertDialog.Builder dialogoalerta = new AlertDialog.Builder(this);
                String [] strout=new String[sin_orden.length];
                System.out.println(sin_orden.length);
                for(int i=0;i<sin_orden.length;i++){
                    strout[i]= sin_orden[i][1] + "," + sin_orden[i][2] + "," + sin_orden[i][3]+"\n";


                }
                dialogoalerta.setTitle("Usuarios sin Orden");
                dialogoalerta.setItems(strout,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }});
                // dialogoalerta.setTitle("Usuarios sin orden ("+i+")");
                //dialogoalerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                dialogoalerta.create();
                dialogoalerta.show();
            }
            else
                Toast.makeText(getApplicationContext(), "Carga Completa", Toast.LENGTH_LONG).show();
        }
    }
}
