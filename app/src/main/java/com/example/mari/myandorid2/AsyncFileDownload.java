package com.example.mari.myandorid2;

/**
 * Created by murotanimari on 2015/11/21.
 */

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class AsyncFileDownload extends AsyncTask<String, Void, Boolean>{
    private final String TAG = "murotani";
    private final int TIMEOUT_READ = 5000;
    private final int TIMEOUT_CONNECT = 30000;

    public Activity owner;
    private final int BUFFER_SIZE = 1024;

    private String urlString;
    private File outputFile;
    private FileOutputStream fileOutputStream;
    private InputStream inputStream;
    private BufferedInputStream bufferedInputStream;

    private int totalByte = 0;
    private int currentByte = 0;

    private byte[] buffer = new byte[BUFFER_SIZE];

    private URL url;
    private URLConnection urlConnection;

    public interface AsyncTaskCallback {
        void preExecute();
        void postExecute(File result);
    }
    private AsyncTaskCallback callback = null;;

    public AsyncFileDownload(Activity activity, String url, File oFile , AsyncTaskCallback _callback) {
        owner = activity;
        urlString = url;
        outputFile = oFile;
        this.callback = _callback;
    }


    @Override
    protected Boolean doInBackground(String... hoge) {
        try{
            connect();
        }catch(IOException e){
            Log.d(TAG, "ConnectError:" + e.toString());
            cancel(true);
        }

        if(isCancelled()){
            return false;
        }
        if (bufferedInputStream !=  null){
            try{
                int len;
                while((len = bufferedInputStream.read(buffer)) != -1){
                    fileOutputStream.write(buffer, 0, len);
                    currentByte += len;
                    //publishProgress();
                    if(isCancelled()){
                        break;
                    }
                }
                this.callback.postExecute(outputFile);
            }catch(IOException e){
                Log.d(TAG, e.toString());
                return false;
            }
        }else{
            Log.d(TAG, "bufferedInputStream == null");
        }

        try{
            close();
        }catch(IOException e){
            Log.d(TAG, "CloseError:" + e.toString());
        }
        return true;
    }

    @Override
    protected void onPreExecute(){
    }

    @Override
    protected void onPostExecute(Boolean result){
    }

    @Override
    protected void onProgressUpdate(Void... progress){
    }

    private void connect() throws IOException
    {
        url = new URL(urlString);
        urlConnection = url.openConnection();
        urlConnection.setReadTimeout(TIMEOUT_READ);
        urlConnection.setConnectTimeout(TIMEOUT_CONNECT);
        inputStream = urlConnection.getInputStream();
        bufferedInputStream = new BufferedInputStream(inputStream, BUFFER_SIZE);
        fileOutputStream = new FileOutputStream(outputFile);

        totalByte = urlConnection.getContentLength();
        currentByte = 0;
    }

    private void close() throws IOException
    {
        fileOutputStream.flush();
        fileOutputStream.close();
        bufferedInputStream.close();
    }

    public int getLoadedBytePercent()
    {
        if(totalByte <= 0){
            return 0;
        }
        return (int)Math.floor(100 * currentByte/totalByte);
    }

}