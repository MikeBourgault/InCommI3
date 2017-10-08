package com.auth0.samples;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.auth0.samples.MainActivity;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingDeque;

import retrofit2.*;

public class debit_Transaction_Notification_Service extends Service {
    String TAG = "Service Created";
    String TAG2 = "Service Started";
    Timer timer = new Timer(true);
    boolean ifDebitInLast10Secs = false;
    Retrofit retrofit = MainActivity.builder.client(MainActivity.httpClient.build()).build();


    private boolean isRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();

        Toast.makeText(this, "Transaction Notification Service Created", Toast.LENGTH_SHORT).show();

        isRunning = true;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Transaction Notification Service Started", Toast.LENGTH_SHORT).show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG2, "Transaction Notification Service Started");

                TimerTask checkForTransaction = new TimerTask() {
                    public void run() {
                        try {

                            pullDebitTransactions(boolean ifDebitInLast10Secs);


                            if (MainActivity.ifDebitInLast10Secs) {
                                // Read in counterParty ID
                                Intent intent = new Intent(getApplicationContext(), you_stop_here.class);
                            }


                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                timer.schedule(checkForTransaction, 0, 10000);
            }
        }
    }

    public boolean pullDebitTransactions(boolean ifDebitInLast10Secs) {
        ifDebitInLast10Secs = false;



    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
