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
package com.tbaehr.lunchtime;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MasterPageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Unbinder butterKnife;

    @BindView(R.id.drawer_layout)
    DrawerLayout viewContainer;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private FragmentHolder fragmentHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_page);
        butterKnife = ButterKnife.bind(this);

        initActionBar();
        fragmentHolder = new FragmentHolder(this, savedInstanceState);
    }

    private void initActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, viewContainer, toolbar, R.string.nav_content_desc_drawer_open, R.string.nav_content_desc_drawer_close);
        viewContainer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_dashboard:
                fragmentHolder.showDashboardFragment();
                break;
            case R.id.nav_help:
                fragmentHolder.showHelpFragment();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        butterKnife.unbind();
    }
}


class FragmentHolder {

    private FragmentDashboard dashboard;
    private FragmentHelp help;

    private static final String TAG_DASHBOARD = "dashboard";
    private static final String TAG_HELP = "help";

    private Fragment previousFragment, activeFragment;

    private AppCompatActivity context;

    FragmentHolder(AppCompatActivity context, Bundle savedInstance) {
        this.context = context;

        // Add 'dashboard' page
        dashboard = (FragmentDashboard) context.getSupportFragmentManager().findFragmentByTag(TAG_DASHBOARD);
        if (dashboard == null) {
            dashboard = new FragmentDashboard();
        }

        // Add 'help' page
        help = (FragmentHelp) context.getSupportFragmentManager().findFragmentByTag(TAG_HELP);
        if (help == null) {
            help = new FragmentHelp();
        }

        // Default: Show 'dashboard' page
        if (savedInstance == null) {
            showDashboardFragment();
        }
    }

    void showDashboardFragment() {
        context.getSupportActionBar().setTitle(R.string.app_name);
        previousFragment = activeFragment;
        activeFragment = dashboard;
        showFragment(TAG_DASHBOARD);
    }

    void showHelpFragment() {
        context.getSupportActionBar().setTitle(R.string.nav_item_help);
        previousFragment = activeFragment;
        activeFragment = help;
        showFragment(TAG_HELP);
    }

    private void showFragment(String activeFragmentTag) {
        FragmentManager fragmentManager = context.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (previousFragment != null) {
            transaction.remove(previousFragment);
        }
        transaction.replace(R.id.fragment_container, activeFragment, activeFragmentTag).commit();
    }
}
