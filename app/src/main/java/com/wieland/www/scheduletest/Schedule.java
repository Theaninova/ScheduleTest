package com.wieland.www.scheduletest;

/**
 * Created by Wieland on 24.03.2017.
 * in this class the Schedule is being downloaded
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;

import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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

        URL = "http://www.romain-rolland-gymnasium.eu/schuelerbereich/svplaneinseitig/V_TK_002_1.html";

        doc = Jsoup
                .connect(URL) //using a custom method to return a URL, there is no need for using two different methods which are doing essentially the same
                .header("Authorization", "Basic " + login) //just sort of standard layout for Basic http login
                .userAgent("Android Phone") //may be obsolete, but removing this could end in problems in future updates of the website
                .get();

        editor.putString("Day2", doc.toString());

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
}