package com.example.merjenjetornihlastnosticestisc;


import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;



import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class PodatkiMedVoznjoFragment extends Fragment {

    private TextView mtextLocation;
    private TextView mprikazTextKoncniPospesek;

    private NumberFormat numberFormat = new DecimalFormat("0.000");
    private NumberFormat numberFormatHitrost = new DecimalFormat("0");

    public PodatkiMedVoznjoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_podatki_med_voznjo, container, false);

        mprikazTextKoncniPospesek = v.findViewById(R.id.prikazTextKoncniPospesek);
        mtextLocation = v.findViewById(R.id.prikazGpsHitrosti);

        RelativeLayout relativeLayout = (RelativeLayout) v.findViewById(R.id.prikaz_vekt_pospeska);
        relativeLayout.addView(new PrikazGrafa(getActivity()));
        // Vmestitev prikaza grafa na slednji fragment


        new Timer().scheduleAtFixedRate(new TimerTask() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                String prikazMeritvePospeska = numberFormat.format(LogikaMedVoznjo.koeficientTrenja);
                String hitrostGPS = numberFormatHitrost.format(LogikaMedVoznjo.GPShitrost);

                mtextLocation.setText("GPS hitrost:\n"+hitrostGPS+" km/h");
                mprikazTextKoncniPospesek.setText("Zadnja\nmeritev\n" +prikazMeritvePospeska);
            }
        }, 0, 1000);
        // Vsakih 1000ms = 1s osveži rezultat prikaza zadnje meritve koeficienta trenja in
        // kot dodatno informacijo zraven prikazuje GPS hitrost

        return v;
    }

}
