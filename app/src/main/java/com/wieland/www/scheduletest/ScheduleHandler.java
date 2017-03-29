package com.wieland.www.scheduletest;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by Wieland on 29.03.2017.
 */

public class ScheduleHandler {
    Document doc;
    private ArrayList<String> myList = new ArrayList<>();

    public ScheduleHandler(Document doc) {
        this.doc = doc;
    }

    /**
     * This will be useless in future
     *
     * @return a list with all the things are displayed in every row and column of the schedule
     */
    public ArrayList<String> getAll() {
        myList.clear();

        for (org.jsoup.nodes.Element table : doc.select("table")) {
            for (org.jsoup.nodes.Element row : table.select("tr")) {
                Elements tds = row.select("td");
                myList.add(tds.get(0).text());
                myList.add(tds.get(1).text());
                myList.add(tds.get(5).text());
                myList.add(tds.get(4).text());
                myList.add(tds.get(6).text());
                myList.add(tds.get(7).text());

            }
        }

        return myList;
    }


    /**
     * @param thisClass the name of the class, for example "07d". Notice that the "0" at the beginning is important. "7d" != "07d"
     * @return a list of every Item which is useful for a specific class
     */
    public ArrayList<String> getClass(String thisClass) {
        myList = this.getAll();

        ArrayList<String> outputList = new ArrayList<>();

        while (myList.get(0) != thisClass) {
            if (!myList.isEmpty())
                myList.remove(0);
            else
                myList.add("&null&");
            break;
        }

        if (!(outputList.get(0) == "&null&")) {
            while ((myList.get(0) == thisClass) ||
                    (myList.get(0) == "") ||
                    (myList.get(0) == " ") ||
                    (myList.get(0) == "Kl.")) {

                if (!myList.isEmpty()) {
                    if (!(myList.get(0) == "Kl.")) {
                        for (int i = 0; i <= 7; i++) {
                            outputList.add(myList.get(0));
                            myList.remove(0);
                        }
                    } else {
                        for (int i = 0; i <= 7; i++) {
                            myList.remove(0);
                        }
                    }
                } else {
                    break;
                }
            }
        }

        return outputList;
    }

    /**
     * @return a list of all available classes
     */
    public ArrayList<String> getClassList() {
        myList = this.getAll();

        ArrayList<String> outputList = new ArrayList<>();

        while (!myList.isEmpty()) {
            if ((myList.get(0) != "") || (myList.get(0) != " ") || (myList.get(0) != "Kl."))
                outputList.add(myList.get(0));
            myList.remove(0);
        }

        return outputList;
    }
}
