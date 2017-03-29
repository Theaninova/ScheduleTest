package com.wieland.www.scheduletest;

/**
 * Created by Wieland on 24.03.2017.
 * in this class the Schedule is being downloaded
 */

import android.os.StrictMode;

import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

//notice that Jsoup is a separate library which has to be put in the library folder in your Android Studio. You can find the libary at https://jsoup.org/download I used jsoup-1.10.2.jar core library

public class Schedule {
    /**
     * @param index 1 means Today and 2 means Tomorrow
     * @return returns the Schedule at index as a Jsoup Document
     * @throws IOException
     */
    public static Document getSchedule(int index, String username, String password) throws IOException {
        //without it it would produce this error: android.os.NetworkOnMainThreadException
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String URL = "http://www.romain-rolland-gymnasium.eu/schuelerbereich/svplaneinseitig/V_TK_00" + index + "_1.html";

        //Log in
        String tmp = username + ":" + password; //":" is needed, a basic html login requires a return in the form "Basic username:password", where the username:password are Base 64 encoded
        String login = new String(Base64.encodeBase64(tmp.getBytes())); //encoding "username:password" Base64

        //here a Jsoup method was used for making a Document out of a website, the real matter starts here
        Document doc = Jsoup
                .connect(URL) //using a custom method to return a URL, there is no need for using two different methods which are doing essentially the same
                .header("Authorization", "Basic " + login) //just sort of standard layout for Basic http login
                .userAgent("Android Phone") //may be obsolete, but removing this could end in problems in future updates of the website
                .get();

        return doc; //the website will be analyzed later by using Jsoup, which can directly put out the content of the Table table
    }
}