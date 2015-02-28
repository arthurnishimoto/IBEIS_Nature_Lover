package edu.uic.ibeis_tourist.local_database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDatabaseOpenHelper extends SQLiteOpenHelper {

    public LocalDatabaseOpenHelper(Context context) {
        super(context, LocalDatabaseContract.DATABASE_NAME, null, LocalDatabaseContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LocalDatabaseContract.SQL_CREATE_PICTURES_TABLE);
        db.execSQL(LocalDatabaseContract.SQL_CREATE_LOCATIONS_TABLE);
        // Insert values in Location table
        addLocations(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO implement an actual upgrade policy
        // Temporary upgrade policy: discard the data and start over
        db.execSQL(LocalDatabaseContract.SQL_DELETE_PICTURES_TABLE);
        db.execSQL(LocalDatabaseContract.SQL_DELETE_LOCATIONS_TABLE);
        onCreate(db);
    }

    public void addLocations(SQLiteDatabase db) {
        ContentValues values;

        // UIC campus
        values = new ContentValues();
        values.put(LocalDatabaseContract.LocationEntry.COLUMN_NAME_ID, 1);
        values.put(LocalDatabaseContract.LocationEntry.COLUMN_NAME_LOCATION_NAME, "UIC campus");
        values.put(LocalDatabaseContract.LocationEntry.COLUMN_NAME_SW_BOUND_LAT, 41.875668);
        values.put(LocalDatabaseContract.LocationEntry.COLUMN_NAME_SW_BOUND_LON, -87.671639);
        values.put(LocalDatabaseContract.LocationEntry.COLUMN_NAME_NE_BOUND_LAT, 41.879323);
        values.put(LocalDatabaseContract.LocationEntry.COLUMN_NAME_NE_BOUND_LON, -87.681928);

        db.insert(LocalDatabaseContract.LocationEntry.TABLE_NAME,
                  LocalDatabaseContract.LocationEntry.COLUMN_NAME_ID,
                  values);

        // TODO Brookfield Zoo
    }
}
