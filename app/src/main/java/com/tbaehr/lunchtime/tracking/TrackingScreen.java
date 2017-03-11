package com.tbaehr.lunchtime.tracking;

/**
 * Created by timo.baehr@gmail.com on 10.03.2017.
 */
public class TrackingScreen {

    public enum Screen {
        DASHBOARD,
        HELP,
        DETAIL;
    }

    private final Screen screen;

    private CustomDimension[] customDimensions;

    public TrackingScreen(Screen screen, CustomDimension... customDimensions) {
        this.screen = screen;
        this.customDimensions = customDimensions;
    }

    public String getScreenName() {
        return screen.name().toLowerCase();
    }

    public CustomDimension[] getCustomDimensions() {
        return customDimensions;
    }

}
