package com.careggio.marcos.tomaestado;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class Recorrido extends AppCompatActivity implements View.OnClickListener {
    Button btn, btn_pasar_por_alto;
    TextView ruta_folio, nombre_apellido, estado_anterior, periodo, orden, consumo, direccion,nro_medidor;
    EditText estado_nuevo, estado_conf;
    String orden_actual;
    String tipo_toma_estado = "";
    String periodo_actual = "";
    String latylon="";
    String admin;
    boolean cargar_estado,ubicacion_activa,detener_carga;
    Estado estado;
    Context context;
    Recorrido rec;
    private LocationManager locManager;
    private LocationListener locListener;
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Recorrido.verifyStoragePermissions(this);
        setContentView(R.layout.activity_recorrido);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btn = (Button) findViewById(R.id.btn_cargar_estado);
        btn_pasar_por_alto = (Button) findViewById(R.id.btn_pasar_s_cargar);
        btn.setOnClickListener(this);
        btn_pasar_por_alto.setOnClickListener(this);
        latylon="";
        nombre_apellido = (TextView) findViewById(R.id.nombre_apellido);
        ruta_folio = (TextView) findViewById(R.id.ruta_folio);
        estado_anterior = (TextView) findViewById(R.id.estado_anterior);
        direccion = (TextView) findViewById(R.id.direccion);
        estado_nuevo = (EditText) findViewById(R.id.estado_nuevo);
        nro_medidor= (TextView) findViewById(R.id.nro_medidor);
        ubicacion_activa=true;
        detener_carga=false;
        estado_nuevo.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        /*This method is called to notify you that, within s, the count characters beginning at start are about to be replaced by new text with length after. It is an error to attempt to make changes to s from this callback.*/
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int est_act_int = 0, estado_ant_int;
                String estado_ant_str = estado_anterior.getText().toString();
                if (s != null && s.toString().compareTo("") != 0 && estado_ant_str != null && estado_ant_str.compareTo("") != 0) {
                    if (s.toString().length() >= estado_ant_str.length()) {
                        est_act_int = Integer.valueOf(s.toString());
                        estado_ant_int = Integer.valueOf(estado_ant_str);
                        int res = (est_act_int - estado_ant_int);

                        consumo.setText(String.valueOf(res));
                    } else
                        consumo.setText("");
                }
            }
        });


        periodo = (TextView) findViewById(R.id.periodo);
        orden = (TextView) findViewById(R.id.orden);
        consumo = (TextView) findViewById(R.id.consumo);
        context = this;
        rec = this;


        Periodo per = new Periodo();
        periodo_actual = per.getPeriodoActual(this);
        System.out.println("Periodo Actual " + periodo_actual);

        if (periodo_actual.compareTo("00_0000") == 0) {
            tipo_toma_estado = getIntent().getExtras().getString("tipo_toma_estado");
            admin = getIntent().getExtras().getString("admin");
            System.out.println("Ir al menu principal");
            Toast.makeText(this, "No selecciono ningun periodo", Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, Inicio.class);
            i.putExtra("tipo_toma_estado", tipo_toma_estado);
            i.putExtra("admin", admin);
            startActivity(i);
            this.finish();
        } else {
            tipo_toma_estado = getIntent().getExtras().getString("tipo_toma_estado");
            admin = getIntent().getExtras().getString("admin");
            String ruta = getIntent().getExtras().getString("ruta");
            String folio = getIntent().getExtras().getString("folio");
            //cargar orden por ruta folio si no esta cargado
            cargar_estado = true;
            periodo.setText(periodo_actual);
            orden_actual = "0";
            if (ruta != null && ruta.compareTo("") != 0 && folio != null && folio.compareTo("") != 0) {
                Estado est2 = new Estado();
                if (est2.IsEstadoCargado(this, ruta, folio, this.tipo_toma_estado, periodo_actual)) {
                    Orden_usuario ordn = new Orden_usuario();
                    int orden_sig = ordn.getOrdenByRyF(this, ruta, folio, this.tipo_toma_estado);
                    int orden_actual_int = orden_sig - 1;
                    orden_actual = String.valueOf(orden_actual_int);
                    this.orden.setText(orden_actual);
                    ruta = null;
                    folio = null;
                } else {
                    Toast.makeText(this, "El Estado ya Fue Cargado", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
            this.cargar_siguete_usuario(tipo_toma_estado, periodo_actual, orden_actual);
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_cargar_estado:
                    this.obtenerGeolocalizacion();
                if(!detener_carga){
                if (estado_nuevo.getText().toString().compareTo("") != 0 && cargar_estado) {
                    String[] ruta_folio = Calculo.Separar_ruta_folio((String) this.ruta_folio.getText());
                    estado = new Estado(this, ruta_folio[0], ruta_folio[1], (estado_nuevo.getText()).toString(), periodo_actual, tipo_toma_estado);

                    if (estado.verificacion_estadistica(this, this.tipo_toma_estado, (estado_nuevo.getText()).toString()))

                    {


                        this.acpetarEstado(estado);

                        //

                    } else {


                        System.out.println("errorrr estado fuera de estadistica ingresar nuevamente");
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        // Get the layout inflater
                        //
                        LayoutInflater inflater = this.getLayoutInflater();

                        // Inflate and set the layout for the dialog
                        // Pass null as the parent view because its going in the dialog layout
                        View dialogview = inflater.inflate(R.layout.doble_conf_estado, null);
                        builder.setView(dialogview);
                        estado_conf = (EditText) dialogview.findViewById(R.id.estado_confirm);

                        // Add action buttons
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                //setContentView(R.layout.doble_conf_estado);


                                System.out.println(estado_conf.getText().toString());
                                System.out.println(estado_nuevo.getText().toString());

                                if (estado_conf.getText().toString().compareTo(estado_nuevo.getText().toString()) == 0) {
                                    rec.acpetarEstado(estado);
                                    System.out.println("estado aceptado");
                                } else {
                                    System.out.println("rechazado");
                                    AlertDialog.Builder dialogoalerta = new AlertDialog.Builder(context);
                                    dialogoalerta.setMessage("Los Estados no Coinciden");
                                    dialogoalerta.setTitle("Atencion");
                                    dialogoalerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();

                                        }
                                    });
                                    dialogoalerta.create();
                                    dialogoalerta.show();
                                    estado_nuevo.setText("");
                                    consumo.setText("");
                                    estado_nuevo.requestFocus();
                                }

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
                }
            }


        break;

        case R.id.btn_pasar_s_cargar:
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Get the layout inflater
            //
            LayoutInflater inflater = this.getLayoutInflater();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            View dialogview=inflater.inflate(R.layout.cargar_observacion_estado, null);
            builder.setView(dialogview);
            final EditText observacion=(EditText)dialogview.findViewById(R.id.observacion);
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                    //setContentView(R.layout.doble_conf_estado);
                    String[] ruta_folio = Calculo.Separar_ruta_folio((String) rec.ruta_folio.getText());
                    estado = new Estado(rec, ruta_folio[0], ruta_folio[1],rec.estado_anterior.getText().toString(), periodo_actual, tipo_toma_estado);
                    estado.observacion=observacion.getText().toString();
                    rec.acpetarEstado(estado);







                }
            })
                    .setNegativeButton("Cargar Estado mas tarde", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                 rec.cargar_siguete_usuario(tipo_toma_estado, periodo_actual, orden.getText().toString());
                                }
                            }
                    );
            builder.create();
            builder.show();
            //Toast.makeText(this, "Pansando Usuario Sin Cargar Estado", Toast.LENGTH_SHORT).show();


            break;
        }
    }
     private void cargar_siguete_usuario(String tipo_toma_estado,String periodo,String orden_actual){
        Usuario usr=new Usuario();
        Estado est=new Estado();
        String[][] array=usr.proximo_usuario_vacio(this,tipo_toma_estado,periodo,orden.getText().toString());
         estado_nuevo.setText("");
         ruta_folio.setText("");
         nombre_apellido.setText("");
         estado_anterior.setText("");
         consumo.setText("");
        direccion.setText("");
        orden.setText("");
        nro_medidor.setText("");
        if(array.length>0) {
            ruta_folio.setText(array[0][1] + "-" + array[0][2]);
            nombre_apellido.setText(array[0][3]);
            nombre_apellido.setTextSize(20);
            direccion.setText(array[0][4]);
            if(array[0][6]!=null)
            nro_medidor.setText(array[0][6]);
            estado_anterior.setText(est.getEstadoByPeriodoyRyF(this,Calculo.getPeriodoAnterior(periodo),tipo_toma_estado,array[0][1],array[0][2]));
            String com=new String(tipo_toma_estado);

            orden.setText(array[0][5]);
        }
        else
        {
            cargar_estado=false;

            Toast.makeText(this, "No hay mas datos para cargar", Toast.LENGTH_SHORT).show();
        }
            // System.out.println("Devolucion final "+array[0][7]+"_"+array[1][7]);

    }
    private void acpetarEstado(Estado estado){

        estado.setGeolocalizacion(this.latylon);
        estado.guardar_estado();

        String[] ruta_folio = Calculo.Separar_ruta_folio((String) rec.ruta_folio.getText());
        Usuario usr=new Usuario();

        usr.guardarGeolocSiNoExiste(this,ruta_folio[0],ruta_folio[1],this.latylon);

        Toast.makeText(this, "Estado Cargado", Toast.LENGTH_SHORT).show();
        this.cargar_siguete_usuario(tipo_toma_estado, periodo_actual, orden.getText().toString());
    }
    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_LOCATION,1
            );
        }
    }
    public void obtenerGeolocalizacion() {
        if (ubicacion_activa) {
            try {
                if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    System.out.println("No hay Permiso");

                    locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                } else {

                    locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    LocationListener locationListenerGPS = new LocationListener() {
                        @Override
                        public void onLocationChanged(android.location.Location location) {

                            double lat = location.getLatitude();
                            double lon = location.getLongitude();
                            latylon = String.valueOf(lat) + "," + String.valueOf(lon);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    };

                    locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60, (float)0.5, locationListenerGPS);

                    if (this.latylon == "") {
                        double lat = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
                        double lon = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
                        latylon = String.valueOf(lat) + "," + String.valueOf(lon);
                    }
                    detener_carga=false;
                  //  Toast.makeText(this, "LatyLong " + this.latylon, Toast.LENGTH_SHORT).show();
                    //System.out.println("geo" + this.latylon);


                }
            } catch (Exception e) {
               /* detener_carga=true;

                AlertDialog.Builder dialogoalerta = new AlertDialog.Builder(context);
                dialogoalerta.setMessage("No es posible obtener la ubicacion");
                dialogoalerta.setTitle("Atencion");
                dialogoalerta.setPositiveButton("Continuar sin Ubicación", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ubicacion_activa = false;//cambiar
                        detener_carga=false;
                        dialog.cancel();
                    }
                });
                dialogoalerta.setNegativeButton("Cancelar para Activarla", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });
                dialogoalerta.create();
                dialogoalerta.show();*/ //sacar comentario para que aparezca el cartel

                e.printStackTrace();
            }
            if(latylon!=""&&latylon!=null)
            //Toast.makeText(this, latylon, Toast.LENGTH_LONG).show();
            System.out.println(latylon);
        }
    }


}
