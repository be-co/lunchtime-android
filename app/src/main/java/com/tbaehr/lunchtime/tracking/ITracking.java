package com.tbaehr.lunchtime.tracking;

/**
 * Created by timo.baehr@gmail.com on 07.03.2017.
 */
public interface ITracking {

    enum Screen {
        DASHBOARD,
        HELP,
        DETAIL;
    }

    /***
     * Tracking screen view
     *
     * @param screen screen name to be displayed on dashboard
     */
    void trackScreenView(Screen screen, String content);

    /***
     * Tracking an exception
     *
     * @param e exception to be tracked
     */
    void trackException(Exception e);

    /***
     * Tracking an event
     *
     * @param category event category
     * @param action   action of the event
     * @param label    label
     */
    void trackEvent(String category, String action, String label);

}
