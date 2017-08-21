package com.search.deezer.views.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.search.deezer.R;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("on Create ", "Base Activity");
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(showHomeAsUp());
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setTitle(getTitle());
    }

    protected abstract int getLayoutId();

    protected abstract boolean showHomeAsUp();

}
