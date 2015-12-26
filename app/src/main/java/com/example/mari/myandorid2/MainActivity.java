package com.example.mari.myandorid2;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import dalvik.system.DexClassLoader;


public class MainActivity extends AppCompatActivity {
    //private String targetClassURL = "http://192.168.10.4:8888/intermediates/classes/release/com/example/mari/myandroid/TestDinamicLoad.class";
    //private String targetClassURL = "http://192.168.10.4:8888/";
    //  class
    private String targetClassURL = "https://drive.google.com/file/d/0B0Hrot21k8nYZ2pWNXBBMU5Qd2M/view?usp=sharing";
    private String targetClassFile = "TestDinamicLoad.class";
    //  jar
    //private String targetClassURL = "https://drive.google.com/file/d/0B0Hrot21k8nYb05PWDYzRVBWRjg/view?usp=sharing";
    //  jar dex
    //private String targetClassURL = "https://drive.google.com/file/d/0B0Hrot21k8nYdG9wdHhiOWJIVE0/view?usp=sharing";
    //rivate String targetClassFile = "TestDinamicLoad.jar";

    private String targetClassPath = "";
    private String targetClassName = "com.example.mari.myandroid.TestDinamicLoad";
    private AsyncFileDownload asyncfiledownload;

    private String TAG = "murotani";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        this.targetClassPath = getApplicationContext().getFilesDir().getPath().toString();
        this.targetClassPath += "/" + targetClassFile;

        Log.v(TAG, this.targetClassPath);

        //WebView webview = new WebView(this.getApplicationContext());
        //webview.loadUrl(this.targetClassURL);
        //RelativeLayout relativeLayout= (RelativeLayout)findViewById(R.id.layout);
        //relativeLayout.addView(webview);

        try {
            AsyncFileDownload asyncFileDownload = new AsyncFileDownload(this,targetClassURL,new File(targetClassPath),
            new AsyncFileDownload.AsyncTaskCallback() {
                public void preExecute() {
                }
                public void postExecute(File file) {
                    Log.v(TAG, file.toString());

                    changeTextMessage("Download Complete: " + file.toString());

                    urlLoader(file);
                    dexLoader(file);
                    changeTextMessage("END");

                 }
            });
            asyncFileDownload.execute();

        } catch(Exception e){
            e.printStackTrace();
        }

    }

        private void dexLoader(final File file){
        try {
            Context ctx     = getApplicationContext();
            String         dex_dir = ctx.getDir("dex", 0).getAbsolutePath();
            ClassLoader    parent  = getClass().getClassLoader();
            DexClassLoader loader  = new DexClassLoader(file.getPath(), dex_dir, null, parent);
            Class          c       = loader.loadClass(this.targetClassName);
            Object         o       = c.newInstance();
            Method m       = c.getMethod("func");

            m.invoke(o);
        } catch (Exception e) {
            Log.v(TAG, e.toString());
        }

    }

        private void urlLoader(final File file){
            try {
                URL myurl[] = {new URL("file:" + file.toString())};
                URLClassLoader x = new URLClassLoader(myurl);
                Class c = x.loadClass(this.targetClassName);
                for (Field f : c.getDeclaredFields()) {
                    Log.v(TAG, f.getName() + " : " + f.toString());
                }
            }catch(Exception e){
                Log.v(TAG, e.toString());
            }
        }

        private void changeTextMessage(final String message){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView text = (TextView) findViewById(R.id.textme);
                    text.setText(message);
                }
            });

        }
}
