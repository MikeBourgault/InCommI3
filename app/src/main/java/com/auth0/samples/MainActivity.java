package com.auth0.samples;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
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

    public String authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6Ik9VVXhRVVF4UXpGQ1JrWTVSVUUwUWpORk0wVTJPRGt6UmpNME9VSTRORGRHTnpkR056YzFOUSJ9.eyJodHRwczovL2luY29tbS1hY3QtbWd0LmFwcHNwb3QuY29tL2p0aSI6ImViMjliYWM1LWQ4MzAtNDJlMi1hYWExLTkxMGNjN2E3MjRlNCIsImlzcyI6Imh0dHBzOi8vamdlb3JnZS1pbmNvbW0uYXV0aDAuY29tLyIsInN1YiI6Imdvb2dsZS1vYXV0aDJ8MTEyNTE1OTQ1NTI5MDMyMTM1NTg4IiwiYXVkIjpbImh0dHBzOi8vaW5jb21tLWFjdC1tZ3QuYXBwc3BvdC5jb20iLCJodHRwczovL2pnZW9yZ2UtaW5jb21tLmF1dGgwLmNvbS91c2VyaW5mbyJdLCJpYXQiOjE1MDc0NDU2OTgsImV4cCI6MTUwNzUzMjA5OCwiYXpwIjoicU5TREREVEFvNFc1aHg1M0ozbVEzOUYzNjkwWkgyU0kiLCJzY29wZSI6Im9wZW5pZCByZWFkOmFjY291bnQgcmVhZDp0cmFuc2FjdGlvbnMgd3JpdGU6dHJhbnNhY3Rpb25zIn0.bZ7d-oLVrwnpkOzF2L0B3Hy2LianXhWom9ryHVkhgO_cxY9bydIC7vWMSizW6V_7F6kDOhNeN7GX_ZjaaEqT-YLu62A1elLC2GheEEtcjZG7aXs2tiVybYL24ZVP0WMGarHrFDVPP266ZIgoEGxsuP3Igz0-HGjkI9mFWf9UsKW8ESddJPF2hXBhHBlMAFLfK1V3K9HxRgJZfQUh7cGf1sE9TgPSxZYI9OlcXpbU1Ukp8_qTh7__L9PbnTGRh0Gb4DzLbg4Ced5mxtFkFqpajfAOfOELcqLIZmhGFejhCQXL3Bg-Ag42cwZFcsjccLvIQ_xL5T5BuDC9EF7E3AOWew";







    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Button loginButton = (Button)findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        }

    private void login() {
        Log.d("msg","I'm at login start.");
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
                                Log.d("msg","I'm at onFailure Run.");
                            }
                        });
                    }

                    @Override
                    public void onFailure(final AuthenticationException exception) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d("msg","I'm at onFailure Run Auth Exc");
                            }
                        });
                    }

                    @Override
                    public void onSuccess(@NonNull final Credentials credentials) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Log.d("msg","I'm at onSuccess Start");
                                Intent intentService = new Intent(MainActivity.this, debit_Transaction_Notification_Service.class);
                                startService(intentService);

                                Intent intentActivity = new Intent(MainActivity.this, SecondActivity.class);
                                startActivity(intentActivity);

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





}
