package edu.uic.ibeis_tourist.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.GregorianCalendar;

public class PictureInfo implements Parcelable {
    // TODO add attributes
    private String fileName;
    private GregorianCalendar dateTime;
    private LatLng position;
    private String individualName;
    private String individualSpecies;
    private Location location;

    public PictureInfo() {}

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public GregorianCalendar getDateTime() {
        return dateTime;
    }

    public void setDateTime(GregorianCalendar dateTime) {
        this.dateTime = dateTime;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getIndividualName() {
        return individualName;
    }

    public void setIndividualName(String individualName) {
        this.individualName = individualName;
    }

    public String getIndividualSpecies() {
        return individualSpecies;
    }

    public void setIndividualSpecies(String individualSpecies) {
        this.individualSpecies = individualSpecies;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    // Implementation of Parcelable interface

    public PictureInfo(Parcel in){
        fileName = in.readString();
        dateTime = new GregorianCalendar();
        dateTime.setTimeInMillis(in.readLong());
        position = new LatLng(in.readDouble(), in.readDouble());
        individualName = in.readString();
        individualSpecies = in.readString();
        location = in.readParcelable(Location.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileName);
        dest.writeLong(dateTime.getTimeInMillis());
        dest.writeDouble(position.latitude);
        dest.writeDouble(position.longitude);
        dest.writeString(individualName);
        dest.writeString(individualSpecies);
        dest.writeParcelable(location, 0);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public PictureInfo createFromParcel(Parcel in) {
            return new PictureInfo(in);
        }

        public PictureInfo[] newArray(int size) {
            return new PictureInfo[size];
        }
    };
}