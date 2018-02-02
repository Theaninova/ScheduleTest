package com.wieland.www.scheduletest.schedule;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;

import com.wieland.www.scheduletest.activities.SettingsActivity;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Wieland on 29.03.2017.
 */

public class ScheduleHandler {
    DatabaseHelper databaseHelper;
    private int index;
    Context context;
    private ArrayList<String> myList = new ArrayList<>();
    boolean teacher;

    public ScheduleHandler(int index, Context context) {
        databaseHelper = new DatabaseHelper(context);
        this.index = index;
        this.context = context;
        teacher = false;
    }

    public ScheduleHandler(int index, Context context, boolean teacher) {
        databaseHelper = new DatabaseHelper(context);
        this.index = index;
        this.context = context;
        this.teacher = teacher;
    }

    /**
     * @return a list of all available classes
     */
    public ArrayList<String> getClassList() {

        ArrayList<String> outputList = new ArrayList<>();  //this is the List which will be put out. at the end it will contain all Classes that are appearing in the schedule

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor res;

        res = db.rawQuery("SELECT " + databaseHelper.COL_1 + " FROM " + databaseHelper.TABLE_NAME + index + " GROUP BY " + databaseHelper.COL_1, null);

        try {
            while (res.moveToNext()) {
                outputList.add(res.getString(0));
            }
        } finally {
            res.close();
        }

        return outputList;
    }

    public ArrayList<String> getBySQL(String SQL) {
        ArrayList<String> output = new ArrayList<>();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor res = db.rawQuery(SQL, null);
        try {
            while (res.moveToNext()) {
                int i = 0;
                while (true) {
                    try {
                        output.add(res.getString(i));
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }
            }
        } finally {
            res.close();
        }
        return output;
    }

    public ArrayList<String> getClassListCustom() throws Exception {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor res;
        SharedPreferences pref = context.getSharedPreferences("Tralala", MODE_PRIVATE);
        ArrayList<String> outputList = new ArrayList<>();

        res = db.rawQuery("SELECT kl FROM " + databaseHelper.TABLE_NAME + index  + " WHERE " + pref.getString(SettingsActivity.CUSTOMSQL_NAME, "") + " GROUP BY " + databaseHelper.COL_1, null);

        while (res.moveToNext()) {
            outputList.add(res.getString(0));
        }
        res.close();

        return outputList;
    }

    public ArrayList<android.text.Spanned> getClassInfoCustom(String thisClass) throws Exception {
        SharedPreferences pref = context.getSharedPreferences("Tralala", MODE_PRIVATE);

        return getClassInfoForSQL("SELECT * FROM " + databaseHelper.TABLE_NAME + index + " WHERE (" + pref.getString(SettingsActivity.CUSTOMSQL_NAME, "")+ ") and " + databaseHelper.COL_1 + " = '" + thisClass + "'");
    }

    public ArrayList<String> getClassListPersonalized(int id) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor res;
        SharedPreferences pref = context.getSharedPreferences("Tralala", MODE_PRIVATE);
        String coursesRaw = pref.getString(SettingsActivity.CLASSES_NAME, "");
        ArrayList<String> outputList = new ArrayList<>();
        if (coursesRaw == "")
            return outputList;

        ArrayList<String> extraArguments = new ArrayList<>();
        String buffer = "";

        for(int i = 0; i < coursesRaw.length(); i++) {
            if ((coursesRaw.charAt(i) == ';') || (i == (coursesRaw.length() - 1))) {
                if (i == (coursesRaw.length() - 1))
                    buffer = buffer + coursesRaw.charAt(i);
                extraArguments.add(databaseHelper.COL_1 + " = '" + buffer.trim() + "' COLLATE NOCASE");
                buffer = "";
            } else {
                buffer = buffer + coursesRaw.charAt(i);
            }
        }

        String extraArgumentsSQL = "";
        if (extraArguments.size() != 0)
            extraArgumentsSQL = " WHERE ";

        for(int i = 0; i < extraArguments.size(); i++) {
            extraArgumentsSQL = extraArgumentsSQL + extraArguments.get(i) + " COLLATE NOCASE";
            if(i < (extraArguments.size() - 1))
                extraArgumentsSQL = extraArgumentsSQL + " or ";
        }

        res = db.rawQuery("SELECT kl FROM " + databaseHelper.TABLE_NAME + index  + extraArgumentsSQL + " GROUP BY " + databaseHelper.COL_1, null);

        try {
            while (res.moveToNext()) {
                outputList.add(res.getString(0));
            }
        } finally {
            res.close();
        }

        return outputList;
    }

    /**
     * The basic idea of this method is that you have go a String in SharedPreferences. It will look like this: "gen 1;gku 1;Fr 2" The ; seperates them from each other. So this method returns
     * a ArrayList of Spannable Strings with the Class you want and in addition only the courses you want. When the user puts in all the Info, first he will be asked to put in all the classes
     * which potentially could fit the rules
     * @param thisClass
     * @return
     */
    public ArrayList<android.text.Spanned> getClassInfoPersonalized(String thisClass) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor res;
        SharedPreferences pref = context.getSharedPreferences("Tralala", MODE_PRIVATE);
        String coursesRaw = pref.getString("Courses", "");

        /*if (coursesRaw == "")
            return new ArrayList<>();*/

        ArrayList<String> extraArguments = new ArrayList<>();
        String buffer = "";

        boolean moreThanZero = false;

