package edu.uic.ibeis_tourist.utils;

import com.google.android.gms.maps.model.LatLng;

import edu.uic.ibeis_tourist.model.Location;

public class LocationUtils {

    public static boolean isPositionAtLocation(LatLng position, Location location) {
        return location.getBounds().contains(position);
    }
}
