package com.auth0.samples;

import com.auth0.samples.Transaction;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.samples.MainActivity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingDeque;

import okhttp3.OkHttpClient;
import retrofit2.*;
import retrofit2.Call;
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
    public static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    public static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl("https://incomm-act-mgt.appspot.com")
            .addConverterFactory(GsonConverterFactory.create());
    private SharedPreferences preferences;
    public Retrofit retrofit = builder.client(httpClient.build()).build();
    public ApiClient client = retrofit.create(ApiClient.class);
    private RelativeLayout relativeLayout;
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    private String realAuthToken;



    @Override
    public void onCreate() {
        super.onCreate();

        Toast.makeText(this, "Transaction Notification Service Created", Toast.LENGTH_SHORT).show();

        preferences = this.getSharedPreferences("key", MODE_WORLD_WRITEABLE);

        realAuthToken = preferences.getString("key","null");


        boolean isRunning = true;
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

                            pullDebitTransactions(ifDebitInLast10Secs);

                            if(ifDebitInLast10Secs) {

                                Intent intent = new Intent(getApplicationContext(), you_stopped_here.class);
                            }


                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                timer.schedule(checkForTransaction, 0, 10000);
            }
        });
        return START_STICKY;
    }

    public boolean pullDebitTransactions(boolean ifDebitInLast10Secs) {
        ifDebitInLast10Secs = false;

        getTransactions();

        if()


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }

    public interface ApiClient {
        @POST("/api/accounts")
        Call<IncommAccount> createUserAccount(@Header("Authorization") String realAuthToken);

        @GET("/api/accounts")
        Call<IncommAccount> getUserAccount(@Header("Authorization") String realAuthToken);

        @POST("/api/transactions")
        Call<Transaction> createTransaction(@Body TransactionBody data, @Header("Authorization") String realAuthToken);

        @GET("/api/transactions")
        Call<List<Transaction>> getAllTransactions(@Header("Authorization") String realAuthToken);

        @GET("/api/transactions/(id)")
        Call<Transaction> getTransaction(@Path("id") String id, @Header("Authorization") String realAuthToken);

    }


    public List<Transaction> getTransactions() {
        Call<List<Transaction>> transactions = client.getAllTransactions(realAuthToken);

        transactions.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                // The call was a success.  We successfully got a response
                if (response.code() == 200) {
                    int amount = response.body().get(response.body().size() - 1).getAmount();

                    relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayoutyoustoppedhere);
                    layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.you_stopped_here, null);

                    popupWindow = new PopupWindow(container, 400, 400, true);
                    popupWindow.showAtLocation(relativeLayout, Gravity.NO_GRAVITY, 500, 500);

                } else if(response.code() == 401) {

                } else if(response.code() == 404) {
                    // User account doesn't exist, need to create one

                } else {

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
