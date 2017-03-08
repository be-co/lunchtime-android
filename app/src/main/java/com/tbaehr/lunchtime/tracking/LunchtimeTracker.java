package com.tbaehr.lunchtime.tracking;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.tbaehr.lunchtime.R;

/**
 * Created by baehrt on 07.03.2017.
 */
public class LunchtimeTracker implements ITracking {

    private Context context;

    private Tracker tracker;

    public LunchtimeTracker(Context context) {
        this.context = context;
    }

    private synchronized Tracker getGoogleAnalyticsTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            tracker = analytics.newTracker(R.xml.global_tracker);
            tracker.enableExceptionReporting(true);

            // remove the last octet of the IP address prior
            tracker.setAnonymizeIp(true);
        }
        return tracker;
    }

    @Override
    public void trackScreenView(Screen screen) {
        Tracker t = getGoogleAnalyticsTracker();
        t.setScreenName(screen.name().toLowerCase());

        // TODO: make custom dimensions visible on GAnalytics Dashboard
        //t.set("&cd1", dimensionValue);
        t.send(new HitBuilders.ScreenViewBuilder()
                //.set("&cd1", dimensionValue + 1)
                //.setCustomDimension(1, dimensionValue + 2)
                .build());

        GoogleAnalytics.getInstance(context).dispatchLocalHits();
    }

    @Override
    public void trackException(Exception e) {
        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();
            t.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(
                            new StandardExceptionParser(context, null)
                                    .getDescription(Thread.currentThread().getName(), e))
                    .setFatal(false)
                    .build()
            );

            GoogleAnalytics.getInstance(context).dispatchLocalHits();
        }
    }

    @Override
    public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());

        GoogleAnalytics.getInstance(context).dispatchLocalHits();
    }
}
