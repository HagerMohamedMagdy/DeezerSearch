package com.search.deezer.views.activity;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.search.deezer.R;
import com.search.deezer.models.data.DeezerApplication;
import com.search.deezer.models.service.ConnectivityReceiver;
import com.search.deezer.presenter.ISplashScreenPresenter;
import com.search.deezer.presenter.SplashScreenImp;
import com.search.deezer.utils.Utility;
import com.search.deezer.views.ISplashScreenView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashScreen extends AppCompatActivity implements ISplashScreenView, ConnectivityReceiver.ConnectivityReceiverListener {
    @BindView(R.id.get_started_Button)
    Button get_started_btn;
    ISplashScreenPresenter splashPresenter;
    @BindView(R.id.parent_view)
    ConstraintLayout parent_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);
        splashPresenter = new SplashScreenImp(this);
        get_started_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ConnectivityReceiver.isConnected()) {
                    splashPresenter.getAuthorization();
                } else {
                    Utility.showSnack(false, parent_view);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        DeezerApplication.getInstance().setConnectivityListener(this);
        DeezerApplication.setCurrentActivity(this);
    }

    @Override
    public void AuthSuccesScinario() {
        Log.e("Sucess","Authorization");
        finish();
        Intent login = new Intent(SplashScreen.this, MainActivity.class);
        startActivity(login);
    }

    @Override
    public void AuthFailScinario() {

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }
}
