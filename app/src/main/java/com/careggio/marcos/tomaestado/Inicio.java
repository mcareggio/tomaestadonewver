package com.careggio.marcos.tomaestado;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class Inicio extends AppCompatActivity {

    private String tipo_toma_estado;
    private boolean admin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.tipo_toma_estado="";
        this.admin=false;
        this.tipo_toma_estado=getIntent().getExtras().getString("tipo_toma_estado");
        if(getIntent().getExtras().getString("admin").compareTo("1")==0)
        this.admin=true;
        else
        this.admin=false;
        System.out.println(this.tipo_toma_estado);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inicio, menu);


        if(!this.admin) {
           //Descomentat para sacar permisos al user
           /* menu.findItem(R.id.action_cargar_rutas_folios).setVisible(false);
            menu.findItem(R.id.cambiar_orden).setVisible(false);
            menu.findItem(R.id.action_configuracion).setVisible(false);
            menu.findItem(R.id.action_serverconexion).setVisible(false);
            menu.findItem(R.id.agregar_usuario).setVisible(false);
            menu.findItem(R.id.action_actualizar_datos).setVisible(false);*/

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id) {

            case R.id.action_iniciar_recorrido:
                Toast.makeText(this, "Iniciando Recorrido", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this, Recorrido.class);
                //this.tipo_toma_estado="energia";
                i.putExtra("tipo_toma_estado", this.tipo_toma_estado);
                if(this.admin)
                i.putExtra("admin", "1");
                else
                i.putExtra("admin", "0");
                startActivity(i);

            break;
            case R.id.action_cargar_rutas_folios:
                Toast.makeText(this, "Cargar Ruta y Folio", Toast.LENGTH_SHORT).show();
                Intent i2 = new Intent(this, Cargar_ruta_folio.class);
                startActivity(i2);
            break;
            case  R.id.action_configuracion:
                Toast.makeText(this, "Configuracion", Toast.LENGTH_SHORT).show();
                Intent i3 = new Intent(this, Configuracion.class);
                startActivity(i3);
            break;
            case R.id.agregar_usuario:
                Intent i4 = new Intent(this,Agregar_Usuario.class);
                //i4.putExtra("tipo_toma_estado", this.tipo_toma_estado);
                startActivity(i4);
                break;
            case R.id.cambiar_orden:
                Intent i5 = new Intent(this,Modificar_orden.class);
                i5.putExtra("tipotomaestado", "");
                startActivity(i5);
            break;
            case R.id.action_buscar:
                Intent i6 = new Intent(this, Buscar.class);
                i6.putExtra("tipo_toma_estado", this.tipo_toma_estado);
                i6.putExtra("admin",this.admin);

                startActivity(i6);
                break;
            case R.id.action_serverconexion:
                Intent i7 = new Intent(this, Serverconexion.class);
                startActivity(i7);
                break;
            case R.id.action_actualizar_datos:
                Toast.makeText(this, "Actualizar Datos", Toast.LENGTH_SHORT).show();
                Intent i8 = new Intent(this, Actualizar_Datos.class);
                startActivity(i8);
                break;

        }

        return super.onOptionsItemSelected(item);
    }
}
