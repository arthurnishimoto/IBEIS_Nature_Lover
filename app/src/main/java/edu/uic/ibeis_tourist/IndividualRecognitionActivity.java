package edu.uic.ibeis_tourist;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.GregorianCalendar;

import edu.uic.ibeis_tourist.enums.ActivityRequestCode;
import edu.uic.ibeis_tourist.enums.GpsEvent;
import edu.uic.ibeis_tourist.exceptions.MatchNotFoundException;
import edu.uic.ibeis_tourist.ibeis.IbeisInterfaceImplementation;
import edu.uic.ibeis_tourist.interfaces.IbeisInterface;
import edu.uic.ibeis_tourist.interfaces.LocalDatabaseInterface;
import edu.uic.ibeis_tourist.local_database.LocalDatabase;
import edu.uic.ibeis_tourist.model.Location;
import edu.uic.ibeis_tourist.model.PictureInfo;
import edu.uic.ibeis_tourist.services.GpsService;
import edu.uic.ibeis_tourist.utils.FileUtils;


public class IndividualRecognitionActivity extends ActionBarActivity {
    private static final String LOCATION = "location";
    private static final String IMG_FILE_NAME = "imgFileName";
    private static final String DATE_TIME = "dateTime";
    private static final String CUR_LAT = "latitude";
    private static final String CUR_LON = "longitude";

    public static final int IMG_REQUESTED_HEIGHT = 350;
    public static final int IMG_REQUESTED_WIDTH = 500;

    private Location location;
    private String fileName;
    private GregorianCalendar dateTime;
    private Double currentLatitude;
    private Double currentLongitude;

    private IbeisInterface ibeis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO Handle screen orientation change
        System.out.println("IndividualRecognitionActivity: onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_recognition);

        dateTime = new GregorianCalendar();

        if (savedInstanceState != null) {
            System.out.println("Individual Recognition Activity: reading savedInstanceState");

            location = savedInstanceState.getParcelable(LOCATION);
            System.out.println("Location: " + location.getId());
            fileName = savedInstanceState.getString(IMG_FILE_NAME);
            dateTime.setTimeInMillis(savedInstanceState.getLong(DATE_TIME));

            String lat = savedInstanceState.getString(CUR_LAT);
            String lon = savedInstanceState.getString(CUR_LON);

            if(lat != null && lon != null) {
                currentLatitude = Double.parseDouble(lat);
                currentLongitude = Double.parseDouble(lon);
            }
            else {
                // TODO Throw Exception
            }
        }
        else {
            Intent intent = getIntent();
            location = intent.getParcelableExtra("location");
            fileName = intent.getStringExtra("fileName");
            dateTime.setTimeInMillis(intent.getLongExtra("dateTime", 0));

            String lat = intent.getStringExtra("lat");
            String lon = intent.getStringExtra("lon");

            if(lat != null && lon != null) {
                currentLatitude = Double.parseDouble(lat);
                currentLongitude = Double.parseDouble(lon);
            }
            else {
                // TODO Throw Exception
            }
        }

        try {
            ibeis = new IbeisInterfaceImplementation();
            ibeis.identifyIndividual(fileName, currentLatitude, currentLongitude, dateTime, location, this);

        } catch (MatchNotFoundException e) {
            e.printStackTrace();
            handleMatchNotFound();
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
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(LOCATION, location);
        outState.putString(IMG_FILE_NAME, fileName);
        outState.putLong(DATE_TIME, dateTime.getTimeInMillis());
        outState.putString(CUR_LAT, currentLatitude!=null ? currentLatitude.toString() : null);
        outState.putString(CUR_LON, currentLongitude!=null ? currentLongitude.toString() : null);

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_individual_recognition, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    private void handleMatchNotFound() {
        // TODO
    }

    public void displayPictureInfo(PictureInfo pictureInfo) {

        try {

            ProgressBar progressBar = (ProgressBar) findViewById(R.id.individual_recognition_progress_bar);
            ImageView imageView = (ImageView) findViewById(R.id.individual_recognition_image_view);
            TextView nameText = (TextView) findViewById(R.id.individual_recognition_name);
            TextView speciesText = (TextView) findViewById(R.id.individual_recognition_species);

            nameText.setText("Name: " + pictureInfo.getIndividualName());
            speciesText.setText("Species: " + pictureInfo.getIndividualSpecies());
            imageView.setImageBitmap(
                    FileUtils.getImageBitmap(
                            pictureInfo.getFileName(),
                            IndividualRecognitionActivity.IMG_REQUESTED_HEIGHT,
                            IndividualRecognitionActivity.IMG_REQUESTED_WIDTH,
                            Bitmap.Config.RGB_565
                    )
            );

            progressBar.setVisibility(View.GONE);
            nameText.setVisibility(View.VISIBLE);
            speciesText.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);

        } catch (IOException e) {
            // TODO handle Exception
            e.printStackTrace();
        }
    }

    public void viewMyPictures(View v) {
        Intent myPicturesIntent = new Intent(this, MyPicturesActivity.class);
        myPicturesIntent.putExtra("location", location);
        startActivity(myPicturesIntent);
    }

    // Duplicated camera code from HomeActivity
    private boolean gpsEnabled;

    private boolean takingPicture;
    private boolean locationDetected;

    private String imageFileName;

    private LocalDatabaseInterface localDb;

    private void gpsEnabled() {
        System.out.println("IndividualRecognitionActivity: gpsEnabled");
        gpsEnabled = true;
    }

    private void gpsDisabled() {
        System.out.println("IndividualRecognitionActivity: gpsDisabled");
        gpsEnabled = false;
        currentLatitude = null;
        currentLongitude = null;
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
    }

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

        alertDialog.show();
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
}
