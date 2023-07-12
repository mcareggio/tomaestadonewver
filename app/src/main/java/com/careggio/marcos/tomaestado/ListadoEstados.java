package com.careggio.marcos.tomaestado;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListadoEstados extends AppCompatActivity {
    private String ruta,folio,tipo_toma_estado;
    private TextView usuario_list_estados,direccion_list_estados,tipotomaestado_list_estados,nromed_list_estados,ryf_list_estados;
    private List<Map<String, String>> elementos;
    private SimpleAdapter adaptador1;
    private ListView lista_estados;
    private boolean admin;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_listado_de_estados);
        usuario_list_estados=(TextView)findViewById(R.id.usuario_nombre_list_estados);
        direccion_list_estados=(TextView)findViewById(R.id.usuario_dire_list_estados);
        tipotomaestado_list_estados=(TextView)findViewById(R.id.usuario_tomaestado_list_estados);
        nromed_list_estados=(TextView)findViewById(R.id.usuario_medidor_list_estados);
        ryf_list_estados=(TextView)findViewById(R.id.usuario_ryf_list_estados);
        lista_estados=(ListView)findViewById(R.id.lista_estados);


        elementos= new ArrayList<Map<String, String>>();
        this.tipo_toma_estado=getIntent().getExtras().getString("tipo_toma_estado");
        this.ruta=getIntent().getExtras().getString("ruta");
        this.folio=getIntent().getExtras().getString("folio");
        this.admin=getIntent().getExtras().getBoolean("admin");
        adaptador1= new SimpleAdapter(this, elementos,
                android.R.layout.simple_list_item_2,
                new String[] {"consumoyestado", "periodo"},
                new int[] {android.R.id.text1,
                        android.R.id.text2});
        lista_estados.setAdapter(adaptador1);
        cargarEstados();
    }
        void cargarEstados(){
            elementos.clear();

            Usuario usr=new Usuario();
            usr.cargarUsuario(this,this.ruta,this.folio);
            String usuario=usr.getNombreyapellido()+" "+usr.getDireccion()+" Nro Medidor "+this.tipo_toma_estado+":"+usr.getNro_medidor()+" Ruta:"+usr.getRuta()+" Folio:"+usr.getFolio();
            usuario_list_estados.setText(usr.getNombreyapellido());
            direccion_list_estados.setText(usr.getDireccion());
            nromed_list_estados.setText("Medidor: "+usr.getNro_medidor());
            tipotomaestado_list_estados.setText(this.tipo_toma_estado);
            ryf_list_estados.setText(this.ruta+"-"+this.folio);

            Estado est=new Estado(this.ruta,this.folio);
            Periodo per=new Periodo();
            String[][] periodo=per.getPeriodosArray(this);
            String consumo="",estado="";
            for(int i=0;i<24&&i<periodo.length-1;i++){
                if(!est.IsEstadoCargado(this,this.ruta,this.folio,this.tipo_toma_estado,periodo[i][1])) {

                consumo=est.getConsumoXperiodo(this,periodo[i][1],this.tipo_toma_estado);
                estado=est.getEstadoByPeriodoyRyF(this,periodo[i][1],this.tipo_toma_estado,this.ruta,this.folio);
                System.out.println("Estado "+estado);

                    Map<String, String> datum = new HashMap<String, String>(3);

                    datum.put("consumoyestado", "Consumo: "+consumo+" - Estado: "+estado);
                    datum.put("periodo", periodo[i][1]);
                    elementos.add(datum);
                }

            }
            adaptador1= new SimpleAdapter(this, elementos,
                    android.R.layout.simple_list_item_2,
                    new String[] {"consumoyestado","periodo"},
                    new int[] {android.R.id.text1,
                            android.R.id.text2});
            lista_estados.setAdapter(adaptador1);


        }
}