package com.royle.beartv2;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class Mxplayer extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mxplayer);




        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(Mxplayer.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();

    }

}

