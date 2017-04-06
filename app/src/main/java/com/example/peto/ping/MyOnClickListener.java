package com.example.peto.ping;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
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


        new Thread(new Runnable() {
            @Override
            public void run() {
                run = true;
                while (run) {
                    final Boolean[] ok = {true};
                    try {
                        for (int i = 0; i < ipAdresses.size(); i++) {
                            if (!ipAdresses.get(i).getText().toString().equals("")) {
                                final int result = ping(ipAdresses.get(i).getText().toString());
                                Thread.sleep(150);
                                final int finalI = i;
                                activity.runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
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
                                                console.append(ipAdresses.get(finalI).getText().toString() + " OK\n");
                                                break;
                                            case 1:
                                                console.append(ipAdresses.get(finalI).getText().toString() + " NEDOSTUPNA\n");
                                                ok[0] = false;
                                                break;
                                            case 2:
                                                console.append(ipAdresses.get(finalI).getText().toString() + " CHYBA\n");
                                                ok[0] = false;
                                                break;
                                            default:
                                                console.append(ipAdresses.get(finalI).getText().toString() + " ????\n");
                                                ok[0] = false;
                                                break;
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
        }).start();


        }

    private int ping(String ipAdress){
        BufferedReader bufferedReader = null;
        String cmd = "/system/bin/ping -c 1 " + ipAdress;
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
            File file = new File("/sdcard/ping/" + now.get(Calendar.DAY_OF_MONTH) + "-" +(now.get(Calendar.MONTH)+1) + "-" + now.get(Calendar.YEAR));
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
                fileOutputStream.write((ipAdress + "OK\n").getBytes());
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
}