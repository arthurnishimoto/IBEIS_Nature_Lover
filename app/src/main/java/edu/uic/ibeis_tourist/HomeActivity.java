package edu.uic.ibeis_tourist;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.GregorianCalendar;

import edu.uic.ibeis_tourist.enums.ActivityRequestCode;
import edu.uic.ibeis_tourist.enums.GpsEvent;
import edu.uic.ibeis_tourist.interfaces.LocalDatabaseInterface;
import edu.uic.ibeis_tourist.local_database.LocalDatabase;
import edu.uic.ibeis_tourist.model.Location;
import edu.uic.ibeis_tourist.services.GpsService;
import edu.uic.ibeis_tourist.utils.FileUtils;


public class HomeActivity extends ActionBarActivity {
    private static final String CUR_LAT = "currentLatitude";
    private static final String CUR_LON = "currentLongitude";
    private static final String IMG_FILE_NAME = "imageFileName";
    private static final String LOCATION_DETECTED = "locationDetected";
    private static final String LOCATION = "location";

    private Location location;

    private Double currentLatitude;
    private Double currentLongitude;
    private boolean gpsEnabled;

    private boolean takingPicture;
    private boolean locationDetected;

    private String imageFileName;

    private LocalDatabaseInterface localDb;

    /**
     * Broadcast receiver for GPS events
     */
    private BroadcastReceiver gpsEventsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int gpsEvent = bundle.getInt("gpsEvent");

                if (gpsEvent == GpsEvent.GPS_ENABLED.getValue()) {
                    gpsEnabled();
                }
                else if (gpsEvent == GpsEvent.GPS_DISABLED.getValue()) {
                    gpsDisabled();
                }
                else if (gpsEvent == GpsEvent.LOCATION_CHANGED.getValue()) {
                    locationChanged(bundle.getDouble("lat"), bundle.getDouble("lon"));
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("HomeActivity: onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (savedInstanceState != null && takingPicture) {
            String lat = savedInstanceState.getString(CUR_LAT);
            String lon = savedInstanceState.getString(CUR_LON);

            if(lat != null) {
                currentLatitude = Double.parseDouble(lat);
            }
            if(lon != null) {
                currentLongitude = Double.parseDouble(lon);
            }
            imageFileName = savedInstanceState.getString(IMG_FILE_NAME);
            locationDetected = savedInstanceState.getBoolean(LOCATION_DETECTED);
            location = savedInstanceState.getParcelable(LOCATION);
        }
    }

    @Override
    protected  void onStart() {
        System.out.println("HomeActivity: onStart");
        super.onStart();

        if (!takingPicture) {
            // Register GPS events receiver
            registerReceiver(gpsEventsReceiver, new IntentFilter("edu.uic.ibeis_tourist.broadcast_gps_event"));
            // Start GPS Service
            startService(new Intent(this, GpsService.class));
        }

        takingPicture = false;
    }

    @Override
    protected void onResume() {
        System.out.println("HomeActivity: onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        System.out.println("HomeActivity: onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        System.out.println("HomeActivity: onStop");

        if (!takingPicture) { // Check not interacting with camera app

            // Unregister GPS events receiver
            try {
                unregisterReceiver(gpsEventsReceiver);
            } catch (IllegalArgumentException e) {
                // TODO handle exception: receiver not registered
                e.printStackTrace();
            }
            // Stop GPS Service
            stopService(new Intent(this, GpsService.class));
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        System.out.println("HomeActivity: onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        System.out.println("HomeActivity: onSaveInstanceState");

        outState.putString(CUR_LAT, currentLatitude!=null ? currentLatitude.toString() : null);
        outState.putString(CUR_LON, currentLongitude!=null ? currentLongitude.toString() : null);
        outState.putString(IMG_FILE_NAME, imageFileName);
        outState.putBoolean(LOCATION_DETECTED, locationDetected);
        outState.putParcelable(LOCATION, location);

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent settingsView = new Intent(this, SettingsActivity.class);
                settingsView.putExtra("location", location);
                startActivity(settingsView);
        }
        return super.onOptionsItemSelected(item);
    }

    public void showGpsAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.gps_alert_title);
        alertDialog.setMessage(R.string.gps_alert_message);

        alertDialog.setNegativeButton(R.string.gps_alert_neg, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.setPositiveButton(R.string.gps_alert_pos, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        alertDialog.show();
    }

    public void showInvalidLocationAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.no_location_alert_title);
        alertDialog.setMessage(R.string.no_location_alert_message);

        alertDialog.setNegativeButton(R.string.no_location_alert_btn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }

    public void takePicture(View v) {
        if (!gpsEnabled) {
            showGpsAlertDialog();
            return;
        }
        else if (location == null) {
            showInvalidLocationAlertDialog();
            return;
        }

        takingPicture = true;

        imageFileName = FileUtils.generateImageName();

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(FileUtils.generateImageFile(imageFileName)));

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, ActivityRequestCode.PICTURE_REQUEST.getValue());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("HomeActivity: onActivityResult");

