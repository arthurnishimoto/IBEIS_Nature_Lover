package edu.uic.ibeis_tourist.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import edu.uic.ibeis_tourist.enums.GpsEvent;

public class GpsService extends Service {

    // Location provider
    private static final String PROVIDER = LocationManager./*GPS_PROVIDER;*/NETWORK_PROVIDER;
    // Minimum time interval between GPS updates in milliseconds
    private static final int MIN_TIME = 0;
    // Minimum distance interval between GPS updates in meters
    private static final float MIN_DIST = 0.1f;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private double latitude;
    private double longitude;

    // Override Service methods

    @Override
    public void onCreate() {
        System.out.println("GPS service created");
        super.onCreate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new GPSLocationListener();

        if (locationManager.isProviderEnabled(PROVIDER)) {
            broadcastEvent(GpsEvent.GPS_ENABLED);
        }
        else {
            broadcastEvent(GpsEvent.GPS_DISABLED);
        }
        locationManager.requestLocationUpdates(PROVIDER, MIN_TIME, MIN_DIST, locationListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("GPS service started");
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Binding not supported
        throw new UnsupportedOperationException("Binding not supported");
    }

    @Override
    public void onDestroy() {
        System.out.println("GPS service destroyed");
                locationManager.removeUpdates(locationListener);
        super.onDestroy();
    }

    /**
     * Send broadcast message of an asynchronous event occurrence
     * If the event is a location change, send new latitude and longitude
     * @param e value from GpsEvent enum
     */
    private void broadcastEvent(GpsEvent e) {
        Intent intent = new Intent();
        intent.setAction("edu.uic.ibeis_tourist.broadcast_gps_event");
        intent.putExtra("gpsEvent", e.getValue());
        if (e == GpsEvent.LOCATION_CHANGED) {
            intent.putExtra("lat", latitude);
            intent.putExtra("lon", longitude);
        }
        sendBroadcast(intent);
    }

    public class GPSLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            broadcastEvent(GpsEvent.LOCATION_CHANGED);

            System.out.println("New Location: (" + latitude + ", " + longitude + ")");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO implement method
        }

        @Override
        public void onProviderEnabled(String provider) {
            System.out.println("listener: GPS enabled");
            broadcastEvent(GpsEvent.GPS_ENABLED);
        }

        @Override
        public void onProviderDisabled(String provider) {
            System.out.println("listener: GPS disabled");
            broadcastEvent(GpsEvent.GPS_DISABLED);
        }
    }
}
