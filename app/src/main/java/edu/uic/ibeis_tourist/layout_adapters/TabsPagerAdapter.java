package edu.uic.ibeis_tourist.layout_adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import edu.uic.ibeis_tourist.MyPicturesFragment;
import edu.uic.ibeis_tourist.MyPicturesMapFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // pictures list
                //return new MyPicturesFragment();
                return null;
            case 1:
                // pictures map
                //return new MyPicturesMapFragment();
                return null;
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }

}