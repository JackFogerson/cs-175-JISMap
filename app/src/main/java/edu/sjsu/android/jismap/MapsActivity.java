package edu.sjsu.android.jismap;

import android.content.Context;
import android.graphics.Color;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


private GoogleMap mMap;
    private final LatLng LOCATION_UNIV = new LatLng(37.335371, -121.881050);
    private final LatLng LOCATION_CS = new LatLng(37.333714, -121.881860);
    private boolean traffic = false;
    private Geocoder geocoder;
    MarkerOptions place1 = new MarkerOptions();
    MarkerOptions place2 = new MarkerOptions();
    Button button;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geocoder = new Geocoder(this, Locale.getDefault());
        place1.position(LOCATION_CS);
        place2.position(LOCATION_UNIV);
        button = (Button) findViewById(R.id.draw);

        context = this;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        List<MarkerOptions> markerList = new ArrayList<MarkerOptions>();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                mMap.addMarker(markerOptions);
                markerList.add(markerOptions);

            }
        });


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.clear();
                markerList.clear();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your handler code here
                mMap.addPolyline((new PolylineOptions()).add(markerList.get(0).getPosition(), markerList.get(1).getPosition()).
                        // below line is use to specify the width of poly line.
                                width(5)
                        // below line is use to add color to our poly line.
                        .color(Color.RED)
                        // below line is to make our poly line geodesic.
                        .geodesic(true));
                // on below line we will be starting the drawing of polyline.
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerList.get(0).getPosition(), 13));

                Toast.makeText(context, String.format("%.2f",distance(markerList.get(0).getPosition().latitude, markerList.get(1).getPosition().latitude,
                        markerList.get(0).getPosition().longitude, markerList.get(1).getPosition().longitude)) +" Kilometers is the distance",
                        Toast.LENGTH_LONG).show();

        }
    });


    }

    //Distance between Markerks
    // ref: https://www.geeksforgeeks.org/program-distance-two-points-earth/
    public static double distance(double lat1,
                                  double lat2, double lon1,
                                  double lon2)
    {

        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return(c * r);
    }

    public void getLocation(View view){
        GPSTracker tracker = new GPSTracker(this);
        tracker.getLocation();
    }

    public void switchView(View view) {
        CameraUpdate update = null;
        if (view.getId() == R.id.city) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            update = CameraUpdateFactory.newLatLngZoom(LOCATION_UNIV, 10f);
        }
        else if (view.getId() == R.id.univ) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            update = CameraUpdateFactory.newLatLngZoom(LOCATION_UNIV, 14f);
        }
        else if (view.getId() == R.id.cs) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            update = CameraUpdateFactory.newLatLngZoom(LOCATION_CS, 18f);
        }
        mMap.animateCamera(update);
    }

    public void mapView(View view){
        mMap.setMapType(mMap.MAP_TYPE_NORMAL);
    }

    public void satView(View view){
        mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
    }

    public void terView(View view){
        mMap.setMapType(mMap.MAP_TYPE_TERRAIN);
    }

    public void trafficView(View view){
        if(traffic){
            mMap.setTrafficEnabled(false);
            traffic = false;
        }
        else{
            mMap.setTrafficEnabled(true);
            traffic = true;
        }
    }



}