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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.View;

import com.propaneapps.tomorrow.presenter.BasePresenter;
import com.tbaehr.lunchtime.BuildConfig;
import com.tbaehr.lunchtime.LunchtimeApplication;
import com.tbaehr.lunchtime.R;
import com.tbaehr.lunchtime.controller.BaseFragment;
import com.tbaehr.lunchtime.view.HelpViewContainer;

/**
 * Created by timo.baehr@gmail.com on 31.12.16.
 */
public class HelpPresenter extends BasePresenter<HelpViewContainer> {

    private static final String URI_GOOGLE_PLUS = "https://plus.google.com/u/0/communities/115383591691417596235";
    private static final String URI_PLAY_STORE = "https://play.google.com/store/apps/details?id=com.tbaehr.lunchtime";

    private BaseFragment fragment;

    public HelpPresenter(BaseFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void bindView(HelpViewContainer view) {
        super.bindView(view);
        String debug = BuildConfig.DEBUG ? "-debug" : "";
        String versionName = LunchtimeApplication.getContext().getString(R.string.help_about_version, BuildConfig.VERSION_NAME);
        getView().setVersionName(versionName + debug);
        setClickListeners();
    }

    @Override
    public void unbindView() {
        getView().removeOnClickListeners();
        super.unbindView();
    }

    private void setClickListeners() {
        HelpViewContainer view = getView();
        view.setOnGooglePlusCardClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGooglePlusLinkClicked();
            }
        });
        view.setOnPlayStoreCardClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlayStoreLinkClicked();
            }
        });
        view.setOnTermsOfUsageClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowTermsOfUsageClicked();
            }
        });
        view.setOnAboutTeamClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAboutTeamClicked();
            }
        });

    }

    private void onShowTermsOfUsageClicked() {
        new AlertDialog.Builder(fragment.getActivity())
                .setTitle(R.string.agb_terms_of_use)
                .setMessage(R.string.agb)
                .setPositiveButton(R.string._close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private void onPlayStoreLinkClicked() {
        fragment.openUrl(Uri.parse(URI_PLAY_STORE));
    }

    private void onGooglePlusLinkClicked() {
        fragment.openUrl(Uri.parse(URI_GOOGLE_PLUS));
    }

    private void onAboutTeamClicked() {
        new AlertDialog.Builder(fragment.getActivity())
                .setTitle(R.string.help_about_team)
                .setMessage(R.string.help_about_team_content)
                .setPositiveButton(R.string._close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }
}
