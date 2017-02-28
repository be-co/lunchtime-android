package com.tbaehr.lunchtime.model;

import android.support.annotation.NonNull;
import android.util.Pair;

import com.tbaehr.lunchtime.utils.DateTime;
import com.tbaehr.lunchtime.utils.DateUtils;

import java.util.Collection;
import java.util.Map;

/**
 * Created by timo.baehr@gmail.com on 24.02.17.
 */
public class NearbyRestaurants {

    private Map<String, Pair<String, String>> lastUpdatedMap;

    public NearbyRestaurants(@NonNull Map<String, Pair<String, String>> map) {
        lastUpdatedMap = map;
    }

    public Collection<String> getRestaurantKeys() {
        return lastUpdatedMap.keySet();
    }

    public DateTime restaurantLastUpdated(@NonNull String restaurantId) {
        String dStr = lastUpdatedMap.get(restaurantId).first;
        return DateUtils.createDateFromString(dStr);
    }

    public DateTime offersLastUpdated(@NonNull String restaurantId) {
        String dStr = lastUpdatedMap.get(restaurantId).second;
        return DateUtils.createDateFromString(dStr);
    }

}
