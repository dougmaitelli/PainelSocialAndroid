package br.com.painelsocial;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import br.com.painelsocial.model.Comment;
import br.com.painelsocial.model.Request;
import br.com.painelsocial.view.CommentView;
import br.com.painelsocial.ws.Ws;


public class ViewRequestActivity extends AppCompatActivity {

    public static final String REQUEST_EXTRA = "request";

    private Request request;
    private LatLng requestLocation = null;

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;

    private TextView addressInfo;
    private Button minusButton;
    private Button plusButton;
    private TextView textDescription;
    private LinearLayout previewPictures;

    private LinearLayout comments;
    private EditText inputComment;
    private Button sendComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request);

        Bundle bundle = getIntent().getExtras();

        String _id = (String) bundle.get(REQUEST_EXTRA);
        loadRequest(_id);

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
        minusButton = (Button) findViewById(R.id.minusButton);
        plusButton = (Button) findViewById(R.id.plusButton);
        textDescription = (TextView) findViewById(R.id.textDescription);
        previewPictures = (LinearLayout) findViewById(R.id.previewPictures);

        comments = (LinearLayout) findViewById(R.id.comments);
        inputComment = (EditText) findViewById(R.id.inputComment);
        sendComment = (Button) findViewById(R.id.sendComment);

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minus();
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plus();
            }
        });

        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = inputComment.getText().toString();
                inputComment.setText("");
                postComment(commentText);
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

    public void minus() {
        new LoaderTask<ViewRequestActivity>(this, true) {

            @Override
            public void process() {
                try {
                    Ws.minusRequest(request.get_id());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onComplete() {
                if (request.getCurrentVote() != null && request.getCurrentVote() == 1) {
                    request.setPlusCount(request.getPlusCount() - 1);
                }

                request.setMinusCount(request.getMinusCount() + 1);

                request.setCurrentVote(0);
                refreshVote();
            }
        };
    }

    public void plus() {
        new LoaderTask<ViewRequestActivity>(this, true) {

            @Override
            public void process() {
                try {
                    Ws.plusRequest(request.get_id());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onComplete() {
                if (request.getCurrentVote() != null && request.getCurrentVote() == 0) {
                    request.setMinusCount(request.getMinusCount() - 1);
                }

                request.setPlusCount(request.getPlusCount() + 1);

                request.setCurrentVote(1);
                refreshVote();
            }
        };
    }

    public void postComment(final String commentText) {
        new LoaderTask<ViewRequestActivity>(this, true) {

            private Comment comment;

            @Override
            public void process() {
                try {
                    comment = Ws.commentRequest(request.get_id(), commentText);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onComplete() {
                if (comment != null) {
                    addComment(comment);
                }
            }
        };
    }

    public void addComment(Comment comment) {
        CommentView commentView = new CommentView(this, comment);

        comments.addView(commentView);
    }

    public void refreshVote() {
        minusButton.setText("- " + request.getMinusCount());
        plusButton.setText("+ " + request.getPlusCount());

        if (request.getCurrentVote() != null) {
            if (request.getCurrentVote() == 0) {
                minusButton.setEnabled(false);
                plusButton.setEnabled(true);
            } else {
                minusButton.setEnabled(true);
                plusButton.setEnabled(false);
            }
        }
    }

    private void loadRequest(final String _id) {
        new LoaderTask<ViewRequestActivity>(this, true) {

            @Override
            public void process() {
                try {
                    request = Ws.loadRequest(_id);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onComplete() {
                if (request != null) {
                    requestLocation = new LatLng(request.getLatitude(), request.getLongitude());

                    textDescription.setText(request.getDescription());

                    refreshVote();

                    for (String image : request.getImages()) {
                        if (image == null) {
                            continue;
                        }

                        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        ImageView previewPicture = new ImageView(getContext());
                        previewPicture.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        previewPicture.setImageBitmap(bitmap);
                        previewPicture.setAdjustViewBounds(true);

                        previewPictures.addView(previewPicture);
                    }

                    for (Comment comment : request.getComments()) {
                        if (comment == null) {
                            continue;
                        }

                        addComment(comment);
                    }

                    updateMapLocation(requestLocation);
                }
            }
        };
    }
}
