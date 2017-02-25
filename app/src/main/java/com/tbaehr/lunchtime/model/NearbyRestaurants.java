package com.tbaehr.lunchtime.model;

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

    public NearbyRestaurants(Map<String, Pair<String, String>> map) {
        lastUpdatedMap = map;
    }

    public Collection<String> getRestaurantKeys() {
        return lastUpdatedMap.keySet();
    }

    public DateTime restaurantLastUpdated(String restrauntId) {
        String dStr = lastUpdatedMap.get(restrauntId).first;
        return DateUtils.createDateFromString(dStr);
    }

    public DateTime offersLastUpdated(String restrauntId) {
        String dStr = lastUpdatedMap.get(restrauntId).second;
        return DateUtils.createDateFromString(dStr);
    }

}
