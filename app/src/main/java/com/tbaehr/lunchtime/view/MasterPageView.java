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

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.tbaehr.lunchtime.R;
import com.tbaehr.lunchtime.controller.DashboardFragment;
import com.tbaehr.lunchtime.controller.HelpFragment;
import com.tbaehr.lunchtime.controller.MasterPageActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tbaehr.lunchtime.view.MasterPageView.TAG_DASHBOARD_FRAGMENT;
import static com.tbaehr.lunchtime.view.MasterPageView.TAG_HELP_FRAGMENT;

/**
 * Created by timo.baehr@gmail.com on 31.12.16.
 */
public class MasterPageView implements IMasterPageViewContainer {

    public static final String TAG_DASHBOARD_FRAGMENT = DashboardFragment.class.getCanonicalName();

    public static final String TAG_HELP_FRAGMENT = HelpFragment.class.getCanonicalName();

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;

    private FragmentHolder fragmentHolder;

    public MasterPageView(MasterPageActivity activity, FragmentManager fragmentManager) {
        activity.setContentView(R.layout.activity_master_page);
        activity.setAsFullScreenActivity();
        View rootView = activity.findViewById(R.id.drawer_layout);
        ButterKnife.bind(this, rootView);
        fragmentHolder = new FragmentHolder(fragmentManager);
    }

    @Override
    public void showToolbar(AppCompatActivity activity, NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener) {
        activity.setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                activity,
                drawer,
                toolbar,
                R.string.nav_content_desc_drawer_open,
                R.string.nav_content_desc_drawer_close
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);
    }

    @Override
    public void setToolbarTitle(String title) {
        collapsingToolbar.setTitle(title);
    }

    @Override
    public void showDashboardFragment() {
        appBarLayout.setExpanded(true, false);
        fragmentHolder.showDashboardFragment();
    }

    @Override
    public void showHelpFragment() {
        appBarLayout.setExpanded(false, false);
        fragmentHolder.showHelpFragment();
    }

    @Override
    public void closeDrawer() {
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else {
            return false;
        }
    }
}

class FragmentHolder {

    private DashboardFragment dashboard;
    private HelpFragment help;

    private Fragment previousFragment, activeFragment;

    private FragmentManager fragmentManager;

    FragmentHolder(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;

        // Add 'dashboard' page
        dashboard = (DashboardFragment) fragmentManager.findFragmentByTag(TAG_DASHBOARD_FRAGMENT);
        if (dashboard == null) {
            dashboard = new DashboardFragment();
        }

        // Add 'help' page
        help = (HelpFragment) fragmentManager.findFragmentByTag(TAG_HELP_FRAGMENT);
        if (help == null) {
            help = new HelpFragment();
        }
    }

    void showDashboardFragment() {
        previousFragment = activeFragment;
        activeFragment = dashboard;
        showFragment();
    }

    void showHelpFragment() {
        previousFragment = activeFragment;
        activeFragment = help;
        showFragment();
    }

    private void showFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (previousFragment != null) {
            transaction.remove(previousFragment);
        }
        transaction.replace(R.id.fragment_container, activeFragment, activeFragment.getClass().getCanonicalName()).commit();
    }
}
