package com.example.merjenjetornihlastnosticestisc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.example.merjenjetornihlastnosticestisc.Kalibracija1korak.odklonX;
import static com.example.merjenjetornihlastnosticestisc.Kalibracija1korak.odklonY;
import static com.example.merjenjetornihlastnosticestisc.Kalibracija1korak.odklonZ;
import static com.example.merjenjetornihlastnosticestisc.Kalibracija3korak.pozitivnoX;
import static com.example.merjenjetornihlastnosticestisc.Kalibracija3korak.pozitivnoY;
import static com.example.merjenjetornihlastnosticestisc.Kalibracija3korak.pozitivnoZ;
import static com.example.merjenjetornihlastnosticestisc.MojeMatematicneOperacije.izracunRezultante;
import static com.example.merjenjetornihlastnosticestisc.MojeMatematicneOperacije.sprozilecNegativni;
import static com.example.merjenjetornihlastnosticestisc.MojeMatematicneOperacije.sprozilecPozitivni;
import static com.example.merjenjetornihlastnosticestisc.MojeMatematicneOperacije.sum;

public class LogikaMedVoznjo extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, SensorEventListener {
    private Sensor mySensor;
    private SensorManager SM;
    int stevec =0;
    double pretvornik = 1000_000_000;
    private static double[][] matrikaPodatkov = new double[4][500];

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    private CountDownTimer mcountDownTimer;
    private static final long START_TIME_IN_MILLIS = 10000000;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private boolean mTimerRunning;
    public static double publicLatitude;
    public static double publicLongitude;
    double standardnaGravtacija= 9.81;

    double pospesekX;
    double pospesekY;
    double pospesekZ;
    static double rezultantaPospeska;
    double casMeritve;

    boolean postopekMeriteve = false;
    static double koncipospesek;
    static double koeficientTrenja;
    static double GPShitrost;

