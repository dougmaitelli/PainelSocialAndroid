package br.com.painelsocial;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.painelsocial.model.Request;
import br.com.painelsocial.ws.Ws;


public class NewRequestActivity extends AppCompatActivity {

    public static final String LOCATION_EXTRA = "location";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SELECT_IMAGE = 2;
    public static final String RESULT_REQUEST = "request";

    private LatLng requestLocation = null;

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;

    private TextView addressInfo;
    private EditText inputDescription;
    private List<Bitmap> images;
    private LinearLayout previewPictures;
    private Button takePictureButton;
    private Button selectPictureButton;
    private Button buttonSendRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_request);

        Bundle bundle = getIntent().getExtras();

        requestLocation = (LatLng) bundle.get(LOCATION_EXTRA);

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
        inputDescription = (EditText) findViewById(R.id.inputDescription);
        images = new ArrayList<>();
        previewPictures = (LinearLayout) findViewById(R.id.previewPictures);

        takePictureButton = (Button) findViewById(R.id.takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        selectPictureButton = (Button) findViewById(R.id.selectPictureButton);
        selectPictureButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectPicture();
            }
        });

        buttonSendRequest = (Button) findViewById(R.id.sendRequest);
        buttonSendRequest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                createRequest();
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
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bitmap bitmap = getPicture(mCurrentPhotoPath);

                addPicture(bitmap);
            } else if (requestCode == REQUEST_SELECT_IMAGE) {
                Uri selectedImageUri = data.getData();
                Bitmap bitmap = getPicture(selectedImageUri);

                addPicture(bitmap);
            }
        }
    }

    private void updateMapLocation(LatLng latlng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
        mMap.addMarker(new MarkerOptions().position(latlng));

        String addressText = null;
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(latlng.latitude, latlng.longitude, 1);
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
            File photoFile = null;
            try {
                photoFile = createTempImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void selectPicture() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_SELECT_IMAGE);
    }

    //----------------------------------------------------------------------------------------------
    // Image Methods
    //----------------------------------------------------------------------------------------------
    private String mCurrentPhotoPath;

    private File createTempImageFile() throws IOException {
        File storageDir = Environment.getExternalStorageDirectory();
        storageDir = new File(storageDir.getAbsolutePath() + "/.temp/");

        if(!storageDir.exists()) {
            storageDir.mkdir();
        }

        File image = File.createTempFile("picture", ".jpg", storageDir);

        mCurrentPhotoPath = image.getAbsolutePath();

        image.delete();

        return image;
    }

    private Bitmap getPicture(Object photoPath) {
        // Get the dimensions of the View
        int targetH = previewPictures.getHeight();

        Bitmap bitmap = null;
        try {
            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            if (photoPath instanceof String) {
                BitmapFactory.decodeFile((String) photoPath, bmOptions);
            } else if (photoPath instanceof Uri) {
                InputStream imageStream = getContentResolver().openInputStream((Uri) photoPath);
                BitmapFactory.decodeStream(imageStream, null, bmOptions);
            }
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = photoH / targetH;

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            if (photoPath instanceof String) {
                bitmap = BitmapFactory.decodeFile((String) photoPath, bmOptions);
            } else if (photoPath instanceof Uri) {
                    InputStream imageStream = getContentResolver().openInputStream((Uri) photoPath);
                    bitmap = BitmapFactory.decodeStream(imageStream, null, bmOptions);
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        return bitmap;
    }
    //----------------------------------------------------------------------------------------------

    private void addPicture(Bitmap bitmap) {
        images.add(bitmap);

        ImageView previewPicture = new ImageView(this);
        previewPicture.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        previewPicture.setImageBitmap(bitmap);
        previewPicture.setAdjustViewBounds(true);

        previewPictures.addView(previewPicture);
    }

    private void createRequest() {
        final String description = inputDescription.getText().toString();

        new LoaderTask<NewRequestActivity>(this, true) {

            private Request request;

            @Override
            public void process() {
                try {
                    request = Ws.createRequest(description, requestLocation.latitude, requestLocation.longitude, images);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onComplete() {
                Bundle data = new Bundle();
                data.putSerializable(RESULT_REQUEST, request);
                Intent intent = new Intent();
                intent.putExtras(data);
                setResult(RESULT_OK, intent);
                finish();
            }
        };
    }

}
