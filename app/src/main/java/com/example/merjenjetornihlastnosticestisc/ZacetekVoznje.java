package com.example.merjenjetornihlastnosticestisc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ZacetekVoznje extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zacetek_voznje);
    }

    public void zacniVoznjoTipka(View view) {
        Intent intent = new Intent(this, LogikaMedVoznjo.class);
        startActivity(intent);
    }
}
