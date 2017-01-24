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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.tbaehr.lunchtime.R;
import com.tbaehr.lunchtime.view.IMasterPageViewContainer;

import static com.tbaehr.lunchtime.view.MasterPageView.TAG_DASHBOARD_FRAGMENT;
import static com.tbaehr.lunchtime.view.MasterPageView.TAG_HELP_FRAGMENT;

/**
 * Created by timo.baehr@gmail.com on 31.12.16.
 */
public class MasterPagePresenter extends CustomBasePresenter<IMasterPageViewContainer>
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static String KEY_TOOLBAR_TITLE = "toolbarTitle";

    private final static String KEY_ACTIVE_FRAGMENT = "activeFragment";

    private String toolbarTitle;

    private String activeFragment;

    private AppCompatActivity activity;

    public MasterPagePresenter(AppCompatActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            toolbarTitle = getDashboardTitle();
            activeFragment = TAG_DASHBOARD_FRAGMENT;
        } else {
            toolbarTitle = savedInstanceState.getString(KEY_TOOLBAR_TITLE);
            activeFragment = savedInstanceState.getString(KEY_ACTIVE_FRAGMENT);
        }
    }

    @Override
    public void bindView(IMasterPageViewContainer view) {
        super.bindView(view);
        view.showToolbar(activity, this);
        view.setToolbarTitle(toolbarTitle);

        if (activeFragment.equals(TAG_DASHBOARD_FRAGMENT)) {
            view.showDashboardFragment();
        } else if (activeFragment.equals(TAG_HELP_FRAGMENT)) {
            view.showHelpFragment();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_TOOLBAR_TITLE, toolbarTitle);
        outState.putString(KEY_ACTIVE_FRAGMENT, activeFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // TODO: Check visitor pattern for a MVP improvement
        switch (item.getItemId()) {
            case R.id.nav_dashboard:
                toolbarTitle = getDashboardTitle();
                activeFragment = TAG_DASHBOARD_FRAGMENT;
                getView().setToolbarTitle(toolbarTitle);
                getView().showDashboardFragment();
                break;
            case R.id.nav_help:
                toolbarTitle = getString(R.string.nav_item_help);
                activeFragment = TAG_HELP_FRAGMENT;
                getView().setToolbarTitle(toolbarTitle);
                getView().showHelpFragment();
                break;
        }

        getView().closeDrawer();

        return true;
    }

    private String getDashboardTitle() {
        return getString(R.string.lunch_time);
    }

    private String getString(@StringRes int resId) {
        return activity.getApplicationContext().getString(resId);
    }
}
