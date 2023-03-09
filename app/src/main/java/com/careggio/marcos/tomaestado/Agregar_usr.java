package com.careggio.marcos.tomaestado;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class Agregar_usr extends AppCompatActivity implements View.OnClickListener {

    EditText nombreyapellido, ruta, folio, nro_med_agua,nro_med_energia,direccion;
    CheckBox med_agua,med_energia;
    Button guardar;
    Boolean modificar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_usr);
        nombreyapellido=(EditText)findViewById(R.id.nombreyapellido);
        direccion=(EditText)findViewById(R.id.direccion);
        ruta=(EditText)findViewById(R.id.ruta);
        folio=(EditText)findViewById(R.id.folio);
        med_agua=(CheckBox)findViewById(R.id.check_nro_med_agua);
        med_agua.setOnClickListener(this);
        med_energia=(CheckBox)findViewById(R.id.check_nro_med_energia);
        med_energia.setOnClickListener(this);
        nro_med_agua=(EditText)findViewById(R.id.nro_m_agua);
        nro_med_energia=(EditText)findViewById(R.id.nro_m_energia);
        guardar=(Button)findViewById(R.id.guardar_usr_nuevo);
        guardar.setOnClickListener(this);
        modificar=false;
        if(getIntent().getExtras().getString("modificar").compareTo("1")==0){
            modificar=true;
            nombreyapellido.setText(getIntent().getExtras().getString("nombreyapellido"));
            ruta.setText(getIntent().getExtras().getString("ruta"));
            folio.setText(getIntent().getExtras().getString("folio"));
            direccion.setText(getIntent().getExtras().getString("direccion"));
            ruta.setEnabled(false);
            folio.setEnabled(false);
            if(getIntent().getExtras().getString("med_energia").compareTo("1")==0) {
                med_energia.setChecked(true);
                nro_med_energia.setVisibility(View.VISIBLE);
                nro_med_energia.setText(getIntent().getExtras().getString("nro_med_energia"));
            }
            if(getIntent().getExtras().getString("med_agua").compareTo("1")==0) {
                med_agua.setChecked(true);
                nro_med_agua.setVisibility(View.VISIBLE);
                nro_med_agua.setText(getIntent().getExtras().getString("nro_med_agua"));
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
         case R.id.check_nro_med_agua:
             if(med_agua.isChecked()){
                 nro_med_agua.setVisibility(view.VISIBLE);
             }
             else{
                 nro_med_agua.setVisibility(View.INVISIBLE);
             }

             break;
         case R.id.check_nro_med_energia:
             if(med_energia.isChecked()){
                 nro_med_energia.setVisibility(view.VISIBLE);
             }
             else{
                 nro_med_energia.setVisibility(view.INVISIBLE);
             }

             break;
         case R.id.guardar_usr_nuevo:

             if(!med_energia.isChecked())
             {
             nro_med_energia.setText("0");
             }
             if(!med_agua.isChecked()){
             nro_med_agua.setText("0");
             }
             Usuario usr=new Usuario();
             Intent volver=new Intent(this,Agregar_Usuario.class);
             if(!modificar) {
                if(verificar())
                {usr.guardarNuevoUsr(this, nombreyapellido.getText().toString(), ruta.getText().toString(), folio.getText().toString(), direccion.getText().toString(), nro_med_energia.getText().toString(), med_energia.isChecked(), nro_med_agua.getText().toString(), med_agua.isChecked());
                 startActivity(volver);
                 this.finish();}
             }else {
                 usr.modificarUsr(this, nombreyapellido.getText().toString(), ruta.getText().toString(), folio.getText().toString(), direccion.getText().toString(), nro_med_energia.getText().toString(), med_energia.isChecked(), nro_med_agua.getText().toString(), med_agua.isChecked());
                 startActivity(volver);
                 this.finish();
             }


             break;

        }
    }
    private boolean verificar(){
        boolean res=true;
        if(nombreyapellido.getText().toString().compareTo("")==0)
        {
            nombreyapellido.setError("El Nombre y Apellido no debe ser vacio");
            res=false;
        }
        Usuario usr=new Usuario();
        if(!(ruta.getText().toString().compareTo("")==0)&&!(folio.getText().toString().compareTo("")==0)) {
            if (usr.existeRutaFolio(this, ruta.getText().toString(), folio.getText().toString())) {
                res = false;
                folio.setError("Este folio ya existe en esta ruta");

            }
        }
        else
        {
            res=false;
            folio.setError("Ruta y Folio no pueden ser vacios");
        }

        return res;
    }
}
