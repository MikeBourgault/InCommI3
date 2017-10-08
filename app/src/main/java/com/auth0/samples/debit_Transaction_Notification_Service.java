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
    private String lastId = "";
    public String authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6Ik9VVXhRVVF4UXpGQ1JrWTVSVUUwUWpORk0wVTJPRGt6UmpNME9VSTRORGRHTnpkR056YzFOUSJ9.eyJodHRwczovL2luY29tbS1hY3QtbWd0LmFwcHNwb3QuY29tL2p0aSI6ImViMjliYWM1LWQ4MzAtNDJlMi1hYWExLTkxMGNjN2E3MjRlNCIsImlzcyI6Imh0dHBzOi8vamdlb3JnZS1pbmNvbW0uYXV0aDAuY29tLyIsInN1YiI6Imdvb2dsZS1vYXV0aDJ8MTEyNTE1OTQ1NTI5MDMyMTM1NTg4IiwiYXVkIjpbImh0dHBzOi8vaW5jb21tLWFjdC1tZ3QuYXBwc3BvdC5jb20iLCJodHRwczovL2pnZW9yZ2UtaW5jb21tLmF1dGgwLmNvbS91c2VyaW5mbyJdLCJpYXQiOjE1MDc0NDU2OTgsImV4cCI6MTUwNzUzMjA5OCwiYXpwIjoicU5TREREVEFvNFc1aHg1M0ozbVEzOUYzNjkwWkgyU0kiLCJzY29wZSI6Im9wZW5pZCByZWFkOmFjY291bnQgcmVhZDp0cmFuc2FjdGlvbnMgd3JpdGU6dHJhbnNhY3Rpb25zIn0.bZ7d-oLVrwnpkOzF2L0B3Hy2LianXhWom9ryHVkhgO_cxY9bydIC7vWMSizW6V_7F6kDOhNeN7GX_ZjaaEqT-YLu62A1elLC2GheEEtcjZG7aXs2tiVybYL24ZVP0WMGarHrFDVPP266ZIgoEGxsuP3Igz0-HGjkI9mFWf9UsKW8ESddJPF2hXBhHBlMAFLfK1V3K9HxRgJZfQUh7cGf1sE9TgPSxZYI9OlcXpbU1Ukp8_qTh7__L9PbnTGRh0Gb4DzLbg4Ced5mxtFkFqpajfAOfOELcqLIZmhGFejhCQXL3Bg-Ag42cwZFcsjccLvIQ_xL5T5BuDC9EF7E3AOWew";





    @Override
    public void onCreate() {
        super.onCreate();

        Toast.makeText(this, "Transaction Notification Service Created", Toast.LENGTH_SHORT).show();

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
                            processTransactions();

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

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }

    public interface ApiClient {
        @POST("/api/accounts")
        Call<IncommAccount> createUserAccount(@Header("Authorization") String authToken);

        @GET("/api/accounts")
        Call<IncommAccount> getUserAccount(@Header("Authorization") String authToken);

        @GET("/api/transactions")
        Call<List<Transaction>> getAllTransactions(@Header("Authorization") String authToken);

        @GET("/api/transactions/(id)")
        Call<Transaction> getTransaction(@Path("id") String id, @Header("Authorization") String authToken);

    }


    public void processTransactions() {
        Call<List<Transaction>> transactions = client.getAllTransactions(authToken);

        transactions.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                // The call was a success.  We successfully got a response
                if (response.code() == 200) {
                    Transaction lastTransaction = response.body().get(response.body().size() - 1);
                    if (lastTransaction.getType() == "debit") {
                        if(lastId == "") {
                            Log.d("msg", "1I made it!!!!!!!!!!!!");
                            lastId = lastTransaction.getId();
                        }
                        else if (lastTransaction.getId() != lastId) {
                            Log.d("msg", "I made it!!!!!!!!!!!!");
                            Intent intent = new Intent(debit_Transaction_Notification_Service.this, you_stopped_here.class);
                            startActivity(intent);

                        }
                    }

                    return;
                } else if(response.code() == 401) {
                    return;
                } else if(response.code() == 404) {
                    // User account doesn't exist, need to create one
                    return ;
                } else {
                    return ;
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                // The call failed.
                // TODO: Handle the Error
            }
        });

    }
    public void onDestroy() {
        super.onDestroy();
        boolean isRunning = false;
    }


}
