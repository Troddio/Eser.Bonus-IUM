package com.example.esercitazioneBonusIUM;


import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.R;


import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static final String PARK = "com.example.progettoIUMcorretto.Parcheggi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        Intent showResult = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(showResult);
        finish();
    }
}


