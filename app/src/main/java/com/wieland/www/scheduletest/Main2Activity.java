package com.wieland.www.scheduletest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

//TODO: wire the OnScrollListener from the ListView manually http://nlopez.io/swiperefreshlayout-with-listview-done-right/

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    //TextView MainText;
    SwipeRefreshLayout SwipeRefresh;
    RecyclerView myList;

    MenuItem menu_today;

    private View mProgressView;
    private View mLoginFormView;

    private boolean TodaySelected = true; //for Swipe to Refresh

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        myList = (RecyclerView) findViewById(R.id.theListOfDoom);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        SwipeRefresh.setOnRefreshListener(this);

        mLoginFormView = findViewById(R.id.swiperefresh);
        mProgressView = findViewById(R.id.progressBar12);




        SharedPreferences preferences = getSharedPreferences("Tralala", MODE_PRIVATE);
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

        this.setText(Schedule.getSchedule(1, getApplicationContext()), 1);
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
            showProgress(true);

            this.setText(Schedule.getSchedule(1, getApplicationContext()), 1);
            TodaySelected = true;

            showProgress(false);
        } else if (id == R.id.nav_morgen) {
            showProgress(true);

            this.setText(Schedule.getSchedule(2, getApplicationContext()), 2);
            TodaySelected = false;

            showProgress(false);
        } else if (id == R.id.nav_slideshow) {

            //Intent intent = new Intent(this, Detailed_View.class);
            //startActivity(intent);

        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            Uri uri = Uri.parse("http://www.romain-rolland-gymnasium.eu/index.php?id=271");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setText(Document doc, int index) {
        ArrayList<String> willBeSet = new ArrayList<>();

        for (org.jsoup.nodes.Element table : doc.select("table")) {
            for (org.jsoup.nodes.Element row : table.select("tr")) {
                Elements tds = row.select("td");
                willBeSet.add(tds.get(0).text());
                willBeSet.add(tds.get(1).text());
                willBeSet.add(tds.get(5).text());
                willBeSet.add(tds.get(4).text());
                willBeSet.add(tds.get(6).text());
                willBeSet.add(tds.get(7).text());

            }
        }

        myList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        myList.setLayoutManager(layoutManager);
        ScheduleHandler myHandler;
        myHandler = new ScheduleHandler(doc);

        willBeSet.clear();
        willBeSet = myHandler.getClassList();

        Layout_Row adapter = new Layout_Row(willBeSet, doc, this);
        myList.setAdapter(adapter);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        // get menu from navigationView
        Menu menu = navigationView.getMenu();

        String putIn = "";
        if(Schedule.getDate(1, getApplicationContext()).contains("erscheint"))
            putIn = "Nicht verfügbar.";
        else
            putIn = Schedule.getDate(1, getApplicationContext());

        // find MenuItem you want to change
        MenuItem nav_camara = menu.findItem(R.id.nav_heute);
        nav_camara.setTitle(putIn);

        if(Schedule.getDate(2, getApplicationContext()).contains("erscheint"))
            putIn = "Nicht verfügbar.";
        else
            putIn = Schedule.getDate(2, getApplicationContext());

        MenuItem nav_gallery = menu.findItem(R.id.nav_morgen);
        nav_gallery.setTitle(putIn);

        // add NavigationItemSelectedListener to check the navigation clicks
        navigationView.setNavigationItemSelectedListener(this);

        setTitle(Schedule.getDate(index, getApplicationContext()));

        SharedPreferences pref = getApplicationContext().getSharedPreferences("Tralala", MODE_PRIVATE);

        //Header set username, just some cosmetic stuff
        NavigationView menu2 = (NavigationView) findViewById(R.id.nav_view);
        View mHeaderView = menu2.getHeaderView(0);
        TextView username_view = (TextView) mHeaderView.findViewById(R.id.textView_username);
        username_view.setText(pref.getString("set_username", "[Nutzername]"));
    }

    @Override
    public void onRefresh() {
        SwipeRefresh.animate();
        SwipeRefresh.setRefreshing(true);
        int OneOrTwo = 1;

        if (TodaySelected)  //so that the right page will be updated, before when swiping down only the first page was being loaded
            OneOrTwo = 1;
        else
            OneOrTwo = 2;

        SharedPreferences preferences = getSharedPreferences("Tralala", MODE_PRIVATE);

        try {
            Schedule.refresh(getApplicationContext());
            SwipeRefresh.setRefreshing(false);
        } catch (IOException e) {
            SwipeRefresh.setRefreshing(false);
            if (e.getMessage() == "HTTP error fetching URL") {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Keine Verbindung möglich.", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        this.setText(Schedule.getSchedule(OneOrTwo, getApplicationContext()), OneOrTwo);
    }

    /**
     * Shows the progress UI and hides the login form.
     * AUTOGENERATED
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}