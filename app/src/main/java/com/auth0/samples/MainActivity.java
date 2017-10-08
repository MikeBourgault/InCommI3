package com.auth0.samples;

import android.app.Activity;
import android.app.Dialog;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.widget.PopupWindowCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends Activity {

    private TextView token;
    public static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        token = (TextView) findViewById(R.id.token);
        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        token.setText("Not logged in");
        Auth0 auth0 = new Auth0(this);
        auth0.setOIDCConformant(true);
        WebAuthProvider.init(auth0)
                .withScheme("https")
               // .withAudience(String.format("https://incomm-act-mgt.appspot.com", getString(R.string.com_auth0_domain)))
                .withAudience("https://incomm-act-mgt.appspot.com")
                .withScope("openid read:accounts read:transactions write:transactions")
                .start(MainActivity.this, new AuthCallback() {
                    @Override
                    public void onFailure(@NonNull final Dialog dialog) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(final AuthenticationException exception) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onSuccess(@NonNull final Credentials credentials) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                token.setText("Logged in: " + credentials.getAccessToken());
                                Log.d("Jwt token", credentials.getAccessToken());

                            }
                        });
                    }
                });
    }

    /*public interface GitHubClient {
        @GET("/users/{user}/repos")
        Call<List<GitHubRepo>> reposForUser(
                @Path("user") String user
        );
    }

    public class GitHubRepo {
        private int id;
        private String name;

        public GitHubRepo() {
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }*/


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

    private void getTransactions() {
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
