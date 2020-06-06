package com.example.merjenjetornihlastnosticestisc;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class FragmentCollectionAdapter extends FragmentPagerAdapter {

    public FragmentCollectionAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PodatkiMedVoznjoFragment();
            case 1:
                return new MapsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
    // Class ki poskrbi za uporabniško izkušnjo med uporabo že kalibriranje aplikaicje
    // Slednji omogoča, da se 2 fragmenta lahko preklapljata s podrsanjem prsta po zaslonu v levo ali desno
}
