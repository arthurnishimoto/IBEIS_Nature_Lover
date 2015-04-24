package edu.uic.ibeis_tourist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;

public class MyPicturesMapFragment extends android.support.v4.app.Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_my_pictures_map, container, false);

        Context c = getActivity().getApplicationContext();

        Intent i = new Intent(c, MyPicturesMapActivity.class);
        startActivity(i);

        return rootView;
    }

}
