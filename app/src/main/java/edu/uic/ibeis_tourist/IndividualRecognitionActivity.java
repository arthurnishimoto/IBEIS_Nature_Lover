package edu.uic.ibeis_tourist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.GregorianCalendar;

import edu.uic.ibeis_tourist.exceptions.MatchNotFoundException;
import edu.uic.ibeis_tourist.ibeis.IbeisInterfaceImplementation;
import edu.uic.ibeis_tourist.interfaces.IbeisInterface;
import edu.uic.ibeis_tourist.model.Location;
import edu.uic.ibeis_tourist.model.PictureInfo;
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

            if(lat != null) {
                currentLatitude = Double.parseDouble(lat);
            }
            if(lon != null) {
                currentLongitude = Double.parseDouble(lon);
            }
        }
        else {
            Intent intent = getIntent();
            location = intent.getParcelableExtra("location");
            fileName = intent.getStringExtra("fileName");
            dateTime.setTimeInMillis(intent.getLongExtra("dateTime", 0));

            if (intent.getBooleanExtra("positionAvailable", false)) {
                currentLatitude = intent.getDoubleExtra("lat", 0);
                currentLongitude = intent.getDoubleExtra("lon", 0);
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
        if (id == R.id.action_settings) {
            return true;
        }

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
}
