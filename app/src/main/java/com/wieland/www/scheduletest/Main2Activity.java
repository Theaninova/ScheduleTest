package com.wieland.www.scheduletest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

    private boolean TodaySelected = true; //for Swipe to Refresh

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //MainText = (TextView) findViewById(R.id.textViewMain2);
        SwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        myList = (RecyclerView) findViewById(R.id.theListOfDoom);

        //MainText.setText("");

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
            this.setText(Schedule.getSchedule(1, preferences.getString("set_username", "-1"), preferences.getString("set_password", "-1")));
        } catch (IOException e) {
            //MainText.setText(e.getMessage());
            if (e.getMessage() == "HTTP error fetching URL") {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                alertDialogBuilder.setTitle("Fehler");
                alertDialogBuilder.setMessage("Keine Internetverbindung");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(0);
                    }
                })
                        .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }
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

        if (id == R.id.nav_camera) {
            //do {
            try {
                this.setText(Schedule.getSchedule(1, preferences.getString("set_username", "-1"), preferences.getString("set_password", "-1")));
                TodaySelected = true;
            } catch (IOException e) {
                //MainText.setText(e.getMessage());
                if (e.getMessage() == "HTTP error fetching URL") {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                    alertDialogBuilder.setTitle("Fehler");
                    alertDialogBuilder.setMessage("Keine Internetverbindung");
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setPositiveButton("Erneut versuchen", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    })
                            .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
            //} while (willBeRepeated);
        } else if (id == R.id.nav_gallery) {
            try {
                this.setText(Schedule.getSchedule(2, preferences.getString("set_username", "default"), preferences.getString("set_password", "default")));
                TodaySelected = false;
            } catch (IOException e) {
                if (e.getMessage() == "HTTP error fetching URL") {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                    alertDialogBuilder.setTitle("Fehler");
                    alertDialogBuilder.setMessage("Keine Internetverbindung");
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            System.exit(0);
                        }
                    })
                            .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

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

    private void setText(Document doc) {
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
        //ArrayList u = new ArrayList();
        ScheduleHandler myHandler;
        myHandler = new ScheduleHandler(doc);

        willBeSet.clear();
        willBeSet = myHandler.getClassList();

        Layout_Row adapter = new Layout_Row(willBeSet, doc, this);
        myList.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {

        int OneOrTwo = 1;

        if (TodaySelected)  //so that the right page will be updated, before when swiping down only the first page was being loaded
            OneOrTwo = 1;
        else
            OneOrTwo = 2;

        SharedPreferences preferences = getSharedPreferences("Tralala", MODE_PRIVATE);

        try {
            this.setText(Schedule.getSchedule(OneOrTwo, preferences.getString("set_username", "-1"), preferences.getString("set_password", "-1")));
            SwipeRefresh.setRefreshing(false);
        } catch (IOException e) {
            SwipeRefresh.setRefreshing(false);
            //MainText.setText(e.getMessage());
            if (e.getMessage() == "HTTP error fetching URL") {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "keine Verbindung möglich", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}