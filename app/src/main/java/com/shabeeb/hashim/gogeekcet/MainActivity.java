package com.shabeeb.hashim.gogeekcet;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.androidnetworking.AndroidNetworking;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //fragment transaction
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MainFragment fragment = new MainFragment();
        fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
        //intitialize android fast android networking
        AndroidNetworking.initialize(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        Fragment f = getFragmentManager().findFragmentById(R.id.fragment_container);
        if (f instanceof MainFragment)
            super.onBackPressed();
        else {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new MainFragment());
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            //ft.addToBackStack(null);
            ft.replace(R.id.fragment_container, new AboutFragment(), "About Frag");
            ft.commit();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            //ft.addToBackStack(null);
            ft.replace(R.id.fragment_container, new MainFragment(), "MainFragment");
            ft.commit();
        } else if (id == R.id.nav_order) {
            if (NetworkDetector.isConnected(this)) {

                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                //ft.addToBackStack(null);
                ft.replace(R.id.fragment_container, new OrderItem(), "OrderFragment Frag");
                ft.commit();
            } else {
                Snackbar.make(findViewById(R.id.fragment_container), "No internet Connection", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        } else if (id == R.id.nav_notes) {
            if (NetworkDetector.isConnected(this)) {

                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                //ft.addToBackStack(null);
                ft.replace(R.id.fragment_container, new LectureNotesFragment(), "LectureNote");
                ft.commit();
            } else {
                Snackbar.make(findViewById(R.id.fragment_container), "No internet Connection", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }


        } else if (id == R.id.nav_project) {
            if (NetworkDetector.isConnected(this)) {

                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                //ft.addToBackStack(null);
                ft.replace(R.id.fragment_container, new ProjectReportFragment(), "Project Report");
                ft.commit();
            } else {
                Snackbar.make(findViewById(R.id.fragment_container), "No internet Connection", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        } else if (id == R.id.nav_about) {
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            //ft.addToBackStack(null);
            ft.replace(R.id.fragment_container, new AboutFragment(), "About Frag");
            ft.commit();

        } else if (id == R.id.nav_credits) {
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            //ft.addToBackStack(null);
            ft.replace(R.id.fragment_container, new CreditsFragment(), "Credits Frag");
            ft.commit();

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
