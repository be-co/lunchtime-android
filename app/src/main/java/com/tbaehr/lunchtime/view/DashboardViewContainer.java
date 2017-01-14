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
package com.tbaehr.lunchtime.view;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tbaehr.lunchtime.R;
import com.tbaehr.lunchtime.model.Offer;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by timo.baehr@gmail.com on 31.12.16.
 */
public class DashboardViewContainer implements IDashboardViewContainer {

    private Context context;

    private View rootView;

    @BindView(R.id.no_offers)
    TextView noOffersView;

    @BindView(R.id.view_container)
    LinearLayout viewContainer;

    @BindView(R.id.progress_bar_loading)
    LinearLayout progressBarLoading;

    public DashboardViewContainer(Context context, LayoutInflater inflater, ViewGroup container) {
        rootView = inflater.inflate(R.layout.content_dashboard, container, false);
        ButterKnife.bind(this, rootView);
        this.context = context;
    }

    @Override
    public void addOffers(String sectionTitle, String shortDescription, List<Offer> offers, HorizontalSliderView.OnSliderHeaderClickListener headerClickListener, HorizontalSliderView.OnSliderItemClickListener sliderItemClickListener) {
        HorizontalSliderView sliderView = new HorizontalSliderView(context, sectionTitle, shortDescription, offers, headerClickListener, sliderItemClickListener);
        viewContainer.addView(sliderView, 0);
    }

    @Override
    public void enableNoOffersView(@StringRes int message) {
        noOffersView.setText(message);
        noOffersView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoOffersView() {
        noOffersView.setVisibility(View.GONE);
    }

    public void setProgressBarVisibility(boolean visible) {
        progressBarLoading.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public View getRootView() {
        return rootView;
    }

    @Override
    public boolean isInitialized() {
        int childCount = viewContainer.getChildCount();
        return childCount > 3;
    }
}
