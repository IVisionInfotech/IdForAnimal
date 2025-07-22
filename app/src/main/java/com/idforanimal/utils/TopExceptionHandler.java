package com.idforanimal.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.idforanimal.activity.BaseActivity;
import com.idforanimal.R;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TopExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler defaultUEH;
    private String localPath;
    private Context context;

    public TopExceptionHandler(String localPath, Context context) {
        this.context = context;
        this.localPath = localPath;
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void uncaughtException(Thread t, Throwable e) {

        //Write a printable representation of this Throwable
        //The StringWriter gives the lock used to synchronize access to this writer.
        final Writer stringBuffSync = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringBuffSync);
        e.printStackTrace(printWriter);
        String stacktrace = stringBuffSync.toString();
        printWriter.close();

        if (localPath != null) {
            writeToFile(stacktrace);
        }

        //Used only to prevent from any code getting executed.
        // Not needed in this example
        defaultUEH.uncaughtException(t, e);
    }

    private void writeToFile(String currentStacktrace) {

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            Date date = new Date();
            String filename = "/" + dateFormat.format(date) + ".txt";

            File directory;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                String path = Environment.DIRECTORY_DOCUMENTS + File.separator + context.getString(R.string.app_name);
                directory = Environment.getExternalStoragePublicDirectory(path);
            } else {
                directory = new File(Environment.getExternalStorageDirectory(), context.getString(R.string.app_name));
            }

            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Write the file into the folder
            File file = new File(directory, filename);
            if (directory.exists()) {
                file.createNewFile();
            }

            if (file.exists()) {
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.append(currentStacktrace);
                fileWriter.flush();
                fileWriter.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}