package com.example.merjenjetornihlastnosticestisc;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import static com.example.merjenjetornihlastnosticestisc.LogikaMedVoznjo.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;
    List<List<String>> listaPodatkov = LogikaMedVoznjo.listOfLists;


    public MapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Prikaz mape na fragmentu
        mView = inflater.inflate(R.layout.fragment_maps, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) mView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize((getContext()));
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true); // Omogočeno da pokaže z modro piko kjer se nahajam
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); // Nastavitev vizualnega stila zemljevida

        for(int i = 0; i< listaPodatkov.size(); i++){
            googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(listaPodatkov.get(i).get(4)) ,
                    Double.parseDouble(listaPodatkov.get(i).get(3)))).title(listaPodatkov.get(i).get(1)));
            // Izris rezultatov meritev na zemljevidu kjer so bile meritve zabeležene
        }

        CameraPosition mojapozicija = CameraPosition.builder().target(new LatLng(publicLatitude, publicLongitude)).zoom(16).bearing(0).tilt(45).build();
        // Določanje pozicije kamere prikaza zemljevida ob zagonu
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(mojapozicija));
        // Pomikanje zemljevida proti dejanski lokaciji uporabnika med vožnjo
    }
}
