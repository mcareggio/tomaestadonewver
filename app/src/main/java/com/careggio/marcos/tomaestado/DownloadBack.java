package com.careggio.marcos.tomaestado;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by marcos on 22/10/2018.
 */

public class DownloadBack extends AsyncTask<URL, Integer, Long>{
    private String ip;
    private String file_name;


    public DownloadBack(){

    }
    @Override
    protected Long doInBackground(URL... urls){

        System.out.println(urls[0]);
        URL myurl=urls[0];
        try{
            byte[] todo=null;
            byte[] parte=new byte[1024];
            ByteArrayOutputStream boss=new ByteArrayOutputStream();
            HttpURLConnection con=(HttpURLConnection)myurl.openConnection();

            con.connect();


            int cont=0;
            while ((cont=con.getInputStream().read(parte))!=-1){

                boss.write(parte,0,cont);

                boss.flush();
            }
            String array[]=myurl.getFile().split("/");
            System.out.println("file"+array[2]);
            todo=boss.toByteArray();

            File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),array[2]);
            FileOutputStream fos=new FileOutputStream(file);
            BufferedOutputStream buff=new BufferedOutputStream(fos);
            buff.write(todo);
            buff.close();

        }catch (Exception e){
            e.printStackTrace();
        System.out.println(" Error en la descarga");
        }

        return null;
    }
    protected void onProgressUpdate(Integer... progress) {
        //
    }

    protected void onPostExecute(Long result) {
        System.out.println("Finn");
    }
}
