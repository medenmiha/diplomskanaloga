package com.example.merjenjetornihlastnosticestisc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ZagonAplikacije extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zagon_aplikacije);

    }

    public void naslednjaDejavnost(View view) {
        Intent intent = new Intent(this, KalibracijaInSprejemDovoljenj.class);
        startActivity(intent);
    }

}
