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

    SwipeRefreshLayout SwipeRefresh;
    RecyclerView myList;

    MenuItem menu_today;

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
            this.setText(Schedule.getSchedule(1, getApplicationContext()), 1);
            TodaySelected = true;

        } else if (id == R.id.nav_morgen) {
            this.setText(Schedule.getSchedule(2, getApplicationContext()), 2);
            TodaySelected = false;

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

    private void setText(Document doc, int index) {
        ArrayList<String> willBeSet = new ArrayList<>();

        ArrayList<ArrayList<String>> listInList = new ArrayList<>();

        myList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        myList.setLayoutManager(layoutManager);

        ScheduleHandler myHandler = new ScheduleHandler(doc);
        willBeSet = myHandler.getClassList();

        for (int i = 0; i < willBeSet.size(); i++) {
            listInList.add(myHandler.getClassInfo(willBeSet.get(i)));
        }

        Layout_Row adapter = new Layout_Row(willBeSet, listInList, this);
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
}