        if (coursesRaw != "" && coursesRaw.length() > 0) {
            for (int i = 0; i < coursesRaw.length(); i++) {
                if ((coursesRaw.charAt(i) == ';') || (i == (coursesRaw.length() - 1))) {
                    if (i == (coursesRaw.length() - 1))
                        buffer = buffer + coursesRaw.charAt(i);
                    extraArguments.add(databaseHelper.COL_3 + " = '" + buffer.trim() + "' COLLATE NOCASE or " + databaseHelper.COL_6 + " = '" + buffer.trim() + "' COLLATE NOCASE");
                    buffer = "";
                } else {
                    buffer = buffer + coursesRaw.charAt(i);
                }
            }
            moreThanZero = true;
        } else {
            moreThanZero = false;
        }

        String extraArgumentsSQL = "";

        if (moreThanZero) {
            if (extraArguments.size() > 0)
                if (extraArguments != null) {
                    extraArgumentsSQL = " and (";
                    moreThanZero = true;
                }
                else
                    moreThanZero = false;
            else
                moreThanZero = false;
        }

        for(int i = 0; i < extraArguments.size(); i++) {
            extraArgumentsSQL = extraArgumentsSQL + extraArguments.get(i);
            if(i < (extraArguments.size() - 1))
                extraArgumentsSQL = extraArgumentsSQL + " or ";
        }

        if(moreThanZero) {
            return getClassInfoForSQL("SELECT * FROM " + databaseHelper.TABLE_NAME + index + " WHERE " + databaseHelper.COL_1 + " = '" + thisClass + "'" + extraArgumentsSQL + ")");
        } else {
            return getClassInfoForSQL("SELECT * FROM " + databaseHelper.TABLE_NAME + index + " WHERE " + databaseHelper.COL_1 + " = '" + thisClass + "'"/* + extraArgumentsSQL*/);
        }
    }

    public ArrayList<android.text.Spanned> getClassInfo(String thisClass) {
        return getClassInfoForSQL("SELECT * FROM " + databaseHelper.TABLE_NAME + index + " WHERE " + databaseHelper.COL_1 + " = '" + thisClass + "'");
    }

    /**
     * @param
     * @return a single String with all the information from a specific row in the table
     */
    public ArrayList<android.text.Spanned> getClassInfoForSQL(String sql) {
        boolean forInfo = true;

        Cursor res;
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        if (index == 1)
            res = db.rawQuery(sql, null);
        else
            res = db.rawQuery(sql, null);

        ArrayList<android.text.Spanned> outList = new ArrayList<>();

        String currentLesson = "x";

        try {
            while (res.moveToNext()) {
                forInfo = true;
                String output = "";

                if (res.getString(2).contains(currentLesson))
                    output = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
                else if (currentLesson.contains("10"))
                    output = res.getString(2) + ".&nbsp;";
                else
                    output = res.getString(2) + ".&nbsp;&nbsp;";

                if (res.getString(6).contains("\u00A0")) {
                    if (res.getString(3).contains("\u00A0"))
                        output = output + getColoredSpanned("[Fach]", "grey");
                    else
                        output = output + getColoredSpanned(NameShortcuts.getRealClass(res.getString(3)), "#008000");
                } else {
                    output = output + getColoredSpanned(NameShortcuts.getRealClass(res.getString(6)), "#8B0000");
                }

                if (res.getString(5).contains("*Frei")) {
                    output = output + " " + getColoredSpanned("entfällt", "#8B0000");
                    forInfo = false;
                } else if (res.getString(5).contains("Raumänderung")) {
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
                        output = output + getColoredSpanned(NameShortcuts.getRealName(res.getString(5)), "#8B0000");
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
                    if (res.getString(2).contains(currentLesson))
                        output = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + getColoredSpanned(NameShortcuts.getRealClass(res.getString(3)), "#008000") + " wird " + getColoredSpanned(res.getString(8), "#8B0000");
                    else if (currentLesson.contains("10"))
                        output = res.getString(2) + ".&nbsp;" + getColoredSpanned(NameShortcuts.getRealClass(res.getString(3)), "#008000") + " wird " + getColoredSpanned(res.getString(8), "#8B0000");
                    else
                        output = res.getString(2) + ".&nbsp;&nbsp;" + getColoredSpanned(NameShortcuts.getRealClass(res.getString(3)), "#008000") + " wird " + getColoredSpanned(res.getString(8), "#8B0000");  //[Fach] wird [verschoben auf Datum]
                } else if (res.getString(8).contains("anstatt")) {
                    output = output + " " + res.getString(8);
                } else if (res.getString(8).contains("Aufg. erteilt")) {
                    output = output + "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + getColoredSpanned("Aufgaben erteilt", "grey");
                } else if (res.getString(8).contains("Aufg. für zu Hause erteilt")) {
                    output = output + "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + getColoredSpanned("Aufgaben für Zuhause erteilt", "grey");
                } else if (res.getString(8).contains("Aufg. für Stillarbeit erteilt")) {
                    output = output + "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + getColoredSpanned("Aufgaben für Stillarbeit erteilt", "grey");
                    //} else if (myList.get(six).contains("ganze Klasse")) {
                } else if (!res.getString(8).contains("\u00A0")) {
                    output = output + "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + getColoredSpanned(res.getString(8), "grey");
                }

                if (!res.getString(2).contains("&nbsp;"))
                    currentLesson = res.getString(2);

                outList.add(Html.fromHtml(output));
            }
        } finally {
            res.close();
        }

        return outList;
    }

    private String getColoredSpanned(String text, String color) {
        SharedPreferences pref = context.getSharedPreferences("Tralala", MODE_PRIVATE);
        String input;
        if (pref.getBoolean(SettingsActivity.COLORS_ENABLED, true))
            input = "<font color=" + color + ">" + text + "</font>";
        else
            input = text;
        return input;
    }
}
