package edu.uic.ibeis_tourist;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

public class PictureTabsActivity extends ActionBarActivity implements ActionBar.TabListener {


    private Fragment fragment;
    // Declaring our tabs and the corresponding fragments.
    ActionBar.Tab picTab, mapTab;
    Fragment picFragTab = new MyPicturesFragment();
    Fragment mapFragTab = new MyPicturesMapFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);


        // Set the Action Bar to use tabs for navigation
        ActionBar ab = getSupportActionBar();
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        picTab = ab.newTab();
        //mapTab = ab.newTab();
        picTab.setTabListener(new TabListener(picFragTab));
        //mapTab.setTabListener(new TabListener(mapFragTab));

        // Add three tabs to the Action Bar for display
        ab.addTab(picTab);
       // ab.addTab(mapTab);

        ab.addTab(ab.newTab().setText("Map View").setTabListener(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu from menu resource (res/menu/main)
        getMenuInflater().inflate(R.menu.menu_settings, menu);

        return super.onCreateOptionsMenu(menu);
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

}
