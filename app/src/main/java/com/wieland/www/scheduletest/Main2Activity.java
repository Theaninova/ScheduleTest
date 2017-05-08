package com.wieland.www.scheduletest;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.Toast;

import org.jsoup.nodes.Document;

import java.io.IOException;

//TODO: wire the OnScrollListener from the ListView manually http://nlopez.io/swiperefreshlayout-with-listview-done-right/

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //viewPager.setCurrentItem(tab.getPosition());
                onRefresh();
            }
        });





        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        try {
            Schedule.refresh(getApplicationContext());
        } catch (IOException e) {
            if (e.getMessage() == "HTTP error fetching URL") {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                alertDialogBuilder.setTitle("Verbindungsfehler");
                alertDialogBuilder.setMessage("Gespeicherter Plan von " + Schedule.getDate(1, getApplicationContext()) + " und " + Schedule.getDate(2, getApplicationContext()) + " wird geladen.");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }
    }

    public static TabHost createTabHost(Context context) {
        TabWidget tabWidget = new TabWidget(context);
        tabWidget.setId(android.R.id.tabs);

        FrameLayout frame = new FrameLayout(context);
        frame.setId(android.R.id.tabcontent);
        LinearLayout.LayoutParams frameLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        frameLayoutParams.setMargins(4, 4, 4, 4);
        frame.setLayoutParams(frameLayoutParams);

        LinearLayout tabHostLayout = new LinearLayout(context);
        tabHostLayout.setOrientation(LinearLayout.VERTICAL);
        tabHostLayout.addView(tabWidget);
        tabHostLayout.addView(frame);

        TabHost tabHost = new TabHost(context, null);
        tabHost.addView(tabHostLayout);
        tabHost.setup();

        return tabHost;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        final boolean willBeRepeated = true;

        //SharedPreferences.Editor editor = sharedPref.edit();
        SharedPreferences preferences = getSharedPreferences("Tralala", MODE_PRIVATE);

        if (id == R.id.nav_heute) {


        } else if (id == R.id.nav_morgen) {


        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_share) {
            Intent i = new Intent(Intent.ACTION_SENDTO);
            i.setType("text/plain");
            i.setData(Uri.parse("mailto:" + "wulkanat@gmail.com"));
            i.putExtra(Intent.EXTRA_SUBJECT, "myRoRo Feedback");
            //i.putExtra(Intent.EXTRA_TEXT   , "body of email");
            try {
                startActivity(Intent.createChooser(i, "Email senden..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(Main2Activity.this, "Keine E-Mail App gefunden.", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.nav_send) {
            Uri uri = Uri.parse("http://www.romain-rolland-gymnasium.eu/index.php?id=271");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (id == R.id.nav_news) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class Refresh extends AsyncTask<Void, Void, Boolean> {
        private final Context context;
        private IOException e;

        Refresh(Context context) {
            this.context = context;
        }

        public Boolean doInBackground(Void... params) {
            try {
                Schedule.refresh(context);
                return true;
            } catch (IOException e) {
                this.e = e;
                return false;
            }
        }

        public void onPostExecute(final Boolean success) {
            if (!success) {
                if (e.getMessage() == "HTTP error fetching URL") {
                    Intent intent = new Intent(this.context, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(this.context, "Keine Verbindung möglich.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }
    }

    @Override
    public void onRefresh() {

        Document compare1 = Schedule.getSchedule(1, this);
        Document compare2 = Schedule.getSchedule(2, this);

        Refresh refresh = new Refresh(this);
        refresh.execute();

        if (compare1.toString() == Schedule.getSchedule(1, this).toString() && compare2.toString() == Schedule.getSchedule(2, this).toString()) {
            Toast toast = Toast.makeText(this, "Plan ist bereits aktuell (nicht sicher).", Toast.LENGTH_SHORT);
            toast.show();
            Tab1 tab1 = (Tab1) pagerAdapter.getItem(1);
            tab1.setIndex(1);
            //tab1.refreshContent();
            Tab1 tab2 = (Tab1) pagerAdapter.getItem(2);
            tab2.setIndex(2);
            //tab2.refreshContent();

        } else {
            Tab1 tab1 = (Tab1) pagerAdapter.getItem(1);
            tab1.setIndex(1);
            //tab1.refreshContent();
            Tab1 tab2 = (Tab1) pagerAdapter.getItem(2);
            tab2.setIndex(2);
            //tab2.refreshContent();
        }
    }
}