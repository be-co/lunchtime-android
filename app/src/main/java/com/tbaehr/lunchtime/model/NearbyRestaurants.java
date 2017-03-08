package com.tbaehr.lunchtime.model;

import android.support.annotation.NonNull;
import android.util.Pair;

import com.tbaehr.lunchtime.utils.DateTime;
import com.tbaehr.lunchtime.utils.DateUtils;

import java.text.ParseException;
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

    /**
     * @param restaurantId unique identifier of a restaurant
     * @return null if restaurant is not cached
     * @throws ParseException if the cached date cannot be parsed
     */
    public DateTime restaurantLastUpdated(@NonNull String restaurantId) {
        Pair<String, String> lastUpdated = lastUpdatedMap.get(restaurantId);
        if (lastUpdated == null) {
            throw new IllegalStateException("There should always be a non-null update pair for restaurants. (restaurant key = " + restaurantId + ")");
        }
        String restaurantLastUpdated = lastUpdated.first;
        try {
            return restaurantLastUpdated != null ? DateUtils.createDateFromString(restaurantLastUpdated) : null;
        } catch (ParseException e) {
            throw new IllegalStateException("The cached date should always be parsable. (restaurant key = " + restaurantId + ")");
        }
    }

    public DateTime offersLastUpdated(@NonNull String restaurantId) {
        String dStr = lastUpdatedMap.get(restaurantId).second;
        try {
            return DateUtils.createDateFromString(dStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
