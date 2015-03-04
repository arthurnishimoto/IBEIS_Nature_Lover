package edu.uic.ibeis_tourist.local_database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

import edu.uic.ibeis_tourist.HomeActivity;
import edu.uic.ibeis_tourist.IndividualRecognitionActivity;
import edu.uic.ibeis_tourist.MyPicturesActivity;
import edu.uic.ibeis_tourist.MyPicturesMapActivity;
import edu.uic.ibeis_tourist.interfaces.LocalDatabaseInterface;
import edu.uic.ibeis_tourist.model.Location;
import edu.uic.ibeis_tourist.model.PictureInfo;
import edu.uic.ibeis_tourist.utils.DateTimeUtils;
import edu.uic.ibeis_tourist.utils.LocationUtils;

public class LocalDatabase implements LocalDatabaseInterface {

    private LocalDatabaseOpenHelper dbHelper;

    @Override
    public void addPicture(PictureInfo pictureInfo, Context context) {
        new AddPictureAsyncTask(pictureInfo, context).execute();
    }

    @Override
    public void getPicture(String fileName, Context context) {
        new GetPictureAsyncTask(fileName, context).execute();
    }

    @Override
    public void removePicture(String fileName, Context context) {
        new RemovePictureAsyncTask(fileName, context).execute();
    }

    @Override
    public void getAllPictures(Context context) {
        new GetAllPicturesAsyncTask(context).execute();
    }

    @Override
    public void getAllPicturesAtLocation(int locationId, Context context) {
        new GetAllPicturesAtLocationAsyncTask(locationId, context).execute();
    }

    @Override
    public void getAllLocations(Context context) {
        new GetAllLocationsAsyncTask(context).execute();
    }

    @Override
    public void getCurrentLocation(LatLng currentPosition, Context context) {
        new GetCurrentLocationAsyncTask(currentPosition, context).execute();
    }

    /**
     * Get Location object from location ID
     * @param locationId
     * @return
     */
    public Location getLocation(int locationId, final Context context) {
        dbHelper = new LocalDatabaseOpenHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String projection[] = {
                LocalDatabaseContract.LocationEntry.COLUMN_NAME_LOCATION_NAME,
                LocalDatabaseContract.LocationEntry.COLUMN_NAME_SW_BOUND_LAT,
                LocalDatabaseContract.LocationEntry.COLUMN_NAME_SW_BOUND_LON,
                LocalDatabaseContract.LocationEntry.COLUMN_NAME_NE_BOUND_LAT,
                LocalDatabaseContract.LocationEntry.COLUMN_NAME_NE_BOUND_LON
        };

        String selection = LocalDatabaseContract.LocationEntry.COLUMN_NAME_ID + " = ?";

        String[] selectionArgs = {String.valueOf(locationId)};

        Cursor cursor = db.query(LocalDatabaseContract.LocationEntry.TABLE_NAME, projection,
                selection, selectionArgs, null, null, null);

        cursor.moveToFirst();
        String locationName = cursor.getString(cursor.getColumnIndex
                (LocalDatabaseContract.LocationEntry.COLUMN_NAME_LOCATION_NAME));
        double swBoundLat = cursor.getDouble(cursor.getColumnIndex
                (LocalDatabaseContract.LocationEntry.COLUMN_NAME_SW_BOUND_LAT));
        double swBoundLon = cursor.getDouble(cursor.getColumnIndex
                (LocalDatabaseContract.LocationEntry.COLUMN_NAME_SW_BOUND_LON));
        double neBoundLat = cursor.getDouble(cursor.getColumnIndex
                (LocalDatabaseContract.LocationEntry.COLUMN_NAME_NE_BOUND_LAT));
        double neBoundLon = cursor.getDouble(cursor.getColumnIndex
                (LocalDatabaseContract.LocationEntry.COLUMN_NAME_NE_BOUND_LON));

        Location location = new Location();
        location.setId(locationId);
        location.setName(locationName);
        location.setBounds(new LatLngBounds(new LatLng(swBoundLat, swBoundLon), new LatLng(neBoundLat, neBoundLon)));

        return location;
    }


