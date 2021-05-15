package edu.sjsu.android.jismap;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
     RelativeLayout searchRl;
    private GoogleMap mMap;
    private final LatLng LOCATION_UNIV = new LatLng(37.335371, -121.881050);
    private final LatLng LOCATION_CS = new LatLng(37.333714, -121.881860);
    CameraUpdate update = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        searchRl=findViewById(R.id.searchRl);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Places.initialize(getApplicationContext(),"AIzaSyAc2i5kpsDJAJDkirJq6HnOFI9CxEKJI_M");

        searchRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList= Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG,Place.Field.NAME);
                Intent intent=new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fieldList).build(MapsActivity.this);
                startActivityForResult(intent,100);
            }
        });



    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions(). position(LOCATION_CS).title("Find me here!"));
    }

    public void getLocation(View view){
        GPSTracker tracker = new GPSTracker(this);
        tracker.getLocation();
    }

    public void switchView(View view) {

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