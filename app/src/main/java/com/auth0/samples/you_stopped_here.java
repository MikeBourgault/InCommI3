package com.auth0.samples;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

public class you_stopped_here extends AppCompatActivity {

    private RelativeLayout relativeLayout;
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.you_stopped_here, null);

        popupWindow = new PopupWindow(container, 400, 400, true);
        popupWindow.showAtLocation(relativeLayout, Gravity.NO_GRAVITY, 500, 500);


    }
}
