package com.wieland.www.scheduletest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

//TODO: wire the OnScrollListener from the ListView manually http://nlopez.io/swiperefreshlayout-with-listview-done-right/

public class Main2Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener, Tab1.OnHeadlineSelectedListener, Tab2.OnHeadlineSelectedListener2 {

    public static boolean firstTimeCreated = true;
    private PagerAdapter pagerAdapter;
    private android.support.v4.app.Fragment tab1;
    private android.support.v4.app.Fragment tab2;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        this.tab1 = pagerAdapter.getItem(0);
        this.tab2 = pagerAdapter.getItem(1);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //SET TAB TEXT
        String putIn1;
        if (Schedule.getDate(1, this).contains("erscheint"))
            putIn1 = "Nicht verfügbar.";
        else
            putIn1 = Schedule.getDate(1, this);

        tabLayout.getTabAt(0).setText(putIn1); //setting Tab Title (Date)

        //noinspection UnusedAssignment
        putIn1 = "";
        if (Schedule.getDate(2, this).contains("erscheint"))
            putIn1 = "Nicht verfügbar.";
        else
            putIn1 = Schedule.getDate(2, this);
        tabLayout.getTabAt(1).setText(putIn1);
        //SET TAB TEXT END

        setTitle(Schedule.getUpdateDate(1, this));

        SharedPreferences pref = this.getSharedPreferences("Tralala", MODE_PRIVATE);
        if (pref.getInt("customizedLayout2", 1) == 1)
            navigationView.setCheckedItem(R.id.nav_heute);
        else if (pref.getInt("customizedLayout2", 1) == 2)
            navigationView.setCheckedItem(R.id.nav_slideshow);
        else
            navigationView.setCheckedItem(R.id.nav_news);

