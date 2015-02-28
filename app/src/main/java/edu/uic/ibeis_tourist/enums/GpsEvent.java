package edu.uic.ibeis_tourist.enums;

/**
 * GpsService events
 */
public enum GpsEvent {
    GPS_ENABLED(1001), GPS_DISABLED(1002), LOCATION_CHANGED(1003);

    private final int value;

    private GpsEvent(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}