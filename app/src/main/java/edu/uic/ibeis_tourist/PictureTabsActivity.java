package edu.uic.ibeis_tourist;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class PictureTabsActivity extends ActionBarActivity implements ActionBar.TabListener {


    private Fragment fragment;
    // Declaring our tabs and the corresponding fragments.
    ActionBar.Tab picTab, mapTab;
    Fragment picFragTab = new MyPicturesFragment();
    Fragment mapFragTab = new edu.uic.ibeis_tourist.MyPicturesMapFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);


        // Set the Action Bar to use tabs for navigation
        ActionBar ab = getSupportActionBar();
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        picTab = ab.newTab().setText("List View");
        mapTab = ab.newTab().setText("Map View");
        picTab.setTabListener(new edu.uic.ibeis_tourist.TabListener(picFragTab));
        mapTab.setTabListener(new edu.uic.ibeis_tourist.TabListener(mapFragTab));

        // Add three tabs to the Action Bar for display
        ab.addTab(picTab);
        ab.addTab(mapTab);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu from menu resource (res/menu/main)
        getMenuInflater().inflate(R.menu.menu_my_pictures, menu);
        return true;
    }

    // Implemented from ActionBar.TabListener
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // This is called when a tab is selected.
    }

    // Implemented from ActionBar.TabListener
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // This is called when a previously selected tab is unselected.
    }

    // Implemented from ActionBar.TabListener
    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // This is called when a previously selected tab is selected again.
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            //case R.id.action_settings:
            //    Intent settingsView = new Intent(this, SettingsActivity.class);
            //    startActivity(settingsView);

        }
        return super.onOptionsItemSelected(item);
    }
}
