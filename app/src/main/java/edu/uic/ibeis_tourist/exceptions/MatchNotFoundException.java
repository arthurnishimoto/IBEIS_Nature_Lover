package edu.uic.ibeis_tourist.exceptions;

public class MatchNotFoundException extends Exception {

    public MatchNotFoundException(String detailMessage) {
        super(detailMessage);
    }

    public MatchNotFoundException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public MatchNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
