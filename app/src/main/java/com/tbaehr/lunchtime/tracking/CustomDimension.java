package com.tbaehr.lunchtime.tracking;

/**
 * Created by timo.baehr@gmail.com on 11.03.2017.
 */
public class CustomDimension {

    public enum CustomDimensionIndex {
        KEY_RESTAURANT_ID(2),
        KEY_LOCATION(3);

        final int index;

        CustomDimensionIndex(int index) {
            this.index = index;
        }
    }

    public final int key;

    public final String value;

    public CustomDimension(CustomDimensionIndex key, String value) {
        this.key = key.index;
        this.value = value;
    }

}
