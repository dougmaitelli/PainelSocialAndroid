package br.com.painelsocial;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class NewRequestActivity extends FragmentActivity {

    public static final String LOCATION_EXTRA = "location";
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private Location requestLocation = null;

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;

    private TextView addressInfo;
    private ImageView previewPicture;
    private Button takePictureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_request);

        requestLocation = (Location) getIntent().getExtras().get(LOCATION_EXTRA);

        FragmentManager fm = getSupportFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.previewMap);
        if (mapFragment == null) {
            GoogleMapOptions options = new GoogleMapOptions();
            options.mapType(GoogleMap.MAP_TYPE_NORMAL);
            options.liteMode(true);
            options.mapToolbarEnabled(false);

            mapFragment = SupportMapFragment.newInstance(options);
            fm.beginTransaction().replace(R.id.previewMap, mapFragment).commit();
        }

        addressInfo = (TextView) findViewById(R.id.addressInfo);

        previewPicture = (ImageView) findViewById(R.id.previewPicture);

        takePictureButton = (Button) findViewById(R.id.takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mMap == null) {
            mMap = mapFragment.getMap();

            if (requestLocation != null) {
                updateMapLocation(requestLocation);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            previewPicture.setImageBitmap(imageBitmap);
        }
    }

    private void updateMapLocation(Location location) {
        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
        mMap.addMarker(new MarkerOptions().position(latlng));

        String addressText = null;
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0) {
                addressText = addresses.get(0).getSubAdminArea();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        addressInfo.setText(addressText);
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
}
