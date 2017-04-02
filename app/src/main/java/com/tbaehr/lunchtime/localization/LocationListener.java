package com.tbaehr.lunchtime.localization;

import android.location.Location;

/**
 * Created by timo.baehr@gmail.com on 30.03.2017.
 */
public interface LocationListener {

    /**
     * Called when the location has changed.
     *
     * @param location The new location, as a Location object.
     */
    void onLocationChanged(Location location);

}
