package com.auth0.samples;

import android.app.Activity;
import android.app.Dialog;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    public static boolean ifDebitInLast10Secs = false;

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
        Call<IncommAccount> createUserAccount(@Header("Authorization") String authToken);

        @GET("/api/accounts")
        Call<IncommAccount> getUserAccount(@Header("Authorization") String authToken);

        @POST("/api/transactions")
        Call<List<Transaction>> createTransaction(@Body Transaction data, @Header("Authorization") String authToken);

        @GET("/api/transactions")
        Call<List<Transaction>> getAllTransactions(@Header("Authorization") String authToken);

        @GET("/api/transactions/(id)")
        Call<List<Transaction>> getTransaction(@Path("id") String id, @Header("Authorization") String authToken);

    }

    String authToken = "Bearer " + getIntent().getExtras().getString("ACCESS_TOKEN");

    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    Retrofit.Builder builder = new Retrofit.Builder().baseUrl("https://incomm-act-mgt.appspot.com").addConverterFactory(GsonConverterFactory.create()
    );

    Retrofit retrofit = builder.client(httpClient.build()).build();
    IncommAccount client = retrofit.create(OkHttpClient.class);

    private void getUserAccount() {
        Call<IncommAccount> call = client.getUserAccount(authToken);

        call.enqueue(new Callback<IncommAccount>() {
            @Override
            public void onResponse(Call<IncommAccount> call, Response<IncommAccount> response) {
                // The call was a success.  We successfully got a response
                if (response.code() == 200) {
                    int balance = response.body().getBalance();
                    ((TextView) findViewById(R.id.balanceText)).setText(String.format("$%d.%02d", balance / 100, balance % 100));
                } else if(response.code() == 401) {
                    ((TextView) findViewById(R.id.balanceText)).setText("Authorization Error.");
                } else if(response.code() == 404) {
                    // User account doesn't exist, need to create one
                    createUserAccount();
                } else {
                    ((TextView) findViewById(R.id.balanceText)).setText("Something went wrong with account details.");
                }

            }

            @Override
            public void onFailure(Call<IncommAccount> call, Throwable t) {
                // The call failed.
                // TODO: Handle the Error
            }
        });
    }

    private void createUserAccount() {
        Call<IncommAccount> call = client.createUserAccount(authToken);

        call.enqueue(new Callback<IncommAccount>() {
            @Override
            public void onResponse(Call<IncommAccount> call, Response<IncommAccount> response) {
                if (response.code() == 200) {
                    ((TextView) findViewById(R.id.balanceText)).setText(String.format("$%d.%02d", balance / 100, balance % 100));
                }
            }
        });
    }

}
