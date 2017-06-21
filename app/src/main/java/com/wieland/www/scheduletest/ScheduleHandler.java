package com.wieland.www.scheduletest;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by Wieland on 29.03.2017.
 */

public class ScheduleHandler {
    DatabaseHelper databaseHelper;
    private int index;
    private ArrayList<String> myList = new ArrayList<>();

    public ScheduleHandler(int index, Context context) {
        databaseHelper = new DatabaseHelper(context);
        this.index = index;
    }

    /**
     * @return a list of all available classes
     */
    public ArrayList<String> getClassList() {

        ArrayList<String> outputList = new ArrayList<>();  //this is the List which will be put out. at the end it will contain all Classes that are appearing in the schedule

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor res;
        if (index == 1)
            res = db.rawQuery("SELECT kl FROM " + databaseHelper.TABLE_NAME + " GROUP BY kl", null);
        else
            res = db.rawQuery("SELECT kl FROM " + databaseHelper.TABLE_NAME2 + " GROUP BY kl", null);
        while (res.moveToNext()) {
            outputList.add(res.getString(0));
        }

        return outputList;
    }

    public ArrayList<String> getCustomizedClassInfo(String sqlcode) throws Exception {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor res;
        res = db.rawQuery(sqlcode, null);
        ArrayList<String> output = new ArrayList<>();

        while (res.moveToNext()) {
            String out = "";
            for (int i = 0; i < 8; i++)
                out = out + " | " + res.getString(i);
            output.add(out);
        }
        return output;
    }

    /**
     * @param thisClass needed to figure out weather the line specified up is still relevant for the class
     * @return a single String with all the information from a specific row in the table
     */
    public ArrayList<android.text.Spanned> getClassInfo(String thisClass) {
        myList.clear();

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor res;
        if (index == 1)
            res = db.rawQuery("SELECT * FROM " + databaseHelper.TABLE_NAME + " WHERE " + databaseHelper.COL_1 + " = '" + thisClass + "'", null);
        else
            res = db.rawQuery("SELECT * FROM " + databaseHelper.TABLE_NAME2 + " WHERE " + databaseHelper.COL_1 + " = '" + thisClass + "'", null);

        boolean forInfo = true;

        //if (myList.isEmpty())
        //    return null;

        int linePositon = 0;

        ArrayList<android.text.Spanned> outList = new ArrayList<>();

        while (res.moveToNext()) {
            forInfo = true;
            String output = "";

            if (res.getString(2).contains("\u00A0"))
                output = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
            else if (res.getString(2).contains("10"))
                output = res.getString(2) + ".&nbsp;";
            else
                output = res.getString(2) + ".&nbsp;&nbsp;";


            if (res.getString(6).contains("\u00A0")) {
                if (res.getString(3).contains("\u00A0"))
                    output = output + getColoredSpanned("[Fach]", "grey");
                else
                    output = output + getColoredSpanned(res.getString(3), "#008000");
            } else {
                output = output + getColoredSpanned(res.getString(6), "#8B0000");
            }

            if (res.getString(5).contains("*Frei")) {
                output = output + " " + getColoredSpanned("entfällt", "8B0000");
                forInfo = false;
            } else if (res.getString(5).contains("Raum�nderung")) {
                output = output + ": Raumänderung in Raum " + getColoredSpanned(res.getString(7), "#8B0000");
                forInfo = false;
            } else if (res.getString(5).contains("*Stillarbeit")) {
                //if (myList.get(3) == "null")  //TODO: Stillarbeit Teacher
                if (res.getString(4).contains("\u00A0"))
                    output = output + ": " + getColoredSpanned("Stillarbeit", "#8B0000");
                else
                    output = output + ": " + getColoredSpanned("Stillarbeit", "#8B0000") + " in Raum " + getColoredSpanned(res.getString(4), "#008000");
                forInfo = false;
            }


            if (forInfo) {
                output = output + " bei ";

                if (res.getString(5).contains("\u00A0")) {
                    output = output + getColoredSpanned("[Lehrer]", "grey");
                } else {
                    output = output + getColoredSpanned(res.getString(5), "#8B0000");
                }


                if (res.getString(7).contains("\u00A0")) {
                    if (res.getString(4).contains("\u00A0"))
                        output = output + " in " + getColoredSpanned("[Raum]", "grey");
                    else {
                        output = output + " in Raum " + getColoredSpanned(res.getString(4), "#008000");
                    }
                } else {
                    output = output + " in Raum ";
                    output = output + getColoredSpanned(res.getString(7), "#8B0000");
                }
            }

            if (res.getString(8).contains("verschoben")) {
                if (res.getString(2).contains("\u00A0"))
                    output = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + getColoredSpanned(res.getString(3), "#008000") + " wird " + getColoredSpanned(res.getString(8), "#8B0000");
                else
                    output = res.getString(2) + ".&nbsp;&nbsp;" + getColoredSpanned(res.getString(3), "#008000") + " wird " + getColoredSpanned(res.getString(8), "#8B0000");  //[Fach] wird [verschoben auf Datum]
            } else if (res.getString(8).contains("anstatt")) {
                output = output + " " + res.getString(8);
            } else if (res.getString(8).contains("Aufg. erteilt")) {
                output = output + "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + getColoredSpanned("Aufgaben erteilt", "grey");
            } else if (res.getString(8).contains("Aufg. f�r zu Hause erteilt")) {
                output = output + "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + getColoredSpanned("Aufgaben für Zuhause erteilt", "grey");
            } else if (res.getString(8).contains("Aufg. f�r Stillarbeit erteilt")) {
                output = output + "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + getColoredSpanned("Aufgaben für Stillarbeit erteilt", "grey");
                //} else if (myList.get(six).contains("ganze Klasse")) {
            } else if (!res.getString(8).contains("\u00A0")) {
                output = output + "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + getColoredSpanned(res.getString(8), "grey");
            }

            outList.add(Html.fromHtml(output));
        }

        return outList;
    }

    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }
}
