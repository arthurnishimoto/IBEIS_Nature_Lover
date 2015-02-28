package edu.uic.ibeis_tourist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import edu.uic.ibeis_tourist.interfaces.LocalDatabaseInterface;
import edu.uic.ibeis_tourist.local_database.LocalDatabase;
import edu.uic.ibeis_tourist.model.Location;


public class SelectLocationActivity extends ActionBarActivity {

    private LocalDatabaseInterface localDb;

    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        localDb = new LocalDatabase();
        localDb.getAllLocations(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_location, menu);
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

    public void displayLocationList(final List<Location> locationList) {
        List<String> locationNames = new ArrayList<>();
        for (Location location : locationList) {
            locationNames.add(location.getName());
        }

        Spinner locationSpinner = (Spinner) findViewById(R.id.select_location_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locationNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter);

        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                location = locationList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO exception: a location should always be selected
            }
        });

        findViewById(R.id.select_location_progress_bar).setVisibility(View.GONE);
        locationSpinner.setVisibility(View.VISIBLE);
        findViewById(R.id.select_location_button).setVisibility(View.VISIBLE);
    }

    public void goToHomeActivity(View v) {
        Intent homeActivityIntent = new Intent(this, HomeActivity.class);
        homeActivityIntent.putExtra("location", location);
        startActivity(homeActivityIntent);
    }
}
