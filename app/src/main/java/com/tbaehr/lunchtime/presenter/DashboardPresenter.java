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

import com.propaneapps.tomorrow.presenter.BasePresenter;
import com.tbaehr.lunchtime.DataProvider;
import com.tbaehr.lunchtime.R;
import com.tbaehr.lunchtime.model.Offer;
import com.tbaehr.lunchtime.model.Offers;
import com.tbaehr.lunchtime.view.HorizontalSliderView;
import com.tbaehr.lunchtime.view.IDashboardViewContainer;

import java.util.Date;
import java.util.Map;

/**
 * Created by timo.baehr@gmail.com on 31.12.16.
 */
public class DashboardPresenter extends BasePresenter<IDashboardViewContainer> {

    private boolean foundOffers = false;

    @Override
    public void bindView(IDashboardViewContainer view) {
        super.bindView(view);
        DataProvider dataProvider = new DataProvider();
        boolean isInitialized = getView().isInitialized();
        // TODO: Insert longitue, latitude and radius (in km)
        Map<String, Date> nearbyRestaurants = dataProvider.getNearbyRestaurants(-1, -1, -1);
        for (String key : nearbyRestaurants.keySet()) {
            Offers nearbyOffers = dataProvider.getNearbyOffers(key);

            HorizontalSliderView.OnSliderItemClickListener onSliderItemClickListener = new HorizontalSliderView.OnSliderItemClickListener() {
                @Override
                public void onSliderItemClick(Offer offer, View view) {
                    // TODO: Add action on item clicks
                    Snackbar.make(view, R.string.excellentChoice, Snackbar.LENGTH_SHORT).show();
                }
            };
            if (nearbyOffers.isEmpty() || isInitialized) {
                continue;
            }

            foundOffers = true;

            HorizontalSliderView.OnSliderHeaderClickListener headerClickListener = new HorizontalSliderView.OnSliderHeaderClickListener() {
                @Override
                public void onSliderHeaderClick() {
                    // TODO: Add action on header clicks
                }
            };
            getView().addOffers(
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
            getView().enableNoOffersView(noOffersMessages[randomNumber]);
        }
    }

    @Override
    public void unbindView() {
        super.unbindView();
    }
}
