package com.auth0.samples;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telecom.Call;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;

import java.util.List;


public class MainActivity extends Activity {

    private TextView token;

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
                .withScheme("demo")
                .withAudience(String.format("https://incomm-act-mgt.appspot.com", getString(R.string.com_auth0_domain)))
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
        Call<Transaction> createTransaction(@Body TransactionBody data, @Header("Authorization") String authToken);

        @GET("/api/transactions")
        Call<List<Transactions>> getAllTransactions(@Header("Authorization") String authToken);

        @GET("/api/transactions/(id)")
        Call<Transaction> getTransaction(@Path("id") String id, @Header("Authorization") String authToken);

    }

    authToken = "Bearer " + getIntent().getExtras().getString("ACCESS_TOKEN");

    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    Retrofit.Builder builder = new Retrofit.Builder().baseUrl(API_BASE_URL).addConverterFactory(GsonConverterFactory.create()
    );

    Retrofit retrofit = builder.client(httpClient.build()).build();
    client = retrofit.create(ApiClient.class);

}


