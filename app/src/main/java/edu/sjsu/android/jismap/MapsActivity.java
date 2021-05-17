package edu.sjsu.android.jismap;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final LatLng LOCATION_UNIV = new LatLng(37.335371, -121.881050);
    private final LatLng LOCATION_CS = new LatLng(37.333714, -121.881860);
    private boolean traffic = false;
    private Geocoder geocoder;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    RelativeLayout searchRl;
    CameraUpdate update = null;
    MarkerOptions place1 = new MarkerOptions();
    MarkerOptions place2 = new MarkerOptions();
    Button button;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        searchRl=findViewById(R.id.searchRl);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();

        geocoder = new Geocoder(this, Locale.getDefault());

        Places.initialize(getApplicationContext(),"AIzaSyAc2i5kpsDJAJDkirJq6HnOFI9CxEKJI_M");

        searchRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList= Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG,Place.Field.NAME);
                Intent intent=new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fieldList).build(MapsActivity.this);
                startActivityForResult(intent,100);
            }
        });

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

        //
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


                if(markerList.size()>1){
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
                }else {
                    Toast.makeText(context, "Please add at least 2 markers!",
                            Toast.LENGTH_LONG).show();
                }

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

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    currentLocation = location;
                    //Toast.makeText(getApplicationContext(),currentLocation.getLatitude() + ""+currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    supportMapFragment.getMapAsync(MapsActivity.this);
                }
            }
        });
    }

    public void getLocation(View view){
        GPSTracker tracker = new GPSTracker(this);
        tracker.getLocation();

        LatLng latLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17f));
        mMap.addMarker(markerOptions);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==100&resultCode==RESULT_OK){

            Place place=Autocomplete.getPlaceFromIntent(data);
            update = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 10f);
            mMap.animateCamera(update);
            mMap.addMarker(new MarkerOptions(). position(place.getLatLng()).title(place.getName()));
            // Toast.makeText(this, ""+String.valueOf(place.getLatLng()), Toast.LENGTH_SHORT).show();
        }
        else if (resultCode== AutocompleteActivity.RESULT_ERROR){
            Status status=Autocomplete.getStatusFromIntent(data);
            Toast.makeText(this, ""+status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}