package com.example.merjenjetornihlastnosticestisc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class Kalibracija1korak extends AppCompatActivity implements SensorEventListener {
    private static final long START_TIME_IN_MILLIS = 5000;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    //Čas kalibracije nastavljen na 5000ms = 5s
    private CountDownTimer mcountDownTimer;
    private boolean mTimerRunning;
    // Odštevalnik časa za postopek kalibracije
    boolean zacetekKaliblracije = false;

    static double odklonX;
    static double odklonY;
    static double odklonZ;
    double[] meritveodklonaX = new double[1000];
    double[] meritveodklonaY = new double[1000];
    double[] meritveodklonaZ = new double[1000];
    int stevec = 0;

    private TextView mTextViewCountDown;
    private Button mButtonZacniKalibracijo;
    private Sensor mySensor;
    private SensorManager SM;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kalibracija1korak);

        SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        // Definiranje objekta ki zajema senzorje v napravi
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // Določitev kateri senzor bo privzet za opazovanje
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
        // Določitev hitrosti osveževanja branja podatkov.
        // V tem primeru čitanje vrednosti pospeškomera v NORMAL RATE, ki je približno 50Hz


        mTextViewCountDown = findViewById(R.id.odstevalnik);
        mButtonZacniKalibracijo = findViewById(R.id.tipkaZacniKalibracijo);
        mButtonZacniKalibracijo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOdstevanjaKalibracije();
            }
        });
        updateCountDownText();
    }


    private void startOdstevanjaKalibracije() {
        zacetekKaliblracije = true;
        mcountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }
            // Odštevanje časa in osveževanje prikaza preostalega časa vsakih 1000ms

            @Override
            public void onFinish() {
                mTimerRunning = false;
                zacetekKaliblracije = false;
                odklonX = MojeMatematicneOperacije.sum(meritveodklonaX)/ stevec;
                odklonY = MojeMatematicneOperacije.sum(meritveodklonaY)/ stevec;
                odklonZ = MojeMatematicneOperacije.sum(meritveodklonaZ)/ stevec;
                openNextActivity();
                // Ko preteče čas kalibracije se izračuna povprečne vrednosti pospeškov zajetih
                // v tem časovnem intervalu in odpre naslednjo aktivnost

            }
        }.start();
        mTimerRunning = true;
        mButtonZacniKalibracijo.setVisibility(View.INVISIBLE);
        mTextViewCountDown.setVisibility(View.VISIBLE);
    }

    private void updateCountDownText() {
        int seconds = (int) (mTimeLeftInMillis / 1000);
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d", seconds);
        mTextViewCountDown.setText(timeLeftFormatted);
    }

    public void openNextActivity() {
        Intent intent2 = new Intent(this, Kalibracija2korak.class);
        startActivity(intent2);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (zacetekKaliblracije && stevec<meritveodklonaX.length) {
            meritveodklonaX[stevec] =event.values[0];
            meritveodklonaY[stevec] =event.values[1];
            meritveodklonaZ[stevec] =event.values[2];
            stevec++;
            // v numerična polja se zapišejo odčitki pospeškomera po vsaki komponenti posebej
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
