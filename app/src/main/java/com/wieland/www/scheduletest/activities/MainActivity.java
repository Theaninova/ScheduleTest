package com.wieland.www.scheduletest.activities;

import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.widget.Toast;

import com.wieland.www.scheduletest.schedule.BackgroundSync;
import com.wieland.www.scheduletest.ui.PagerAdapter;
import com.wieland.www.scheduletest.R;
import com.wieland.www.scheduletest.schedule.Schedule;
import com.wieland.www.scheduletest.ui.TabFragment;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

//TODO: wire the OnScrollListener from the ListView manually http://nlopez.io/swiperefreshlayout-with-listview-done-right/

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener, TabFragment.OnHeadlineSelectedListener {

    public static boolean firstTimeCreated = true;
    private PagerAdapter pagerAdapter;
    private ArrayList<TabFragment> tabFragments;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //The Schedule is being updated every 15 Minutes, so a force refresh every start would be a waste of time
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabFragments = new ArrayList<>();

        SharedPreferences pref = this.getSharedPreferences("Tralala", MODE_PRIVATE);

        //Checking if User is using the App for the First time
        if (pref.getBoolean("FirstStart", true)) {
            //If he is, Login Screen is being shown
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        initTabs();

        if (savedInstanceState != null) {
            for (int i = 0; i < tabFragments.size(); i++) {
                getSupportFragmentManager().beginTransaction().attach(tabFragments.get(i)).commit();
                tabFragments.get(i).context = this;
            }
        }

        //Setting up background sync
        final Context context = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //Job Scheduler requires API 21+
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(context.JOB_SCHEDULER_SERVICE);
            try {
                jobScheduler.cancel(1);
            } catch (NullPointerException e) {

            }
            JobInfo.Builder builder = new JobInfo.Builder(1,
                    new ComponentName(getPackageName(),
                            BackgroundSync.class.getName()));
            builder.setPeriodic(15 * 60 * 1000);//15 minutes
            if (jobScheduler.schedule(builder.build()) <= JobScheduler.RESULT_FAILURE) {
                Snackbar.make(findViewById(R.id.content_main2), "Fail", Snackbar.LENGTH_SHORT).show();
                System.err.println("Something went wrong with the job Scheduler...");
            } else {
                System.out.println("jobScheduler looking good so far...");
            }
        } else {
            System.err.println("Something went wrong with the job Scheduler...");
            try {
                Schedule.refresh(this);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void initTabs() {
        SharedPreferences pref = this.getSharedPreferences("Tralala", MODE_PRIVATE);

        //TODO: More than one personalized view
        for (int i = 1; i <= pref.getInt(Schedule.PAGES_COUNT, 0); i++) {
            TabFragment tabFragment = new TabFragment();
            tabFragment.setIndex(i, 0);
            tabFragments.add(tabFragment);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.removeAllTabs();
        for (int i = 1; i <= pref.getInt(Schedule.PAGES_COUNT, 0); i++) {
            tabLayout.addTab(tabLayout.newTab());
            //setting tab title
            if (Schedule.getDate(1, this).contains("erscheint"))
                tabLayout.getTabAt(i - 1).setText("In Arbeit");
            else
                tabLayout.getTabAt(i - 1).setText(Schedule.getDate(i, this));
        }

        final ViewPager viewPager = findViewById(R.id.pager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabFragments);
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setTitle(Schedule.getUpdateDate(this));

        //checking the last selection of the user (Allgemein, Personalisiert, Custom Query)
        if (pref.getInt("customizedLayout2", 1) == 1)
            navigationView.setCheckedItem(R.id.nav_heute);
        else if (pref.getInt("customizedLayout2", 1) == 2)
            navigationView.setCheckedItem(R.id.nav_slideshow);
        else
            navigationView.setCheckedItem(R.id.nav_news);
    }

    public void onRefreshed() {
        Refresh refresh = new Refresh(this, Schedule.getUpdateDate(this));
        refresh.execute();
    }

    private void refreshTabs() {
        try {
            for (int i = 0; i < tabFragments.size(); i++) {
                tabFragments.get(i).refresh();
            }
        } catch (NullPointerException e) {
            initTabs();
        }
    }

    //For closing App Drawer when pressing back. Othewise the App would close either way
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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

    //For opening settings
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

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
        int id = item.getItemId();

        if (id == R.id.nav_heute) {
            SharedPreferences pref = this.getSharedPreferences("Tralala", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            editor.putInt("customizedLayout2", 1); //for communicating with the Fragments
            editor.commit();

            refreshTabs();
        } else if (id == R.id.nav_news) {
            SharedPreferences pref = this.getSharedPreferences("Tralala", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            editor.putInt("customizedLayout2", 3); //for communicating with the Fragments
            editor.commit();

            refreshTabs();
        } else if (id == R.id.nav_slideshow) {
            SharedPreferences pref = this.getSharedPreferences("Tralala", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            editor.putInt("customizedLayout2", 2); //for communicating with the Fragments
            editor.commit();

            refreshTabs();
        } else if (id == R.id.nav_share) {
            //Let the user send a Email
            Intent i = new Intent(Intent.ACTION_SENDTO);
            i.setType("text/plain");
            i.setData(Uri.parse("mailto:" + "myroro.dev@gmail.com"));
            i.putExtra(Intent.EXTRA_SUBJECT, "myRoRo Feedback");
            try {
                startActivity(Intent.createChooser(i, "Email senden..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Snackbar.make(findViewById(R.id.content_main2), "Keine E-Mail App gefunden.", Snackbar.LENGTH_SHORT).show();
            }

        } else if (id == R.id.nav_send) {
            Uri uri = Uri.parse("http://www.romain-rolland-gymnasium.eu/index.php?id=271");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class Refresh extends AsyncTask<Void, Void, Boolean> {
        private final Context context;
        private IOException e;
        private final String compare1;

        Refresh(Context context, String compare1) {
            this.context = context;
            this.compare1 = compare1;
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
            String compare3 = Schedule.getUpdateDate(context);

            if (!success) {
                if (e.getMessage() == "HTTP error fetching URL") {
                    Intent intent = new Intent(this.context, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Snackbar.make(findViewById(R.id.content_main2), "Keine Verbindung möglich.", Snackbar.LENGTH_SHORT).show();
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //.equals() causes crash on <4.0 devices
                if (Objects.equals(compare1, compare3)) {
                    Snackbar.make(findViewById(R.id.content_main2), "Plan ist bereits aktuell.", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(findViewById(R.id.content_main2), "Neuer Plan geladen: " + Schedule.getUpdateDate(context), Snackbar.LENGTH_SHORT).show();
                    initTabs();
                }
            } else {
                initTabs();
            }

            try {
                for (int i = 0; i < tabFragments.size(); i++) {
                    tabFragments.get(i).swipeRefreshOff();
                }
            } catch (NullPointerException e) {
                initTabs();
            }
        }
    }

    @Override
    public void onRefresh() {
        String compare1 = Schedule.getUpdateDate(this);

        Refresh refresh = new Refresh(this, compare1);
        refresh.execute();
    }
}