    static DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat mdformat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    static List<List<String>> listOfLists = new ArrayList<List<String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager_med_voznjo);

        SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        // Definiranje objekta ki zajema senzorje v napravi
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // Določitev kateri senzor bo privzet za opazovanje
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
        // Določitev hitrosti osveževanja branja podatkov.
        // V tem primeru čitanje vrednosti pospeškomera v NORMAL RATE, ki je približno 50Hz

        Odstevalnik();

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        FragmentCollectionAdapter swipeAdapter = new FragmentCollectionAdapter(getSupportFragmentManager());
        // Okno ki prkazuje fragmente
        // V mojem primeru sta 2 fragmenta (zemljevid z prikazom rezultatov in fragment s prikazom
        // trenutnega stanja s graficnim prikazom meritev)
        viewPager.setAdapter(swipeAdapter);
        // Premikanje med fragmenti je omogoceno s podrsanjem prsta po zaslonu (levo in desno)
        viewPager.setCurrentItem(0);
        // Privzet fragment je fragment s prikazom osnovnih podatkov in trenutnim stanjem meritev

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void Odstevalnik() {
        mcountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                getMyLocation();

            }

            @Override
            public void onFinish() {

            }
        }.start();
        mTimerRunning = true;
    }
    // V tem primeru je funkcija Odstevalnik uporabljen, ker ima odlično podfukcijo onTick
    // Funkcija onTick se zažene vsakic ko preteče dolocen cas, kar pa je idealno za osvezevanje
    // odčitkov koordianat GPS senzorja (getMyLocation();)

    private void getMyLocation() {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                publicLatitude = mLastLocation.getLatitude();
                publicLongitude = mLastLocation.getLongitude();
                GPShitrost = mLastLocation.getSpeed()*3.6;
                // Ob poznanih koordinatah se izracuna tudi hitrost, ki pa je pomnožena z 3.6
                // za pretvorbo iz m/s v uporabniku bolj prijaznih km/h
            } else {

            }
        } catch (SecurityException e) {
            Toast.makeText(LogikaMedVoznjo.this,
                    "SecurityException:\n" + e.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getMyLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(LogikaMedVoznjo.this,
                "onConnectionSuspended: " + String.valueOf(i),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(LogikaMedVoznjo.this,
                "onConnectionFailed: \n" + connectionResult.toString(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        pospesekX = event.values[0]-odklonX;
        pospesekY = event.values[1]-odklonY;
        pospesekZ = event.values[2]-odklonZ;
        casMeritve = event.timestamp / pretvornik;
        rezultantaPospeska = Math.abs(izracunRezultante(pospesekX, pospesekY, pospesekZ));

        boolean startPogojIzpolnjen = sprozilecPozitivni(rezultantaPospeska, 2);
        boolean finishPogojIzpolnjen = sprozilecNegativni(rezultantaPospeska, 0.5);

        if(startPogojIzpolnjen){
            postopekMeriteve =true;
        }
        if(postopekMeriteve && finishPogojIzpolnjen){
            postopekMeriteve =false;

        }

        if (postopekMeriteve) {
            matrikaPodatkov[0][stevec] = pospesekX;
            matrikaPodatkov[1][stevec] = pospesekY;
            matrikaPodatkov[2][stevec] = pospesekZ;
            matrikaPodatkov[3][stevec] = casMeritve;
            stevec++;
        }

        if(stevec>0) {
            // stevec>0 pogoj mora veljati sicer vprimeru da je stevec=0 matrikaPodatkov[3][stevec-1] =>
            // matrikaPodatkov[3][-1] kar pa je nepravilno
            if (!postopekMeriteve && 1.5 < matrikaPodatkov[3][stevec - 1] - matrikaPodatkov[3][0]) {
                // matrikaPodatkov[3][stevec-1]-matrikaPodatkov[3][0]
                // če je vsaj 1,5 sekunde časa med začetkom matrike podatkov in koncem
                double[] meritveX = Arrays.copyOf(matrikaPodatkov[0], stevec);
                double[] meritveY = Arrays.copyOf(matrikaPodatkov[1], stevec);
                double[] meritveZ = Arrays.copyOf(matrikaPodatkov[2], stevec);
                double povPospesekX = sum(meritveX) / stevec;
                double povPospesekY = sum(meritveY) / stevec;
                double povPospesekZ = sum(meritveZ) / stevec;

                if (pozitivnoX==povPospesekX > 0 && pozitivnoY==povPospesekY > 0 && pozitivnoZ==povPospesekZ > 0) {
                    // Preveri če so povprečne vrednosti komponent pospeška ob meritvi enakega predznaka kot
                    // predznaki komponent pri kalibraciji aplikacije ob zaviranju
                    koncipospesek = izracunRezultante(povPospesekX, povPospesekY, povPospesekZ);
                    koeficientTrenja = koncipospesek / standardnaGravtacija;
                    String trenutniCas = mdformat.format(calendar.getTime());
                    // Po uspešni meritvi, izračunu in zajemu podatkov ob meritvi se izvede še upload
                    DatabaseReference newProductRef = mRootRef.push();
                    // Nalaganje podatkov na server Firebase v kolonah: Latitude;Longitude;Model;KoeficientTrenja;Cas
                    newProductRef.child("Latitude").setValue(publicLatitude);
                    newProductRef.child("Longitude").setValue(publicLongitude);
                    newProductRef.child("Model").setValue(Build.MODEL);
                    // Model oziroma tovarniška oznaka telefona
                    newProductRef.child("KoeficientTrenja").setValue(koeficientTrenja);
                    newProductRef.child("Cas").setValue(trenutniCas);

                    ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            listOfLists.clear();
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                List<String> list = new ArrayList<>();
                                String model = ds.child("Model").getValue(String.class);
                                list.add(model);
                                String koefTrenja = ds.child("KoeficientTrenja").getValue().toString();
                                list.add(koefTrenja);
                                String casMeritve = ds.child("Cas").getValue(String.class);
                                list.add(casMeritve);
                                String meritevLon = ds.child("Longitude").getValue().toString();
                                list.add(meritevLon);
                                String meritevLat = ds.child("Latitude").getValue().toString();
                                list.add(meritevLat);
                                listOfLists.add(list);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    };
                    mRootRef.addListenerForSingleValueEvent(valueEventListener);
                }
            }
        }
        if (!postopekMeriteve){
            stevec=0;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

