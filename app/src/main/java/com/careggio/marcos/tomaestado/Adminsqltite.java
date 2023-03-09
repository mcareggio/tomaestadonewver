package com.careggio.marcos.tomaestado;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by marcos on 24/3/2018.
 */

public class Adminsqltite extends SQLiteOpenHelper{
    public static int version=51;

    public Adminsqltite(Context context, String nombre, SQLiteDatabase.CursorFactory factory, int version) {

        super(context, nombre, factory, version);

    }

    @Override

    public void onCreate(SQLiteDatabase db) {
              this.crearTablas(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        System.out.println("Actualizando BBDD...");
        /*
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        while (c.moveToNext()) {
            System.out.println(c.getString(0));
            if(c.getString(0).compareTo("android_metadata")!=0&&c.getString(0).compareTo("sqlite_sequence")!=0)
            db.execSQL("DROP TABLE IF EXISTS "+c.getString(0));

        }
        db.execSQL("DELETE FROM sqlite_sequence");
        this.crearTablas(db);

        *///Actulizar borrando todo
         db.execSQL("ALTER TABLE usuarios ADD geolocalizacion VARCHAR(100)");

    }
    public void crearTablas(SQLiteDatabase db){
        db.execSQL(Ruta_folio.getTableStructQuery());
        db.execSQL(Periodo.getTableStructQuery());
        db.execSQL(Orden_usuario.getTableStructQuery("agua"));
        db.execSQL(Orden_usuario.getTableStructQuery("energia"));
        db.execSQL(Opciones.getTableStructQuery());
        db.execSQL(ActualizarRutayFolio.getTableStructQuery());
        db.insert("opciones",null,Opciones.getValoresDefault());
        db.execSQL(Usuarios_sistema.getTableStructQuery());
        for (int i=0;i<3;i++)
        db.insert("usuarios_sistema",null, Usuarios_sistema.getValoresDefault(i));



    }
}
