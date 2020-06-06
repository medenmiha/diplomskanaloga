package com.example.merjenjetornihlastnosticestisc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Kalibracija2korak extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kalibracija2korak);
    }

    public void zaviralBomTipka(View view) {
        Intent intent = new Intent(this, Kalibracija3korak.class);
        startActivity(intent);
        // Ob pritisku na tipko "Zaviral bom" se zažene naslednja aktivnost in sicer Kalibracija3korak
    }
}
