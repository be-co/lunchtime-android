package com.tbaehr.lunchtime.tracking;

/**
 * Created by timo.baehr@gmail.com on 10.03.2017.
 */
public enum TrackingScreen {

    DASHBOARD,
    HELP,
    DETAIL;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

}
