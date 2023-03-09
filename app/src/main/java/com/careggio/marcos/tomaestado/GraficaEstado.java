package com.careggio.marcos.tomaestado;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;

import java.util.ArrayList;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

public class GraficaEstado extends AppCompatActivity {
    private GraficaEstado graficaEstado;
    private LineChart lineChart;
    private BarChart barChart;
    private String mes_a_mostrar="";
    private String ruta,folio,tipo_toma_estado;
    private String[] meses={"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
    private String [] array_meses={"01","02","03","04","05","06","07","08","09","10","11","12"};
    private ArrayList<String>anios;
    private Layout layout_graficaLinear;
    private int cantidad_anios_a_mostrar=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafica_estado);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        barChart=(BarChart)findViewById(R.id.graficodebarras);
        graficaEstado=this;
        this.tipo_toma_estado=getIntent().getExtras().getString("tipo_toma_estado");
        this.ruta=getIntent().getExtras().getString("ruta");
        this.folio=getIntent().getExtras().getString("folio");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(graficaEstado);
                dialogo1.setTitle("Opciones");
                dialogo1.setMessage("Elija el grafico que desea");
                dialogo1.setCancelable(false);

                dialogo1.setPositiveButton("Grafica Barras Anual", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        personalizarGrafica(barChart);
                        agregarElementoGb(barChart);
                        testLegend(meses,barChart);

                    }
                });
                dialogo1.setNegativeButton("Grafica Linear Mensual Interanual", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        graficaEstado.setContentView(R.layout.content_graficar_linear);
                        lineChart=(LineChart)findViewById(R.id.graficolineas);
                        mes_a_mostrar="12";
                        cantidad_anios_a_mostrar=3;
                        personalizarGrafica(lineChart);
                        agregarElementos(lineChart);
                        anios=Calculo.getAniosAnterioresAlActual(cantidad_anios_a_mostrar);
                        String[] array=new String[anios.size()];
                        for(int i=0;i<anios.size();i++)
                        array[i]=anios.get(i);
                        testLegend(array,lineChart);

                    }
                });
                dialogo1.show();

            }
        });





    }
        private Chart personalizarGrafica(Chart chart){
        chart.getDescription().setText("Estados");
        chart.getDescription().setTextSize(5);
        chart.setBackgroundColor(Color.WHITE);
        chart.animateX(3000);
        return chart;
    }
    private void legend(Chart chart){
            Legend legend=chart.getLegend();
            legend.setForm(Legend.LegendForm.LINE);
            legend.setTextSize(8);
            ArrayList<String> anios=Calculo.getAniosAnterioresAlActual(cantidad_anios_a_mostrar);
            ArrayList<LegendEntry> entries=new ArrayList<>();
            for(int i=0;i<cantidad_anios_a_mostrar;i++){
            LegendEntry entry=new LegendEntry();

            entry.label=anios.get(i);
            entries.add(entry);

        }

        legend.setCustom(entries);
    }
    private void testLegend(final String [] array, Chart chart){

        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return array[(int) value];
            }
        };
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        //xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setValueFormatter(formatter);
        xAxis.setTextSize(8);
        xAxis.setLabelRotationAngle(30f);

    }

    private void agregarElementos(Chart chart){
        ArrayList<Entry> entries=new ArrayList<>();
        Estado estado=new Estado(ruta,folio);
        int consumo=0;
        anios=Calculo.getAniosAnterioresAlActual(cantidad_anios_a_mostrar);
        for(int i=0;i<anios.size();i++){

            consumo=Integer.valueOf(estado.getConsumoXperiodo(graficaEstado,mes_a_mostrar+"_"+anios.get(i),tipo_toma_estado));
            entries.add(new Entry(i, consumo));

        }
        Usuario u=new Usuario();
        String[][] datos=u.getDatosUsuario(graficaEstado,ruta,folio);
        LineDataSet dataSet = new LineDataSet(entries, datos[0][3]+" - Periodo "+mes_a_mostrar); // add entries to dataset
        LineData lineData=new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();

        //dataSet.setColor(...);
        //dataSet.setValueTextColor(...);

    }
    private void  agregarElementoGb(Chart chart){
        ArrayList<BarEntry> entriesgrupo1=new ArrayList<>();
        ArrayList<BarEntry> entriesgrupo2=new ArrayList<>();
        ArrayList<BarEntry> entriesgrupo3=new ArrayList<>();
        Estado estado=new Estado(ruta,folio);
        int consumo=0;
        ArrayList<String> anios=Calculo.getAniosAnterioresAlActual(3);
        for(int i=0;i<12;i++){
            consumo=Integer.valueOf(estado.getConsumoXperiodo(graficaEstado,array_meses[i]+"_"+anios.get(2),tipo_toma_estado));
            entriesgrupo1.add(new BarEntry(i,consumo ));
            consumo=Integer.valueOf(estado.getConsumoXperiodo(graficaEstado,array_meses[i]+"_"+anios.get(1),tipo_toma_estado));
            entriesgrupo2.add(new BarEntry(i,consumo ));
            consumo=Integer.valueOf(estado.getConsumoXperiodo(graficaEstado,array_meses[i]+"_"+anios.get(0),tipo_toma_estado));
            entriesgrupo3.add(new BarEntry(i,consumo ));
            //entries.add(new LineData((i,(i*100))));
        }
        BarDataSet set1= new BarDataSet(entriesgrupo1,anios.get(2));
        set1.setColor(Color.BLUE);
        BarDataSet set2= new BarDataSet(entriesgrupo2,anios.get(1));
        set2.setColor(Color.RED);
        BarDataSet set3= new BarDataSet(entriesgrupo3,anios.get(0));
        set3.setColor(Color.CYAN);
        BarData  data=new BarData(set1,set2,set3);
        float groupSpace = 0.08f;
        float barSpace = 0.001f; // x2 dataset
        float barWidth = 0.3f; // x2 dataset

        data.setBarWidth(barWidth); // set custom bar width


        chart.setData(data);
        barChart.groupBars(-0.5f, groupSpace, barSpace);
        chart.invalidate(); // refresh
    }

}
