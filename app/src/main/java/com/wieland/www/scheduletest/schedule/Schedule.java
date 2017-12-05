package com.wieland.www.scheduletest.schedule;

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
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

//notice that Jsoup is a separate library which has to be put in the library folder in your Android Studio. You can find the libary at https://jsoup.org/download I used jsoup-1.10.2.jar core library

public class Schedule {
    public static final String IS_ACTIVE = "is_active";
    public static final String PAGES_COUNT = "pagesCount";

    /**
     * URLs are being gathered via the index.html for showing more (or less) than just two pages
     * @param login
     * @return
     * @throws IOException
     */
    private static ArrayList<String> getURLs(String login, Context context) throws IOException {
        String URL = "http://www.romain-rolland-gymnasium.eu/schuelerbereich/svplaneinseitig/Index.html";

        Document doc = Jsoup
                .connect(URL) //using a custom method to return a URL, there is no need for using two different methods which are doing essentially the same
                .header("Authorization", "Basic " + login) //just sort of standard layout for Basic http login
                .userAgent("Android Phone") //may be obsolete, but removing this could end in problems in future updates of the website
                .get();

        ArrayList<String> out = new ArrayList<>();

        String lastURL = "";

        for (org.jsoup.nodes.Element table : doc.select("table")) {
            for (org.jsoup.nodes.Element row : table.select("tr")) {
                Elements tds = row.select("td");

                if (!lastURL.equals(tds.get(0).select("a").first().absUrl("href"))) {
                    out.add(tds.get(0).select("a").first().absUrl("href"));
                    System.out.println(tds.get(0).select("a").first().absUrl("href"));
                }

                lastURL = tds.get(0).select("a").first().absUrl("href");
            }
            break;
        }
        String date = doc.select("h1").text();
        context.getSharedPreferences("Tralala", MODE_PRIVATE).edit().putString("UpdateDate", date).commit();

        return out;
    }

    public static void refresh(Context context) throws IOException {

        SharedPreferences pref = context.getSharedPreferences("Tralala", MODE_PRIVATE);
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(IS_ACTIVE, true);
        editor.commit();

        String username = pref.getString("set_username", "-1");
        String password = pref.getString("set_password", "-1");

        //without it it would produce this error: android.os.NetworkOnMainThreadException
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String tmp = username + ":" + password; //":" is needed, a basic html login requires a return in the form "Basic username:password", where the username:password are Base 64 encoded
        String login = new String(Base64.encodeBase64(tmp.getBytes())); //encoding "username:password" Base64

        ArrayList<String> URLs = getURLs(login, context);
        boolean success = false; //for checking

        int pagesCount = 0;

        context.deleteDatabase(DatabaseHelper.DATABASE_NAME); //clear database

        for (int i = 1; i <= URLs.size(); i++) {
            String url = URLs.get(i - 1);

            URL theUrl = new URL(url);
            URLConnection urlConnection = theUrl.openConnection();
            urlConnection.setRequestProperty("Authorization", "Basic " + login);
            InputStream is = urlConnection.getInputStream();

            /*Document fl = Jsoup
                    .connect(url) //using a custom method to return a URL, there is no need for using two different methods which are doing essentially the same
                    .header("Authorization", "Basic " + login) //just sort of standard layout for Basic http login
                    .userAgent("Android Phone") //may be obsolete, but removing this could end in problems in future updates of the website
                    .get();*/

            Document doc = Jsoup.parse(is, "ISO8859-1", url);
            //doc.charset(Charset.forName("Windows-1252"));

            System.out.println("refreshing...");

            //START SQL
            String currentClass = "Aufsicht";
            String currentLesson = "";
            for (org.jsoup.nodes.Element table : doc.select("table")) {
                for (org.jsoup.nodes.Element row : table.select("tr")) {
                    Elements tds = row.select("td");

                    if (!(tds.get(0).toString().contains("&nbsp;"))) { //All rows will need a class name, otherwise it will be much more difficult to parse the data
                        if (!tds.get(0).text().contains("Kl.")) {   //checking for irrelevant data (such as KL., which appears at the top.)
                            currentClass = tds.get(0).text();
                            if (tds.get(1).toString().contains("&nbsp;"))
                                success = databaseHelper.insertData(i, tds.get(0).text(), currentLesson, tds.get(2).text(), tds.get(3).text(), tds.get(4).text(), tds.get(5).text(), tds.get(6).text(), tds.get(7).text()); //inserting all the data into the SQL database
                            else {
                                success = databaseHelper.insertData(i, tds.get(0).text(), tds.get(1).text(), tds.get(2).text(), tds.get(3).text(), tds.get(4).text(), tds.get(5).text(), tds.get(6).text(), tds.get(7).text());
                                currentLesson = tds.get(1).text();
                            }
                        }
                    } else {
                        if (tds.get(1).toString().contains("&nbsp;"))
                            success = databaseHelper.insertData(i, currentClass, currentLesson, tds.get(2).text(), tds.get(3).text(), tds.get(4).text(), tds.get(5).text(), tds.get(6).text(), tds.get(7).text()); //inserting all the data into the SQL database
                        else {
                            success = databaseHelper.insertData(i, currentClass, tds.get(1).text(), tds.get(2).text(), tds.get(3).text(), tds.get(4).text(), tds.get(5).text(), tds.get(6).text(), tds.get(7).text());
                            currentLesson = tds.get(1).text();
                        }
                    }
                }
            }
            //END SQL

            //SAVING DATES
            String out;
            out = doc.select("h2").text();
            editor.putString("Day" + i + "_Date", out);

            out = doc.select("h1").text();
            editor.putString("Day" + i + "_UpdateDate", out);
            //END SAVING DATES

            pagesCount = i;
        }
        editor.putInt(PAGES_COUNT, pagesCount);

        editor.putBoolean(IS_ACTIVE, false);
        editor.commit();
    }

    public static String getDate(int index, Context context) {
        SharedPreferences pref = context.getSharedPreferences("Tralala", MODE_PRIVATE);
        return pref.getString("Day" + index + "_Date", "NOT_EXISTING");
    }

    public static String getUpdateDate(Context context) {
        SharedPreferences pref = context.getSharedPreferences("Tralala", MODE_PRIVATE);
        return pref.getString("UpdateDate", "NOT_EXISTING");

    }
}