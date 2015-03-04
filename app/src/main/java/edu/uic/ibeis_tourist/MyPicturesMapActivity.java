package edu.uic.ibeis_tourist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import edu.uic.ibeis_tourist.interfaces.LocalDatabaseInterface;
import edu.uic.ibeis_tourist.local_database.LocalDatabase;
import edu.uic.ibeis_tourist.model.Location;
import edu.uic.ibeis_tourist.model.PictureInfo;

public class MyPicturesMapActivity extends FragmentActivity {

    private static final String LOCATION = "location";

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocalDatabaseInterface localDb;

    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("MyPicturesMapActivity created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pictures_map);

        if (savedInstanceState != null) {
            location = savedInstanceState.getParcelable(LOCATION);
            System.out.println("(savedInstanceState) Location: " + location.getName());
        }
        else {
            Intent intent = getIntent();
            location = intent.getParcelableExtra("location");
            System.out.println("(getIntent) Location: " + location.getName());
        }
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(LOCATION, location);
        super.onSaveInstanceState(outState);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.my_pictures_map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                localDb = new LocalDatabase();
                localDb.getAllPictures(this);

                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    public void showPictures(List<PictureInfo> pictureInfoList) {
        LatLngBounds bounds;

        if (location != null) {
            bounds = location.getBounds();
        }
        else {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (PictureInfo p : pictureInfoList) {
                builder.include(p.getPosition());
            }
            bounds = builder.build();
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
        addMarkers(pictureInfoList);
    }

    public void addMarkers(List<PictureInfo> pictureInfoList) {
        for (PictureInfo p : pictureInfoList) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(p.getPosition())
                    .title(p.getIndividualName())
                    .snippet("Species: " + p.getIndividualSpecies()));
            marker.showInfoWindow();
        }
    }
}
