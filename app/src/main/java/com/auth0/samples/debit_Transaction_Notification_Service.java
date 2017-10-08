package com.auth0.samples;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.samples.MainActivity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingDeque;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

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


                            if (ifDebitInLast10Secs) {

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



        if(MainActivity.get)


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }

    public interface ApiClient {
        @POST("/api/accounts")
        Call<List<IncommAccount>> createUserAccount(@Header("Authorization") String authToken);

        @GET("/api/accounts")
        Call<List<IncommAccount>> getUserAccount(@Header("Authorization") String authToken);

        @POST("/api/transactions")
        Call<List<Transaction>> createTransaction(@Body Transaction data, @Header("Authorization") String authToken);

        @GET("/api/transactions")
        Call<List<Transaction>> getAllTransactions(@Header("Authorization") String authToken);

        @GET("/api/transactions/(id)")
        Call<List<Transaction>> getTransaction(@Path("id") String id, @Header("Authorization") String authToken);

    }

    String authToken = "Bearer " + getIntent().getExtras().getString("ACCESS_TOKEN");
    public static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl("https://incomm-act-mgt.appspot.com")
            .addConverterFactory(GsonConverterFactory.create());
    public Retrofit retrofit = builder.client(httpClient.build()).build();
    public ApiClient client = retrofit.create(ApiClient.class);

    public void getTransactions() {
        Call<List<Transaction>> transaction= client.getAllTransactions(authToken);

        transaction.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                // The call was a success.  We successfully got a response
                if (response.code() == 200) {
                    int amount = response.body().get(response.body().size() - 1).getAmount();
                    ((TextView) findViewById(R.id.token))
                            .setText(String.format("$%d.%02d", amount / 100, amount % 100));
                } else if(response.code() == 401) {
                    ((TextView) findViewById(R.id.token))
                            .setText("Authorization Error.");
                } else if(response.code() == 404) {
                    // User account doesn't exist, need to create one
                    ((TextView) findViewById(R.id.token))
                            .setText("You don't exist.");
                } else {
                    ((TextView) findViewById(R.id.token)).setText("Something went wrong with account details.");
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                // The call failed.
                // TODO: Handle the Error
            }
        });
    }


}
