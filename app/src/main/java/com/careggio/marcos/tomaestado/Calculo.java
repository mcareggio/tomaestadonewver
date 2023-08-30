package com.careggio.marcos.tomaestado;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by marcos on 27/3/2018.
 */

 public class Calculo {
   static public String[] Separar_ruta_folio(String ruta_folio){
        String [] array=new String [2];
        array=ruta_folio.split("-");
        return array;
    }

    static public String  getTimeStamp(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());


        String fecha = dateFormat.format(new Date());

        System.out.println(fecha);
        return fecha;
    }
    static public String getDateTimeforFile(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss", Locale.getDefault());


        String fecha = dateFormat.format(new Date());

        System.out.println(fecha);
        return fecha;
    }
    public static String getPath(Context context, Uri uri) {
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {// DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                System.out.println(id);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);

            } else if (isMediaDocument(uri)) { // MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);

            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore (and general)
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);

        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
    public static String getPeriodoAnterior(String periodo){
        String res="";
        if(periodo.compareTo("")!=0||periodo!=null) {
            String[] mm_aaaa_array = periodo.split("_");

            int mes = Integer.parseInt(mm_aaaa_array[0]);
            int ano=Integer.parseInt(mm_aaaa_array[1]);
            mes--;
            if(mes<1) {
                mes = 12;
                ano--;
            }
            if(mes>9)
                res=String.valueOf(mes)+"_"+String.valueOf(ano);
            else
                res="0"+String.valueOf(mes)+"_"+String.valueOf(ano);
        }
        return res;
    }
    public static String getPeriodoAnoAnterior(String periodo){
        String res="";
        if(periodo.compareTo("")!=0||periodo!=null) {
            String[] mm_aaaa_array = periodo.split("_");


            int ano=Integer.parseInt(mm_aaaa_array[1]);
            ano--;
            res=mm_aaaa_array[0]+"_"+String.valueOf(ano);
        }
        return res;
    }
    public static boolean isNumeric(String cadena) {

        boolean resultado;

        try {
            Integer.parseInt(cadena);
            resultado = true;
        } catch (NumberFormatException excepcion) {
            resultado = false;
        }

        return resultado;
    }
    public static boolean isRuta(String s){
        boolean res=false;
        if(s!=null&&isNumeric(s)&&(s.compareTo("120")==0||s.compareTo("121")==0||s.compareTo("122")==0||s.compareTo("123")==0||s.compareTo("124")==0||s.compareTo("125")==0))
        res=true;
            return res;
    }
    public static boolean isFolio(String s){
        boolean res=false;
        if(s!=null&&isNumeric(s))
        {
            int f=Integer.parseInt(s);
            if(f>0&&f<1500)
                res=true;
        }

        return res;
    }
    public static boolean isValidText(String s){
        boolean res=false;
        if(s!=null&&s.length()<255)
                res=true;
        return res;
    }
    public static boolean isOrden(String s){
        boolean res=false;
        if(s!=null&&isNumeric(s))
        {
            int f=Integer.parseInt(s);
            if(f>0)
                res=true;
        }

        return res;
    }
    public static boolean isPeriodo(String periodo){
        boolean res=false;
        boolean match_mes=false;
        boolean math_ano=false;
        String [] array_a_verf=periodo.split("_");
        String [] array_meses={"01,02,03,04,05,06,07,08,09,10,11,12"};
        for(int i=0;i<12;i++)
        {
            if(array_a_verf[0].compareTo(array_meses[i])==0)
                match_mes=true;
        }
        if(Calculo.isNumeric(array_a_verf[1])) {
            int ano=Integer.parseInt(array_a_verf[1]);
            if (ano>1900&&ano<3000)
                math_ano=true;
        }
        if(math_ano&&match_mes)
            res=true;
        return res;
    }
    public static String convertPeriodoFormatFox(String periodo){
        String res="";
        boolean match_mes=false;
        boolean math_ano=false;
        int int_ano=0;
        String ano="";
        String mes="";

        int periodo_fox;
        if(Calculo.isNumeric(periodo))
        {

            int_ano=Integer.parseInt(periodo.substring(0,4));
            ano=periodo.substring(0,4);
            mes=periodo.substring(4,6);

        }

        String [] array_meses={"01","02","03","04","05","06","07","08","09","10","11","12"};
        for(int i=0;i<12;i++)
        {
            if(mes.compareTo(array_meses[i])==0)
                match_mes=true;
        }

            if (int_ano>=1900&&int_ano<=3000)
                math_ano=true;

            if(math_ano&&match_mes)
            res=mes+"_"+ano;
            else
            res="";
        System.out.println(res);
        return res;
    }
    public static ArrayList<String> getAniosAnterioresAlActual(int cant){
        ArrayList<String> anios=new ArrayList<>();

        Date d=new Date();
        CharSequence s = DateFormat.format("yyyy", d.getTime());

        int anio_actual=Integer.valueOf(s.toString());

        for (int i=0;i<cant;i++){
        anios.add(String.valueOf(anio_actual-i));
       // System.out.println(anio_actual+"   anioo");
        }
        return anios;

    }
}
