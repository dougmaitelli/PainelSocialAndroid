package br.com.painelsocial;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import br.com.painelsocial.response.CreateRequestResponse;
import br.com.painelsocial.ws.Ws;

public class MapFragment extends Fragment {

    private static final int REQUEST_NEW_REQUEST = 1;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location currentLocation = null;

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.map_fragment, null);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        locationManager = (LocationManager) getActivity().getSystemService(Activity.LOCATION_SERVICE);

        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                currentLocation = location;
            }

            @Override
            public void onProviderDisabled(String provider) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

        FragmentManager fm = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, mapFragment).commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mMap == null) {
            mMap = mapFragment.getMap();

            if (currentLocation != null) {
                focusRegion(currentLocation);
            }

            refresh();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_map, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                refresh();
                break;
            case R.id.menu_add:
                Intent newRequest = new Intent(getActivity(), NewRequestActivity.class);
                newRequest.putExtra(NewRequestActivity.LOCATION_EXTRA, currentLocation);
                startActivityForResult(newRequest, REQUEST_NEW_REQUEST);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void focusRegion(Location location) {
        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13));
    }

    private void refresh() {
        new LoaderTask<MainActivity>((MainActivity) getActivity(), true) {

            CreateRequestResponse.Request[] requests = null;

            @Override
            public void process() {
                try {
                    requests = Ws.getAllRequests();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onComplete() {
                if (requests != null) {
                    for (CreateRequestResponse.Request r : requests) {
                        if (r.getLatitude() != null && r.getLongitude() != null) {
                            mMap.addMarker(new MarkerOptions().position(new LatLng(r.getLatitude(), r.getLongitude())));
                        }
                    }
                }
            }
        };
    }

}
