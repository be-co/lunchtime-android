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

import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tbaehr.lunchtime.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by timo.baehr@gmail.com on 31.12.16.
 */
public class HelpViewContainer implements IHelpViewContainer {

    private View rootView;

    @BindView(R.id.help_about1_text)
    TextView mHelpAbout1;

    @BindView(R.id.help_about2_text)
    TextView mHelpAbout2;

    @BindView(R.id.help_terms_of_usage_card)
    CardView termsOfUsageCard;

    @BindView(R.id.help_feedback1_card)
    CardView playStoreCard;

    @BindView(R.id.help_feedback2_card)
    CardView googlePlusCard;

    public HelpViewContainer(LayoutInflater inflater, ViewGroup container) {
        rootView = inflater.inflate(R.layout.content_help, container, false);
        ButterKnife.bind(this, rootView);
    }

    @Override
    public View getRootView() {
        return rootView;
    }

    @Override
    public void setVersionName(String versionName) {
        mHelpAbout1.setText(versionName);
    }

    @Override
    public void setOnPlayStoreCardClickListener(View.OnClickListener onPlayStoreCardClickListener) {
        playStoreCard.setOnClickListener(onPlayStoreCardClickListener);
    }

    @Override
    public void setOnGooglePlusCardClickListener(View.OnClickListener onGooglePlusCardClickListener) {
        googlePlusCard.setOnClickListener(onGooglePlusCardClickListener);
    }

    @Override
    public void setOnTermsOfUsageClickListener(View.OnClickListener onTermsOfUsageClickListener) {
        termsOfUsageCard.setOnClickListener(onTermsOfUsageClickListener);
    }

    @Override
    public void setOnAboutTeamClickListener(View.OnClickListener onAboutTeamClickListener) {
        mHelpAbout2.setOnClickListener(onAboutTeamClickListener);
    }

    @Override
    public void removeOnClickListeners() {
        termsOfUsageCard.setOnClickListener(null);
        playStoreCard.setOnClickListener(null);
        googlePlusCard.setOnClickListener(null);
        mHelpAbout2.setOnClickListener(null);
    }
}