    // AsyncTask classes implementation

    // Add Picture
    private class AddPictureAsyncTask extends AsyncTask<Void, Void, Void> {

        private Context mContext;
        private PictureInfo mPictureInfo;

        private AddPictureAsyncTask(PictureInfo pictureInfo, Context context) {
            mContext = context;
            mPictureInfo = pictureInfo;
        }

        @Override
        protected Void doInBackground(Void... params) {
            System.out.println("LocalDatabase: AddPicture AsyncTask");

            dbHelper = new LocalDatabaseOpenHelper(mContext);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_FILENAME, mPictureInfo.getFileName());
            values.put(LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_LATITUDE, mPictureInfo.getPosition().latitude);
            values.put(LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_LONGITUDE, mPictureInfo.getPosition().longitude);
            values.put(LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_DATETIME,
                       DateTimeUtils.convertToDatetimeString(mPictureInfo.getDateTime()));
            values.put(LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_INDIVIDUAL_NAME,
                    mPictureInfo.getIndividualName());
            values.put(LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_INDIVIDUAL_SPECIES,
                    mPictureInfo.getIndividualSpecies());
            values.put(LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_LOCATION_ID,
                    mPictureInfo.getLocation().getId());

            db.insert(LocalDatabaseContract.PictureInfoEntry.TABLE_NAME,
                      LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_FILENAME, values);

            return null;
        }

        @Override
        protected void onPostExecute(Void voidParam) {
            if (mContext instanceof Activity) {
                displayPictureInfo((Activity) mContext);
            }
        }

