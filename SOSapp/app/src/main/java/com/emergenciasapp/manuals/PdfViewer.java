package com.emergenciasapp.manuals;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.emergenciasapp.sosapp.Permissions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.emergenciasapp.map.MapGoogle;

/**
 * Created by root on 6/07/16.
 */
public class PdfViewer {
    private  String path;
    private Context context;
    private final String filename;

    public PdfViewer(Activity context, String filename) {
        this.context = context;
        this.filename = filename;
        try {
            this.path = Environment.getExternalStorageDirectory().getCanonicalPath().toString() + "/" + filename;
        } catch (IOException e) {
        }
    }
    public void showPdf()
    {
        if(filename == null)
        {
            Toast.makeText(context, "No existe el archivo", Toast.LENGTH_SHORT).show();
            return;
        }
        File pdfFile = new File(path);
        if(!pdfFile.exists()) {
            createPdf();
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try
        {
             context.startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
            Toast.makeText(context, "NO Pdf Viewer", Toast.LENGTH_SHORT).show();
        }

    }
    private void createPdf()
    {
        if (ContextCompat.checkSelfPermission(context.getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Permissions.requestPermission((AppCompatActivity) context,
                    MapGoogle.WRITE_PERMISSION_REQUEST_CODE,Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return;
        }
        AssetManager manager = context.getAssets();
        InputStream rawPdf = null;
        OutputStream newpdf = null;
        try {
            rawPdf = manager.open(filename);
            if(rawPdf == null) throw new FileReadnullException("managaer can not open " + filename) ;
            newpdf = new FileOutputStream(new File(this.path));
            copyPdf(rawPdf , newpdf);
            rawPdf.close();
            newpdf.flush();
            newpdf.close();

        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(),e.toString() );
        }catch (FileReadnullException e) {
            Log.e(this.getClass().getSimpleName(),e.toString() );
        }
        finally {
            rawPdf = null;
            newpdf = null;
            //manager.close();
        }
    }
    private void copyPdf(InputStream original , OutputStream copy)
    {
        final int sizebuffer = 2048;
        byte buffer [] = new byte[sizebuffer];
        int read;
        try {
            while((read = original.read(buffer)) != -1)
            {
                copy.write(buffer,0,read);
            }
        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(),e.toString());
        }
    }
    private class FileReadnullException extends   Exception
    {
        private final String message;

        public FileReadnullException(String message) {
            this.message = message;
        }
        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
