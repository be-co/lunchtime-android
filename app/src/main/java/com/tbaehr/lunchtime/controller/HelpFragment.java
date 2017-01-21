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
package com.tbaehr.lunchtime.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tbaehr.lunchtime.R;
import com.tbaehr.lunchtime.presenter.HelpPresenter;
import com.tbaehr.lunchtime.view.HelpViewContainer;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by timo.baehr@gmail.com on 26.12.16.
 */
public class HelpFragment extends BaseFragment<HelpViewContainer, HelpPresenter> {

    // TODO: MVP pattern is not correctly used right now! Fix it!

    private static final String URI_GOOGLE_PLUS = "https://plus.google.com/u/0/communities/115383591691417596235";
    private static final String URI_PLAY_STORE = "https://play.google.com/store/apps/details?id=com.tbaehr.lunchtime";

    private HelpViewContainer view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = new HelpViewContainer(inflater, container);
        ButterKnife.bind(this, view.getRootView());
        return view.getRootView();
    }

    @Override
    public HelpViewContainer getViewLayer() {
        return view;
    }

    @Override
    public Class<? extends HelpPresenter> getTypeClazz() {
        return HelpPresenter.class;
    }

    @Override
    public HelpPresenter create() {
        return new HelpPresenter();
    }

    @OnClick(R.id.help_terms_of_usage)
    void onShowTermsOfUsageClicked() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.agb_terms_of_use)
                .setMessage(R.string.agb)
                .setPositiveButton(R.string._close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    @OnClick(R.id.help_feedback1)
    void onPlayStoreLinkClicked() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URI_PLAY_STORE)));
    }

    @OnClick(R.id.help_feedback2)
    void onGooglePlusLinkClicked() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URI_GOOGLE_PLUS)));
    }
}