        private void displayPictureInfo (Activity activity) {

            String activityName = activity.getClass().getSimpleName();

            switch (activityName) {
                case "IndividualRecognitionActivity":
                    IndividualRecognitionActivity individualRecognitionActivity =
                            (IndividualRecognitionActivity) activity;
                    individualRecognitionActivity.displayPictureInfo(mPictureInfo);
            }
        }
    }

    // Get Picture
    private class GetPictureAsyncTask extends AsyncTask<Void, Void, PictureInfo> {

        // TODO if picture not found in folder, delete row

        private Context mContext;
        private String mFileName;

        private GetPictureAsyncTask(String fileName, Context context) {
            mContext = context;
            mFileName = fileName;
        }

        @Override
        protected PictureInfo doInBackground(Void... params) {
            // TODO implement
            return null;
        }

        @Override
        protected void onPostExecute(PictureInfo pictureInfo) {
            // TODO implement
        }
    }

    // Remove Picture
    private class RemovePictureAsyncTask extends AsyncTask<Void, Void, Void> {

        // TODO if picture not found in folder, delete row

        private Context mContext;
        private String mFileName;

        private RemovePictureAsyncTask(String fileName, Context context) {
            mContext = context;
            mFileName = fileName;
        }

        @Override
        protected Void doInBackground(Void... params) {
            System.out.println("LocalDatabase: RemovePicture AsyncTask");

            dbHelper = new LocalDatabaseOpenHelper(mContext);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String selection = LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_FILENAME;
            String[] selectionArgs = { mFileName };
            db.delete(LocalDatabaseContract.PictureInfoEntry.TABLE_NAME, selection, selectionArgs);
            return null;
        }

        @Override
        protected void onPostExecute(Void voidParam) {
            // TODO implement
        }
    }


    //Get All Pictures
    private class GetAllPicturesAsyncTask extends AsyncTask<Void, Void, List<PictureInfo>> {

        // TODO if picture not found in folder, delete row

        private Context mContext;

        private GetAllPicturesAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected List<PictureInfo> doInBackground(Void... params) {
            System.out.println("LocalDatabase: GetAllPictures AsyncTask");

            dbHelper = new LocalDatabaseOpenHelper(mContext);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String projection[] = {
                    LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_FILENAME,
                    LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_INDIVIDUAL_NAME,
                    LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_INDIVIDUAL_SPECIES,
                    LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_LATITUDE,
                    LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_LONGITUDE,
                    LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_DATETIME,
                    LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_LOCATION_ID
            };

            String sortOrder = LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_DATETIME + " DESC";

            Cursor cursor = db.query(LocalDatabaseContract.PictureInfoEntry.TABLE_NAME, projection,
                    null, null, null, null, sortOrder);

            List<PictureInfo> pictureInfoList = new ArrayList<>();

            while (cursor.moveToNext())
            {
                PictureInfo pictureInfo = new PictureInfo();

                pictureInfo.setFileName(cursor.getString(cursor.getColumnIndex
                        (LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_FILENAME)));

                pictureInfo.setIndividualName(cursor.getString(cursor.getColumnIndex
                        (LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_INDIVIDUAL_NAME)));

                pictureInfo.setIndividualSpecies(cursor.getString(cursor.getColumnIndex
                        (LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_INDIVIDUAL_SPECIES)));

                try {
                    double lat = Double.parseDouble(cursor.getString(cursor.getColumnIndex
                                    (LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_LATITUDE)));
                    double lon = Double.parseDouble(cursor.getString(cursor.getColumnIndex
                                    (LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_LONGITUDE)));
                    pictureInfo.setPosition(new LatLng(lat, lon));
                } catch (NumberFormatException e) {
                    pictureInfo.setPosition(null);
                }

                pictureInfo.setDateTime(DateTimeUtils.convertToGregorianCalendar(cursor.getString
                        (cursor.getColumnIndex(LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_DATETIME))));

                pictureInfo.setLocation(getLocation(cursor.getInt(cursor.getColumnIndex
                        (LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_LOCATION_ID)), mContext));

                pictureInfoList.add(pictureInfo);
            }

            return pictureInfoList;
        }


        @Override
        protected void onPostExecute(List<PictureInfo> pictureInfoList) {
            if (mContext instanceof Activity) {
                displayPictureInfoList(pictureInfoList, (Activity) mContext);
            }
        }

        private void displayPictureInfoList (final List<PictureInfo> pictureInfoList, final Activity activity) {

            String activityName = activity.getClass().getSimpleName();

            switch (activityName) {
                case "MyPicturesActivity":
                    MyPicturesActivity myPicturesActivity = (MyPicturesActivity) activity;
                    myPicturesActivity.displayPictureInfoList(pictureInfoList);
                    break;
                case "MyPicturesMapActivity":
                    MyPicturesMapActivity myPicturesMapActivity = (MyPicturesMapActivity) activity;
                    myPicturesMapActivity.showPictures(pictureInfoList);
            }
        }
    }

    //Get All Pictures At Location
    private class GetAllPicturesAtLocationAsyncTask extends AsyncTask<Void, Void, List<PictureInfo>> {

        // TODO if picture not found in folder, delete row

        private int mLocationId;
        private Context mContext;

        private GetAllPicturesAtLocationAsyncTask(int locationId, Context context) {
            mLocationId = locationId;
            mContext = context;
        }

        @Override
        protected List<PictureInfo> doInBackground(Void... params) {
            System.out.println("LocalDatabase: GetAllPicturesAtLocation AsyncTask");

            dbHelper = new LocalDatabaseOpenHelper(mContext);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String projection[] = {
                    LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_FILENAME,
                    LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_INDIVIDUAL_NAME,
                    LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_INDIVIDUAL_SPECIES,
                    LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_LATITUDE,
                    LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_LONGITUDE,
                    LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_DATETIME,
                    LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_LOCATION_ID
            };

            String selection = LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_LOCATION_ID + " = ?";

            String[] selectionArgs = { String.valueOf(mLocationId) };

            String sortOrder = LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_DATETIME + " DESC";

            Cursor cursor = db.query(LocalDatabaseContract.PictureInfoEntry.TABLE_NAME, projection,
                    selection, selectionArgs, null, null, sortOrder);

            List<PictureInfo> pictureInfoList = new ArrayList<>();

            while (cursor.moveToNext())
            {
                PictureInfo pictureInfo = new PictureInfo();

                pictureInfo.setFileName(cursor.getString(cursor.getColumnIndex
                        (LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_FILENAME)));

                pictureInfo.setIndividualName(cursor.getString(cursor.getColumnIndex
                        (LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_INDIVIDUAL_NAME)));

                pictureInfo.setIndividualSpecies(cursor.getString(cursor.getColumnIndex
                        (LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_INDIVIDUAL_SPECIES)));

                try {
                    double lat = Double.parseDouble(cursor.getString(cursor.getColumnIndex
                            (LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_LATITUDE)));
                    double lon = Double.parseDouble(cursor.getString(cursor.getColumnIndex
                            (LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_LONGITUDE)));
                    pictureInfo.setPosition(new LatLng(lat, lon));
                } catch (NumberFormatException e) {
                    pictureInfo.setPosition(null);
                }

                pictureInfo.setDateTime(DateTimeUtils.convertToGregorianCalendar(cursor.getString
                        (cursor.getColumnIndex(LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_DATETIME))));

                pictureInfo.setLocation(getLocation(cursor.getInt(cursor.getColumnIndex
                        (LocalDatabaseContract.PictureInfoEntry.COLUMN_NAME_LOCATION_ID)), mContext));

                pictureInfoList.add(pictureInfo);
            }

            return pictureInfoList;
        }


        @Override
        protected void onPostExecute(List<PictureInfo> pictureInfoList) {
            if (mContext instanceof Activity) {
                displayPictureInfoList(pictureInfoList, (Activity) mContext);
            }
        }

        private void displayPictureInfoList (final List<PictureInfo> pictureInfoList, final Activity activity) {

            String activityName = activity.getClass().getSimpleName();

            switch (activityName) {
                case "MyPicturesActivity":
                    MyPicturesActivity myPicturesActivity = (MyPicturesActivity) activity;
                    myPicturesActivity.displayPictureInfoList(pictureInfoList);
            }
        }
    }


    // Get All Locations
    private class GetAllLocationsAsyncTask extends AsyncTask<Void, Void, List<Location>> {

        private Context mContext;

        private GetAllLocationsAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected List<Location> doInBackground(Void... params) {
            System.out.println("LocalDatabase: GetAllLocations AsyncTask");

            dbHelper = new LocalDatabaseOpenHelper(mContext);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String projection[] = {
                    LocalDatabaseContract.LocationEntry.COLUMN_NAME_ID,
                    LocalDatabaseContract.LocationEntry.COLUMN_NAME_LOCATION_NAME,
                    LocalDatabaseContract.LocationEntry.COLUMN_NAME_SW_BOUND_LAT,
                    LocalDatabaseContract.LocationEntry.COLUMN_NAME_SW_BOUND_LON,
                    LocalDatabaseContract.LocationEntry.COLUMN_NAME_NE_BOUND_LAT,
                    LocalDatabaseContract.LocationEntry.COLUMN_NAME_NE_BOUND_LON
            };

            String sortOrder = LocalDatabaseContract.LocationEntry.COLUMN_NAME_LOCATION_NAME + " ASC";

            Cursor cursor = db.query(LocalDatabaseContract.LocationEntry.TABLE_NAME, projection,
                    null, null, null, null, sortOrder);

            List<Location> locationList = new ArrayList<>();

            while (cursor.moveToNext()) {
                Location location = new Location();

                location.setId(cursor.getInt(cursor.getColumnIndex
                        (LocalDatabaseContract.LocationEntry.COLUMN_NAME_ID)));

                location.setName(cursor.getString(cursor.getColumnIndex
                        (LocalDatabaseContract.LocationEntry.COLUMN_NAME_LOCATION_NAME)));

                double swBoundLat = cursor.getDouble(cursor.getColumnIndex
                        (LocalDatabaseContract.LocationEntry.COLUMN_NAME_SW_BOUND_LAT));
                double swBoundLon = cursor.getDouble(cursor.getColumnIndex
                        (LocalDatabaseContract.LocationEntry.COLUMN_NAME_SW_BOUND_LON));
                double neBoundLat = cursor.getDouble(cursor.getColumnIndex
                        (LocalDatabaseContract.LocationEntry.COLUMN_NAME_NE_BOUND_LAT));
                double neBoundLon = cursor.getDouble(cursor.getColumnIndex
                        (LocalDatabaseContract.LocationEntry.COLUMN_NAME_NE_BOUND_LON));

                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                builder.include(new LatLng(swBoundLat, swBoundLon));
                builder.include(new LatLng(neBoundLat, neBoundLon));

                location.setBounds(builder.build());

                locationList.add(location);
            }

            return locationList;
        }

        @Override
        protected void onPostExecute(List<Location> locationList) {
            if (mContext instanceof Activity) {
                displayLocationList(locationList, (Activity) mContext);
            }
        }

        private void displayLocationList (final List<Location> locationList, final Activity activity) {

            String activityName = activity.getClass().getSimpleName();

            switch (activityName) {
            }
        }
    }

    // Get All Locations
    private class GetCurrentLocationAsyncTask extends AsyncTask<Void, Void, Location> {

        private Context mContext;
        private LatLng mPosition;

        private GetCurrentLocationAsyncTask(LatLng position, Context context) {
            mContext = context;
            mPosition = position;
        }

        @Override
        protected Location doInBackground(Void... params) {
            System.out.println("LocalDatabase: GetCurrentLocation AsyncTask");

            dbHelper = new LocalDatabaseOpenHelper(mContext);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String projection[] = {
                    LocalDatabaseContract.LocationEntry.COLUMN_NAME_ID,
                    LocalDatabaseContract.LocationEntry.COLUMN_NAME_LOCATION_NAME,
                    LocalDatabaseContract.LocationEntry.COLUMN_NAME_SW_BOUND_LAT,
                    LocalDatabaseContract.LocationEntry.COLUMN_NAME_SW_BOUND_LON,
                    LocalDatabaseContract.LocationEntry.COLUMN_NAME_NE_BOUND_LAT,
                    LocalDatabaseContract.LocationEntry.COLUMN_NAME_NE_BOUND_LON
            };

            Cursor cursor = db.query(LocalDatabaseContract.LocationEntry.TABLE_NAME, projection,
                    null, null, null, null, null);

            List<Location> locationList = new ArrayList<>();

            while (cursor.moveToNext()) {
                Location location = new Location();

                location.setId(cursor.getInt(cursor.getColumnIndex
                        (LocalDatabaseContract.LocationEntry.COLUMN_NAME_ID)));

                location.setName(cursor.getString(cursor.getColumnIndex
                        (LocalDatabaseContract.LocationEntry.COLUMN_NAME_LOCATION_NAME)));

                double swBoundLat = cursor.getDouble(cursor.getColumnIndex
                        (LocalDatabaseContract.LocationEntry.COLUMN_NAME_SW_BOUND_LAT));
                double swBoundLon = cursor.getDouble(cursor.getColumnIndex
                        (LocalDatabaseContract.LocationEntry.COLUMN_NAME_SW_BOUND_LON));
                double neBoundLat = cursor.getDouble(cursor.getColumnIndex
                        (LocalDatabaseContract.LocationEntry.COLUMN_NAME_NE_BOUND_LAT));
                double neBoundLon = cursor.getDouble(cursor.getColumnIndex
                        (LocalDatabaseContract.LocationEntry.COLUMN_NAME_NE_BOUND_LON));

                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                builder.include(new LatLng(swBoundLat, swBoundLon));
                builder.include(new LatLng(neBoundLat, neBoundLon));

                location.setBounds(builder.build());

                locationList.add(location);
            }

            Location currentLocation = null;

            for (Location location : locationList) {
                if (LocationUtils.isPositionAtLocation(mPosition, location)) {
                    currentLocation = location;
                    break;
                }
            }
            return currentLocation;
        }

        @Override
        protected void onPostExecute(Location currentLocation) {
            if (mContext instanceof Activity) {
                currentLocationDetected(currentLocation, (Activity) mContext);
            }
        }

        private void currentLocationDetected (final Location currentLocation, final Activity activity) {

            String activityName = activity.getClass().getSimpleName();

            switch (activityName) {
                case "HomeActivity":
                    HomeActivity homeActivity = (HomeActivity) activity;
                    homeActivity.currentLocationDetected(currentLocation);
            }
        }
    }
}


