package edu.uic.ibeis_tourist.ibeis;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import java.util.GregorianCalendar;

import edu.uic.ibeis_tourist.exceptions.MatchNotFoundException;
import edu.uic.ibeis_tourist.interfaces.IbeisInterface;
import edu.uic.ibeis_tourist.local_database.LocalDatabase;
import edu.uic.ibeis_tourist.model.Location;
import edu.uic.ibeis_tourist.model.PictureInfo;

public class IbeisInterfaceImplementation implements IbeisInterface {

    @Override
    public void identifyIndividual(String fileName, Double latitude, Double longitude,
                                   GregorianCalendar dateTime, Location location,
                                   Context context) throws MatchNotFoundException {

        System.out.println("IbeisInterfaceImplementation: identify individual");
        new IdentifyIndividualAsyncTask(fileName, latitude, longitude, dateTime, location, context).execute();

    }

    // AsyncTask classes implementation

    // TODO: this is a dummy implementation
    private class IdentifyIndividualAsyncTask extends AsyncTask<Void, Void, PictureInfo> {

        private String mFileName;
        private Double mLatitude;
        private Double mLongitude;
        private GregorianCalendar mDateTime;
        private Location mLocation;
        private Context mContext;

        private IdentifyIndividualAsyncTask(String fileName, Double latitude, Double longitude,
                                    GregorianCalendar dateTime, Location location, Context context) {
            mFileName = fileName;
            mLatitude = latitude;
            mLongitude = longitude;
            mDateTime = dateTime;
            mLocation = location;
            mContext = context;
        }

        @Override
        protected PictureInfo doInBackground(Void... params) {
            System.out.println("IbeisInterfaceImplementation: IdentifyIndividual AsyncTask");

            PictureInfo pictureInfo = new PictureInfo();

            pictureInfo.setFileName(mFileName);
            pictureInfo.setPosition(new LatLng(mLatitude, mLongitude));
            pictureInfo.setDateTime(mDateTime);
            pictureInfo.setIndividualName("Alessandro");
            pictureInfo.setIndividualSpecies("Computer Scientist");
            pictureInfo.setLocation(mLocation);

            return pictureInfo;
        }

        @Override
        protected void onPostExecute(PictureInfo pictureInfo) {
            // TODO Implement
            super.onPostExecute(pictureInfo);

            LocalDatabase localDb = new LocalDatabase();
            localDb.addPicture(pictureInfo, mContext);
        }
    }
}
