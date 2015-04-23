package edu.uic.ibeis_tourist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.uic.ibeis_tourist.interfaces.LocalDatabaseInterface;
import edu.uic.ibeis_tourist.local_database.LocalDatabase;
import edu.uic.ibeis_tourist.model.Location;
import edu.uic.ibeis_tourist.model.PictureInfo;
import edu.uic.ibeis_tourist.utils.FileUtils;

public class MyPicturesMapActivity extends FragmentActivity {

    private static final String LOCATION = "location";

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocalDatabaseInterface localDb;

    private Location location;

    static private ArrayList<Marker> markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        markers = new ArrayList<Marker>();
        System.out.println("MyPicturesMapActivity created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pictures_map);

        if (savedInstanceState != null) {
            location = savedInstanceState.getParcelable(LOCATION);
        }
        else {
            Intent intent = getIntent();
            location = intent.getParcelableExtra("location");
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
            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

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
            try {
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(p.getPosition())
                        .title(p.getIndividualName())
                        .snippet("Species: " + p.getIndividualSpecies())
                        .icon(BitmapDescriptorFactory.fromBitmap(FileUtils.getImageBitmap(
                                p.getFileName(),
                                128,
                                128,
                                Bitmap.Config.RGB_565
                        )))
                        );
                marker.showInfoWindow();
                markers.add(marker);
            }
            catch(IOException e){
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(p.getPosition())
                        .title(p.getIndividualName())
                        .snippet("Species: " + p.getIndividualSpecies())
                        );
                marker.showInfoWindow();
                markers.add(marker);
            }
        }
    }

    private class CustomInfoWindowAdapter implements InfoWindowAdapter {

        private View view;

        public CustomInfoWindowAdapter() {
            view = getLayoutInflater().inflate(R.layout.custom_mapwindow_layout,
                    null);
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        @Override
        public View getInfoWindow(final Marker marker) {

            final String title = marker.getTitle();
            final TextView titleUi = ((TextView) view.findViewById(R.id.title));
            if (title != null) {
                titleUi.setText(title);
            } else {
                titleUi.setText("");
            }

            final String snippet = marker.getSnippet();
            final TextView snippetUi = ((TextView) view
                    .findViewById(R.id.snippet));
            if (snippet != null) {
                snippetUi.setText(snippet);
            } else {
                snippetUi.setText("");
            }

            return view;
        }
    }
}
