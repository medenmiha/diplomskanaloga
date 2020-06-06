package com.example.merjenjetornihlastnosticestisc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;

import static com.example.merjenjetornihlastnosticestisc.Kalibracija1korak.odklonX;
import static com.example.merjenjetornihlastnosticestisc.Kalibracija1korak.odklonY;
import static com.example.merjenjetornihlastnosticestisc.Kalibracija1korak.odklonZ;
import static com.example.merjenjetornihlastnosticestisc.MojeMatematicneOperacije.izracunRezultante;
import static com.example.merjenjetornihlastnosticestisc.MojeMatematicneOperacije.sprozilecNegativni;
import static com.example.merjenjetornihlastnosticestisc.MojeMatematicneOperacije.sprozilecPozitivni;
import static com.example.merjenjetornihlastnosticestisc.MojeMatematicneOperacije.sum;

public class Kalibracija3korak extends AppCompatActivity implements SensorEventListener{
    private Sensor mySensor;
    private SensorManager SM;
    int stevec;

    double pospesekX;
    double pospesekY;
    double pospesekZ;
    static double rezultantaPospeska;
    double casMeritve;
    double pretvornik = 1000_000_000;
    boolean postopekMeritve = false;
    private static double[][] matrikaPodatkov = new double[4][1000];

    static boolean pozitivnoX;
    static boolean pozitivnoY;
    static boolean pozitivnoZ;
    boolean zeZagnano= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kalibracija3korak);

        SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        // Definiranje objekta ki zajema senzorje v napravi
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // Določitev kateri senzor bo privzet za opazovanje
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
        // Določitev hitrosti osveževanja branja podatkov.
        // V tem primeru čitanje vrednosti pospeškomera v NORMAL RATE, ki je približno 50Hz
    }

    public void openNextActivity() {
        Intent intent3 = new Intent(this, ZacetekVoznje.class);
        startActivity(intent3);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        pospesekX = event.values[0]-odklonX;
        pospesekY = event.values[1]-odklonY;
        pospesekZ = event.values[2]-odklonZ;
        // Korigirani pospeški z odštetim odklonom, ki je bil določen pri Kalibracija1korak
        casMeritve = event.timestamp / pretvornik;
        // Pretvarjanje sistemskega časa iz nanosekund v sekunde
        rezultantaPospeska = Math.abs(izracunRezultante(pospesekX, pospesekY, pospesekZ));
        // Izračunan rezultante pospeša vseh komponent, da lahko določimo skupni pospešek, ki je ključen
        // za sprožitev izvajanja meritev

        boolean startPogojIzpolnjenKalibracija = sprozilecPozitivni(rezultantaPospeska, 3);
        // Opazuje kdaj rezultanta pospeška preseže vrednost 3 m/s^2
        // Če je ta vrednost presežena je štartni pogoj za izvajanje meritev izpolnjen
        boolean finishPogojIzpolnjenKalibracija = sprozilecNegativni(rezultantaPospeska, 0.5);
        // Opazuje kdaj rezultanta pospeška pade pod vrednost 0.5 m/s^2

        if(!zeZagnano)
        // Da bi se izognil ponovnem zagonu spodnjih funkicij je ustvarjen pogoj, ki varuje, da
            // se meritev izvede samo enkrat
        {
        if(startPogojIzpolnjenKalibracija){
            postopekMeritve =true;
        }
        if(postopekMeritve && finishPogojIzpolnjenKalibracija){
            postopekMeritve =false;

        }

        if (postopekMeritve) {
            matrikaPodatkov[0][stevec] = pospesekX;
            matrikaPodatkov[1][stevec] = pospesekY;
            matrikaPodatkov[2][stevec] = pospesekZ;
            matrikaPodatkov[3][stevec] = casMeritve;
            stevec++;
        }

        if(stevec>0){
            // stevec>0 pogoj mora veljati sicer vprimeru, da je stevec=0 matrikaPodatkov[3][stevec-1] =>
            // => matrikaPodatkov[3][-1] kar pa je nepravilno
            if (!postopekMeritve && 1.5<matrikaPodatkov[3][stevec-1]-matrikaPodatkov[3][0]){
                // matrikaPodatkov[3][stevec-1]-matrikaPodatkov[3][0]
                // če je vsaj 1,5 sekunde časa med zacetkom matrike podatkov in koncem matrike podatkov
                // in je postopek meritve že zaključen
                double [] meritveX = Arrays.copyOf(matrikaPodatkov[0], stevec);
                double [] meritveY = Arrays.copyOf(matrikaPodatkov[1], stevec);
                double [] meritveZ = Arrays.copyOf(matrikaPodatkov[2], stevec);
                double povPospesekX = sum(meritveX)/stevec;
                double povPospesekY = sum(meritveY)/stevec;
                double povPospesekZ = sum(meritveZ)/stevec;
                // Izračun povprečnega pospeška ob zaviranju za vsako komponento posebej
                pozitivnoX = povPospesekX > 0;
                pozitivnoY = povPospesekY > 0;
                pozitivnoZ = povPospesekZ > 0;
                // Določitec true/false vrednosti za vsako komponento posebej
                // Če je povprečna vrednost komponente pospeška negativna => false
                // Če je povprečna vrednost komponente pospeška pozitivna => true
                zeZagnano = true;
                openNextActivity();
                // Zagon naslednje aktivnosti po uspešni kalibraciji

                stevec=0;
            }}
        if (!postopekMeritve){
            stevec=0;
        }}
    }




    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
