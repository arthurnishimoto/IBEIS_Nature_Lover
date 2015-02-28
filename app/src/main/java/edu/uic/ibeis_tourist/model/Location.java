package edu.uic.ibeis_tourist.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class Location implements Parcelable {

    private int id;
    private String name;
    private LatLngBounds bounds;

    public Location() {}

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LatLngBounds getBounds() {
        return bounds;
    }

    public void setBounds(LatLngBounds bounds) {
        this.bounds = bounds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Implementation of Parcelable interface
    public Location(Parcel in){
        id = in.readInt();
        name = in.readString();

        double swBoundLat = in.readDouble();
        double swBoundLon = in.readDouble();
        double neBoundLat = in.readDouble();
        double neBoundLon = in.readDouble();

        bounds = new LatLngBounds(new LatLng(swBoundLat, swBoundLon), new LatLng(neBoundLat, neBoundLon));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeDouble(bounds.southwest.latitude);
        dest.writeDouble(bounds.southwest.longitude);
        dest.writeDouble(bounds.northeast.latitude);
        dest.writeDouble(bounds.northeast.longitude);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

}
