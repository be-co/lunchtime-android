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

    private boolean optOut;

    private Tracker tracker;

    /**
     * @param context application context
     * @param optOut if true not hits will be sent. This should be set in application initialization code.
     */
    public LunchtimeTracker(Context context, boolean optOut) {
        this.context = context;
        this.optOut = optOut;
    }

    private synchronized Tracker getGoogleAnalyticsTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
            analytics.setAppOptOut(optOut);

            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            tracker = analytics.newTracker(R.xml.global_tracker);
            tracker.enableAdvertisingIdCollection(false);
        }
        return tracker;
    }

    @Override
    public void trackScreenView(TrackingScreen screen) {
        Tracker t = getGoogleAnalyticsTracker();
        t.setScreenName(screen.getScreenName());

        HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder();
        CustomDimension[] customDimensions = screen.getCustomDimensions();
        if (customDimensions != null) {
            for (CustomDimension dimen : customDimensions) {
                builder.setCustomDimension(dimen.key, dimen.value);
            }
        }
        t.send(builder.build());

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
