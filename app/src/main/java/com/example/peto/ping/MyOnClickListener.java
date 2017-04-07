package com.example.peto.ping;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Peto on 30.03.2017.
 */

public class MyOnClickListener implements View.OnClickListener {

    private ArrayList<EditText> ipAdresses;
    private TextView console;
    private Activity activity;
    private EditText okInterval;
    private EditText badInterval;
    private int lines = 0;
    private Thread thread = null;
    static Boolean run;

    public MyOnClickListener(ArrayList<EditText> ipAdresses, TextView console, Activity activity, EditText okInterval, EditText badInterval){
        this.ipAdresses = ipAdresses;
        this.console = console;
        this.activity = activity;
        this.okInterval = okInterval;
        this.badInterval = badInterval;
    }

    @Override
    public void onClick(View view) {
        if (thread != null){
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                run = true;
                while (run) {
                    final Boolean[] ok = {true};
                    try {
                        for (int i = 0; i < ipAdresses.size(); i++) {
                            if (!ipAdresses.get(i).getText().toString().equals("")) {
                                final int result = ping(ipAdresses.get(i).getText().toString(), 1);
                                Thread.sleep(150);
                                final int finalI = i;
                                activity.runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        int j = 1;
                                        int count = 3;
                                        while (j <= count) {
                                            consoleAppend(result, finalI);
                                            if (result > 0){
                                                ok[0] = false;
                                            }
                                            else if (j == 1){
                                                count = 1;
                                            }
                                            j++;
                                        }
                                    }
                                });
                            }
                        }
                        if (ok[0])
                            Thread.sleep(Integer.parseInt(okInterval.getText().toString()) * 1000);
                        else
                            Thread.sleep(Integer.parseInt(badInterval.getText().toString()) * 1000);
                    } catch(InterruptedException e){
                            e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    private int ping(String ipAdress, int count){
        BufferedReader bufferedReader = null;
        String cmd = "/system/bin/ping -c " + count + " " + ipAdress;
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(cmd);
            proc.waitFor();
            bufferedReader = new BufferedReader(
                    new InputStreamReader(proc.getInputStream()));
            //console.append(Integer.toString(proc.exitValue()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String line;
        //console.setText("");
        //console.append(proc.exitValue() + "\n");

        try {
            Calendar now = Calendar.getInstance();
            File file = new File("/sdcard/ping/" + now.get(Calendar.DAY_OF_MONTH) + "-" +(now.get(Calendar.MONTH)+1) + "-" + now.get(Calendar.YEAR)+".txt");
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            if (now.get(Calendar.MINUTE) < 10)
                fileOutputStream.write((now.get(Calendar.HOUR_OF_DAY) + ":0" + (now.get(Calendar.MINUTE)) + ":").getBytes());
            else
                fileOutputStream.write((now.get(Calendar.HOUR_OF_DAY) + ":" + (now.get(Calendar.MINUTE)) + ":").getBytes());
            if (now.get(Calendar.SECOND) < 10)
                fileOutputStream.write(("0" + now.get(Calendar.SECOND) + " ").getBytes());
            else
                fileOutputStream.write((now.get(Calendar.SECOND) + " ").getBytes());
            if (proc.exitValue() == 0){
                fileOutputStream.write((ipAdress + " OK\n").getBytes());
            }
            else {
                fileOutputStream.write(("\n").getBytes());
                while ((line = bufferedReader.readLine()) != null) {
                    //console.append(line + "\n");
                    fileOutputStream.write((line + "\n").getBytes());
                }
                fileOutputStream.write("\n".getBytes());
            }
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return proc.exitValue();
    }

    public static void setRun(Boolean run) {
        MyOnClickListener.run = run;
    }

    public void consoleAppend(int result, int i){
        lines++;
        if (lines > 10){
            console.setText("");
            lines = 0;
        }
        Calendar now = Calendar.getInstance();
        if (now.get(Calendar.MINUTE) < 10)
            console.append(now.get(Calendar.HOUR_OF_DAY) + ":0" + (now.get(Calendar.MINUTE)) + ":");
        else
            console.append(now.get(Calendar.HOUR_OF_DAY) + ":" + (now.get(Calendar.MINUTE)) + ":");
        if (now.get(Calendar.SECOND) < 10)
            console.append("0" + now.get(Calendar.SECOND) + " ");
        else
            console.append(now.get(Calendar.SECOND) + " ");
        switch (result) {
            case 0:
                console.append(ipAdresses.get(i).getText().toString() + " OK\n");
                break;
            case 1:
                console.append(ipAdresses.get(i).getText().toString() + " NEDOSTUPNA\n");
                break;
            case 2:
                console.append(ipAdresses.get(i).getText().toString() + " CHYBA\n");
                break;
            default:
                console.append(ipAdresses.get(i).getText().toString() + " ????\n");
                break;
        }
    }
}
