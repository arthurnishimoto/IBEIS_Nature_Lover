package edu.uic.ibeis_tourist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import edu.uic.ibeis_tourist.model.PictureInfo;
import edu.uic.ibeis_tourist.utils.FileUtils;


public class MyPictureDetailActivity extends ActionBarActivity {

    private static final int IMG_REQUESTED_HEIGHT = 350;
    private static final int IMG_REQUESTED_WIDTH = 500;

    private PictureInfo pictureInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_picture_detail);

        Intent intent = getIntent();
        pictureInfo = intent.getParcelableExtra("pictureInfo");

        try {
            ImageView imageView = (ImageView) findViewById(R.id.picture_detail_image_view);
            TextView locationText = (TextView) findViewById(R.id.picture_detail_location);
            TextView nameText = (TextView) findViewById(R.id.picture_detail_name);
            TextView speciesText = (TextView) findViewById(R.id.picture_detail_species);

            locationText.setText("Location: " + pictureInfo.getLocation().getName());
            nameText.setText("Name: " + pictureInfo.getIndividualName());
            speciesText.setText("Species: " + pictureInfo.getIndividualSpecies());
            imageView.setImageBitmap(
                    FileUtils.getImageBitmap(
                            pictureInfo.getFileName(),
                            this.IMG_REQUESTED_HEIGHT,
                            this.IMG_REQUESTED_WIDTH,
                            Bitmap.Config.RGB_565
                    )
            );
        } catch (IOException e) {
            //TODO handle exception
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_picture_detail, menu);
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
            Intent settingsView = new Intent(this, SettingsActivity.class);
            startActivity(settingsView);
        }

        return super.onOptionsItemSelected(item);
    }
}
