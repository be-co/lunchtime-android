/**
 * The MIT License (MIT) Copyright (c) 2017 Timo Bähr
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or s
 * ubstantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.tbaehr.lunchtime.presenter;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.propaneapps.tomorrow.presenter.BasePresenter;
import com.tbaehr.lunchtime.DataProvider;
import com.tbaehr.lunchtime.LunchtimeApplication;
import com.tbaehr.lunchtime.R;
import com.tbaehr.lunchtime.controller.DashboardFragment;
import com.tbaehr.lunchtime.model.Offer;
import com.tbaehr.lunchtime.model.Offers;
import com.tbaehr.lunchtime.view.HorizontalSliderView;
import com.tbaehr.lunchtime.view.IDashboardViewContainer;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by timo.baehr@gmail.com on 31.12.16.
 */
public class DashboardPresenter extends BasePresenter<IDashboardViewContainer>
        implements DataProvider.LoadJobListener<List<Offers>> {

    private DataProvider dataProvider;

    private DashboardFragment dashboardFragment;

    private Timer timer;

    public DashboardPresenter(DashboardFragment fragment) {
        this.dashboardFragment = fragment;
    }

    @Override
    public void bindView(IDashboardViewContainer view) {
        super.bindView(view);
        dataProvider = new DataProvider();
        dataProvider.syncOffers(this);

        List<Offers> offersList = dataProvider.loadOffersFromCache();
        startTimeBasedRefresh(offersList);

        if (!view.isInitialized()) {
            presentOffers(offersList);
        }
    }

    @Override
    public void unbindView() {
        stopTimeBasedRefresh();
        super.unbindView();
    }

    private void startTimeBasedRefresh(List<Offers> offersList) {
        Set<Date> refreshDates = new HashSet<>();
        for (Offers offers : offersList) {
            refreshDates.addAll(offers.getUiRefreshDates());
        }
        startTimers(refreshDates);
    }

    private void startTimers(Set<Date> dates) {
        if (timer == null) {
            timer = new Timer();
        }
        for (Date date : dates) {
            timer.schedule(createTimerTask(), date);
        }
    }

    private TimerTask createTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                dashboardFragment.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        presentOffers(dataProvider.loadOffersFromCache());
                    }
                });
            }
        };
    }

    private void stopTimeBasedRefresh() {
        timer.cancel();
    }

    @Override
    public void onDownloadStarted() {
        dashboardFragment.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getView().clearOffers();
                getView().hideNoOffersView();
                getView().setProgressBarVisibility(true);
            }
        });
    }

    @Override
    public void onDownloadFailed(final String message) {
        dashboardFragment.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                presentOffers(dataProvider.loadOffersFromCache());
                Toast.makeText(LunchtimeApplication.getContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onNewOffersDownloaded(List<Offers> offersList) {
        IDashboardViewContainer view = getView();
        if (view == null) {
            return;
        }

        view.setProgressBarVisibility(false);
        presentOffers(offersList);
    }

    private void presentOffers(List<Offers> offersList) {
        IDashboardViewContainer view = getView();
        boolean foundOffers = false;

        view.setProgressBarVisibility(false);
        view.hideNoOffersView();
        view.clearOffers();

        for (Offers nearbyOffers : offersList) {
            HorizontalSliderView.OnSliderItemClickListener onSliderItemClickListener = new HorizontalSliderView.OnSliderItemClickListener() {
                @Override
                public void onSliderItemClick(Offer offer, View view) {
                    // TODO: Add action on item clicks
                    Snackbar.make(view, R.string.excellentChoice, Snackbar.LENGTH_SHORT).show();
                }
            };
            if (nearbyOffers.isEmpty()) {
                continue;
            }

            foundOffers = true;

            HorizontalSliderView.OnSliderHeaderClickListener headerClickListener = new HorizontalSliderView.OnSliderHeaderClickListener() {
                @Override
                public void onSliderHeaderClick() {
                    // TODO: Add action on header clicks
                }
            };
            view.addOffers(
                    nearbyOffers.getRestaurantName(),
                    nearbyOffers.getRestaurantDescription(),
                    nearbyOffers.getOffers(),
                    headerClickListener,
                    onSliderItemClickListener
            );
        }

        if (!foundOffers) {
            int[] noOffersMessages = new int[] {
                    R.string.no_offers_today1,
                    R.string.no_offers_today2,
                    R.string.no_offers_today3,
                    R.string.no_offers_today4,
                    R.string.no_offers_today5
            };
            int randomNumber = (int) (Math.random() * 5);
            view.enableNoOffersView(noOffersMessages[randomNumber]);
        }
    }
}
