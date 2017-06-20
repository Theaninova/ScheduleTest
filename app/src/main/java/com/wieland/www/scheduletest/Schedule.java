package com.wieland.www.scheduletest;

/**
 * Created by Wieland on 24.03.2017.
 * in this class the Schedule is being downloaded
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.widget.Toast;

import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

//notice that Jsoup is a separate library which has to be put in the library folder in your Android Studio. You can find the libary at https://jsoup.org/download I used jsoup-1.10.2.jar core library

public class Schedule {
    /**
     * @param index 1 means Today and 2 means Tomorrow
     * @return returns the Schedule at index as a Jsoup Document
     * @throws IOException
     */
    public static Document getSchedule(int index, Context context) {
        SharedPreferences pref = context.getSharedPreferences("Tralala", MODE_PRIVATE);

        Document doc;

        if (index == 1)
            doc = Jsoup.parse(pref.getString("Day1", ""));
        else
            doc = Jsoup.parse(pref.getString("Day2", ""));

        return doc;
    }

    public static void refresh(Context context) throws IOException {

        SharedPreferences pref = context.getSharedPreferences("Tralala", MODE_PRIVATE);
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SharedPreferences.Editor editor = pref.edit();

        String username = pref.getString("set_username", "-1");
        String password = pref.getString("set_password", "-1");

        //without it it would produce this error: android.os.NetworkOnMainThreadException
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String tmp = username + ":" + password; //":" is needed, a basic html login requires a return in the form "Basic username:password", where the username:password are Base 64 encoded
        String login = new String(Base64.encodeBase64(tmp.getBytes())); //encoding "username:password" Base64

        String URL = "http://www.romain-rolland-gymnasium.eu/schuelerbereich/svplaneinseitig/V_TK_001_1.html";

        Document doc = Jsoup
                .connect(URL) //using a custom method to return a URL, there is no need for using two different methods which are doing essentially the same
                .header("Authorization", "Basic " + login) //just sort of standard layout for Basic http login
                .userAgent("Android Phone") //may be obsolete, but removing this could end in problems in future updates of the website
                .get();

        editor.putString("Day1", doc.toString());

        //START SQL
        databaseHelper.getWritableDatabase().delete(DatabaseHelper.TABLE_NAME, null, null); //clear database
        boolean success = false; //for checking
        String currentClass = "Aufsicht";
        for (org.jsoup.nodes.Element table : doc.select("table")) {
            for (org.jsoup.nodes.Element row : table.select("tr")) {
                Elements tds = row.select("td");

                if (!(tds.get(0).toString().contains("&nbsp;"))) { //All rows will need a class name, otherwise it will be much more difficult to parse the data
                    if (!tds.get(0).text().contains("Kl.")) {   //checking for irrelevant data (such as KL., which appears at the top.)
                        currentClass = tds.get(0).text();
                        success = databaseHelper.insertData(1, tds.get(0).text(), tds.get(1).text(), tds.get(2).text(), tds.get(3).text(), tds.get(4).text(), tds.get(5).text(), tds.get(6).text(), tds.get(7).text()); //inserting all the data into the SQL database
                    }
                } else {
                    success = databaseHelper.insertData(1, currentClass, tds.get(1).text(), tds.get(2).text(), tds.get(3).text(), tds.get(4).text(), tds.get(5).text(), tds.get(6).text(), tds.get(7).text()); //inserting all the data into the SQL database
                }
            }
        }
        //if(success) //for cheking, but causes a crash
        //    Toast.makeText(context, "succesful!", Toast.LENGTH_SHORT);
        //END SQL

        URL = "http://www.romain-rolland-gymnasium.eu/schuelerbereich/svplaneinseitig/V_TK_002_1.html";

        doc = Jsoup
                .connect(URL) //using a custom method to return a URL, there is no need for using two different methods which are doing essentially the same
                .header("Authorization", "Basic " + login) //just sort of standard layout for Basic http login
                .userAgent("Android Phone") //may be obsolete, but removing this could end in problems in future updates of the website
                .get();

        editor.putString("Day2", doc.toString());

        //START SQL
        databaseHelper.getWritableDatabase().delete(DatabaseHelper.TABLE_NAME2, null, null); //clear database
        success = false; //for checking
        currentClass = "Aufsicht";
        for (org.jsoup.nodes.Element table : doc.select("table")) {
            for (org.jsoup.nodes.Element row : table.select("tr")) {
                Elements tds = row.select("td");

                if (!(tds.get(0).toString().contains("&nbsp;"))) { //All rows will need a class name, otherwise it will be much more difficult to parse the data
                    if (!tds.get(0).text().contains("Kl.")) {   //checking for irrelevant data (such as KL., which appears at the top.)
                        currentClass = tds.get(0).text();
                        success = databaseHelper.insertData(2, tds.get(0).text(), tds.get(1).text(), tds.get(2).text(), tds.get(3).text(), tds.get(4).text(), tds.get(5).text(), tds.get(6).text(), tds.get(7).text()); //inserting all the data into the SQL database
                    }
                } else {
                    success = databaseHelper.insertData(2, currentClass, tds.get(1).text(), tds.get(2).text(), tds.get(3).text(), tds.get(4).text(), tds.get(5).text(), tds.get(6).text(), tds.get(7).text()); //inserting all the data into the SQL database
                }
            }
        }
        //if(success) //for cheking, but causes a crash
        //    Toast.makeText(context, "succesful!", Toast.LENGTH_SHORT);
        //END SQL

        editor.commit();
    }

    public static String getDate(int index, Context context) {
        SharedPreferences pref = context.getSharedPreferences("Tralala", MODE_PRIVATE);

        String out;
        Document doc;

        if (index == 1)
            doc = Jsoup.parse(pref.getString("Day1", ""));
        else
            doc = Jsoup.parse(pref.getString("Day2", ""));

        out = doc.select("h2").text();

        return out;
    }

    public static String getUpdateDate(int index, Context context) {
        SharedPreferences pref = context.getSharedPreferences("Tralala", MODE_PRIVATE);

        String out;
        Document doc;

        if (index == 1)
            doc = Jsoup.parse(pref.getString("Day1", ""));
        else
            doc = Jsoup.parse(pref.getString("Day2", ""));

        out = doc.select("h1").text();

        return out;
    }
}