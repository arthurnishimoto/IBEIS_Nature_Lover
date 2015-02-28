package edu.uic.ibeis_tourist.enums;

/**
 * Request codes for startActivityForResult invocations
 */
public enum ActivityRequestCode {
    PICTURE_REQUEST(1001);

    private final int value;

    private ActivityRequestCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