        Bundle loaderBndl = new Bundle();
        final Context context = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //Job Scheduler requires API 21+
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(context.JOB_SCHEDULER_SERVICE);
            jobScheduler.cancelAll();
            JobInfo.Builder builder = new JobInfo.Builder(1,
                    new ComponentName(getPackageName(),
                            BackgroundSync.class.getName()));
            builder.setPeriodic(15 * 60 * 1000);//15 minutes
            if (jobScheduler.schedule(builder.build()) <= 0) {
                Toast.makeText(getApplicationContext(),
                        "Fail", Toast.LENGTH_SHORT)
                        .show();
            }
        }

        getSupportLoaderManager().destroyLoader(1);
        getSupportLoaderManager().initLoader(1, loaderBndl, new LoaderManager.LoaderCallbacks<ArrayList<String>>() {
            @Override
            public Loader<ArrayList<String>> onCreateLoader(final int id, final Bundle args) {
                return new RefreshLoader(context);
            }

            @Override
            public void onLoadFinished(final Loader<ArrayList<String>> loader, final ArrayList<String> result) {
                if (result == null)
                    return;
                String compare1 = result.get(0);
                String compare2 = result.get(1);
                String compare3 = Schedule.getUpdateDate(1, context);
                String compare4 = Schedule.getUpdateDate(2, context);
                try {
                    result.get(2);
                    if (result.get(2) == "HTTP error fetching URL") {
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                        alertDialogBuilder.setTitle("Verbindungsfehler");
                        alertDialogBuilder.setMessage("Gespeicherter Plan von " + Schedule.getDate(1, getApplicationContext()) + " und " + Schedule.getDate(2, getApplicationContext()) + " wird geladen. Der Plan wurde auf der Webseite zuletzt am " + Schedule.getUpdateDate(1, context) + " aktualisiert.");
                        alertDialogBuilder.setCancelable(false);
                        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                } catch (Exception e) {
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //.equals() causes crash on <4.0 devices
                    if (Objects.equals(compare1, compare3) && Objects.equals(compare2, compare4)) {
                        Toast toast = Toast.makeText(context, "Plan ist bereits aktuell."/*: " + Schedule.getUpdateDate(1, context)*/, Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(context, "Neuer Plan geladen: " + Schedule.getUpdateDate(1, context), Toast.LENGTH_SHORT);
                        toast.show();
                        String tab1Tag = tab1.getTag();
                        String tab2Tag = tab2.getTag();

                        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.remove(tab1);
                        fragmentTransaction.commit();
                        getSupportFragmentManager().executePendingTransactions();
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.add(tab1, tab1Tag);
                        fragmentTransaction.commit();

                        android.support.v4.app.FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction2.remove(tab2);
                        fragmentTransaction2.commit();
                        getSupportFragmentManager().executePendingTransactions();
                        fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction2.add(tab2, tab2Tag);
                        fragmentTransaction2.commit();
                    }
                } else {
                    String tab1Tag = tab1.getTag();
                    String tab2Tag = tab2.getTag();

                    android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.remove(tab1);
                    fragmentTransaction.commit();
                    getSupportFragmentManager().executePendingTransactions();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.add(tab1, tab1Tag);
                    fragmentTransaction.commit();

                    android.support.v4.app.FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction2.remove(tab2);
                    fragmentTransaction2.commit();
                    getSupportFragmentManager().executePendingTransactions();
                    fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction2.add(tab2, tab2Tag);
                    fragmentTransaction2.commit();
                }
            }

            @Override
            public void onLoaderReset(final Loader<ArrayList<String>> loader) {
            }
        }).forceLoad();
    }

    public static class RefreshLoader extends AsyncTaskLoader<ArrayList<String>> {
        Context context;
        String compare1;
        String compare2;
        ArrayList<String> out;

        public RefreshLoader(Context context) {
            super(context);
            this.context = context;
            compare1 = Schedule.getUpdateDate(1, context);
            compare2 = Schedule.getUpdateDate(2, context);
            out = new ArrayList<>();
        }

        @Override
        public ArrayList<String> loadInBackground() {
            out.add(compare1);
            out.add(compare2);
            SharedPreferences pref = context.getSharedPreferences("Tralala", MODE_PRIVATE);

            int counter = 0;
            while (pref.getBoolean(Schedule.IS_ACTIVE, false)) {
                if (counter > 6)
                    pref.edit().putBoolean(Schedule.IS_ACTIVE, false).commit();
                try {
                    Thread.sleep(1000);
                } catch (java.lang.InterruptedException e) {
                }
                counter++;
            }

            try {
                Schedule.refresh(context);
            } catch (IOException e) {
                if (e.getMessage() == "HTTP error fetching URL") {
                    out.add("HTTP error fetching URL");
                } else {
                    out.add("z");
                }
            }
            return out;
        }
    }

    public void onRefreshed() {
        Refresh refresh = new Refresh(this, Schedule.getUpdateDate(1, this), Schedule.getUpdateDate(2, this));
        refresh.execute();
    }

    public void onRefreshed2() {
        Refresh refresh = new Refresh(this, Schedule.getUpdateDate(1, this), Schedule.getUpdateDate(2, this));
        refresh.execute();
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
            SharedPreferences pref = this.getSharedPreferences("Tralala", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            editor.putInt("customizedLayout2", 1); //for communicating with the Fragments
            editor.commit();

            //REFRESH FRAGMENTS
            String tab1Tag = tab1.getTag();
            String tab2Tag = tab2.getTag();

            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.remove(tab1);
            fragmentTransaction.commit();
            getSupportFragmentManager().executePendingTransactions();
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(tab1, tab1Tag);
            fragmentTransaction.commit();

            android.support.v4.app.FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction2.remove(tab2);
            fragmentTransaction2.commit();
            getSupportFragmentManager().executePendingTransactions();
            fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction2.add(tab2, tab2Tag);
            fragmentTransaction2.commit();
            //END REFRESH FRAGMENTS
        } else if (id == R.id.nav_news) {

            SharedPreferences pref = this.getSharedPreferences("Tralala", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            editor.putInt("customizedLayout2", 3); //for communicating with the Fragments
            editor.commit();

            //REFRESH FRAGMENTS
            String tab1Tag = tab1.getTag();
            String tab2Tag = tab2.getTag();

            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.remove(tab1);
            fragmentTransaction.commit();
            getSupportFragmentManager().executePendingTransactions();
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(tab1, tab1Tag);
            fragmentTransaction.commit();

            android.support.v4.app.FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction2.remove(tab2);
            fragmentTransaction2.commit();
            getSupportFragmentManager().executePendingTransactions();
            fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction2.add(tab2, tab2Tag);
            fragmentTransaction2.commit();
            //END REFRESH FRAGMENTS

        } else if (id == R.id.nav_slideshow) {
            SharedPreferences pref = this.getSharedPreferences("Tralala", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            editor.putInt("customizedLayout2", 2); //for communicating with the Fragments
            editor.commit();

            //REFRESH FRAGMENTS
            String tab1Tag = tab1.getTag();
            String tab2Tag = tab2.getTag();

            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.remove(tab1);
            fragmentTransaction.commit();
            getSupportFragmentManager().executePendingTransactions();
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(tab1, tab1Tag);
            fragmentTransaction.commit();

            android.support.v4.app.FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction2.remove(tab2);
            fragmentTransaction2.commit();
            getSupportFragmentManager().executePendingTransactions();
            fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction2.add(tab2, tab2Tag);
            fragmentTransaction2.commit();
            //END REFRESH FRAGMENTS

        } else if (id == R.id.nav_share) {
            Intent i = new Intent(Intent.ACTION_SENDTO);
            i.setType("text/plain");
            i.setData(Uri.parse("mailto:" + "myroro.dev@gmail.com"));
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
        private final String compare1;
        private final String compare2;

        Refresh(Context context, String compare1, String compare2) {
            this.context = context;
            this.compare1 = compare1;
            this.compare2 = compare2;
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
            String compare3 = Schedule.getUpdateDate(1, context);
            String compare4 = Schedule.getUpdateDate(2, context);

            if (!success) {
                if (e.getMessage() == "HTTP error fetching URL") {
                    Intent intent = new Intent(this.context, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(this.context, "Keine Verbindung möglich.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //.equals() causes crash on <4.0 devices
                if (Objects.equals(compare1, compare3) && Objects.equals(compare2, compare4)) {
                    Toast toast = Toast.makeText(context, "Plan ist bereits aktuell."/*: " + Schedule.getUpdateDate(1, context)*/, Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(context, "Neuer Plan geladen: " + Schedule.getUpdateDate(1, context), Toast.LENGTH_LONG);
                    toast.show();
                }
            }

            String tab1Tag = tab1.getTag();
            String tab2Tag = tab2.getTag();

            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.remove(tab1);
            fragmentTransaction.commit();
            getSupportFragmentManager().executePendingTransactions();
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(tab1, tab1Tag);
            fragmentTransaction.commit();

            android.support.v4.app.FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction2.remove(tab2);
            fragmentTransaction2.commit();
            getSupportFragmentManager().executePendingTransactions();
            fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction2.add(tab2, tab2Tag);
            fragmentTransaction2.commit();


            //SET TAB TEXT
            String putIn1 = "";
            if (Schedule.getDate(1, context).contains("erscheint"))
                putIn1 = "Nicht verfügbar.";
            else
                putIn1 = Schedule.getDate(1, context);

            tabLayout.getTabAt(0).setText(putIn1); //setting Tab Title (Date)

            putIn1 = "";
            if (Schedule.getDate(2, context).contains("erscheint"))
                putIn1 = "Nicht verfügbar.";
            else
                putIn1 = Schedule.getDate(2, context);
            tabLayout.getTabAt(1).setText(putIn1);
            //SET TAB TEXT END


            setTitle(Schedule.getUpdateDate(1, context));
        }
    }

    @Override
    public void onRefresh() {
        String compare1 = Schedule.getUpdateDate(1, this);
        String compare2 = Schedule.getUpdateDate(2, this);

        Refresh refresh = new Refresh(this, compare1, compare2);
        refresh.execute();
    }
}