        if (requestCode == ActivityRequestCode.PICTURE_REQUEST.getValue() && resultCode == RESULT_OK) {

            // Stop GPS Service
            stopService(new Intent(this, GpsService.class));
            // Unregister GPS events receiver
            unregisterReceiver(gpsEventsReceiver);

            goToIndividualRecognitionActivity(imageFileName);
        }
    }

    private void goToIndividualRecognitionActivity(String pictureFileName) {
        System.out.println("HomeActivity: goToPictureDetailActivity");

        Intent individualRecognitionIntent = new Intent(this, IndividualRecognitionActivity.class);
        individualRecognitionIntent.putExtra("location", location);
        individualRecognitionIntent.putExtra("fileName", pictureFileName);
        individualRecognitionIntent.putExtra("dateTime", new GregorianCalendar().getTimeInMillis());
        individualRecognitionIntent.putExtra("lat", currentLatitude.toString());
        individualRecognitionIntent.putExtra("lon", currentLongitude.toString());

        startActivity(individualRecognitionIntent);
    }

    public void viewMyPictures(View v) {
        Intent myPicturesIntent = new Intent(this, MyPicturesActivity.class);
        myPicturesIntent.putExtra("location", location);
        startActivity(myPicturesIntent);
    }

    public void viewSettings(View v) {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        settingsIntent.putExtra("location", location);
        startActivity(settingsIntent);
    }

    private void gpsEnabled() {
        System.out.println("HomeActivity: gpsEnabled");
        gpsEnabled = true;

        findViewById(R.id.gps_progress_bar).setVisibility(View.VISIBLE);
        findViewById(R.id.gps_position_available).setVisibility(View.GONE);
        findViewById(R.id.gps_not_enabled_text).setVisibility(View.GONE);
        findViewById(R.id.take_picture).setEnabled(false);
    }

    private void gpsDisabled() {
        System.out.println("HomeActivity: gpsDisabled");
        gpsEnabled = false;
        currentLatitude = null;
        currentLongitude = null;

        findViewById(R.id.gps_progress_bar).setVisibility(View.GONE);
        findViewById(R.id.gps_position_available).setVisibility(View.GONE);
        findViewById(R.id.gps_not_enabled_text).setVisibility(View.VISIBLE);
        findViewById(R.id.take_picture).setEnabled(true);
    }

    private void locationChanged(double lat, double lon) {
        System.out.println("HomeActivity: locationChanged");
        currentLatitude = lat;
        currentLongitude = lon;

        System.out.println("locationDetected: " + locationDetected);
        if(!locationDetected) {
            localDb = new LocalDatabase();
            localDb.getCurrentLocation(new LatLng(lat, lon), this);
        }
        else {
            findViewById(R.id.gps_progress_bar).setVisibility(View.GONE);
            findViewById(R.id.gps_position_available).setVisibility(View.VISIBLE);
            findViewById(R.id.gps_not_enabled_text).setVisibility(View.GONE);
            findViewById(R.id.take_picture).setEnabled(true);
        }
    }

    public void currentLocationDetected(Location currentLocation) {
        locationDetected = true;
        location = currentLocation;

        findViewById(R.id.gps_progress_bar).setVisibility(View.GONE);
        findViewById(R.id.gps_position_available).setVisibility(View.VISIBLE);

        TextView detectedLocationText = (TextView) findViewById(R.id.detected_location_text);
        if (location != null) {
            detectedLocationText.setText("Location: " + currentLocation.getName());
            detectedLocationText.setVisibility(View.VISIBLE);
        }
        else {
            detectedLocationText.setVisibility(View.GONE);
        }
        findViewById(R.id.gps_not_enabled_text).setVisibility(View.GONE);
        findViewById(R.id.take_picture).setEnabled(true);
    }
}
