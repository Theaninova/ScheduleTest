package com.wieland.www.scheduletest;

import android.text.Html;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by Wieland on 29.03.2017.
 */

public class ScheduleHandler {
    Document doc;
    private ArrayList<String> myList = new ArrayList<>();
    private int linePosition;

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
                myList.add(tds.get(2).text());
                myList.add(tds.get(3).text());
                myList.add(tds.get(4).text());
                myList.add(tds.get(5).text());
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
        myList.clear();

        ArrayList<String> outputList = new ArrayList<>();

        if(!myList.isEmpty()) {
            while (myList.get(0) != thisClass) {
                if (!(myList.size() == 1))
                    myList.remove(0);
                else {
                    myList.add("&null&");
                    break;
                }
            }
        }
        else
            outputList.add("&null&");

        if(!outputList.isEmpty()) {
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
        }

        return outputList;
    }

    /**
     * @return a list of all available classes
     */
    public ArrayList<String> getClassList() {

        ArrayList<String> outputList = new ArrayList<>();  //this is the List which will be put out. at the end it will contain all Classes that are appearing in the schedule

        String tmpClass = "";

        for (org.jsoup.nodes.Element table : this.doc.select("table")) {
            for (org.jsoup.nodes.Element row : table.select("tr")) {
                Elements tds = row.select("td");

                if (!(tds.get(0).text().equals("Kl.") || tds.get(0).text().equals("\u00a0") || tds.get(0).text().equals(tmpClass))) {   //checking for irrelevant data (such as KL., which appears at the top, and \u00a0, which stands for an empty field
                    outputList.add(tds.get(0).text()); //the text will be added to a list
                    tmpClass = tds.get(0).text(); //so no class will be returned twice
                }
            }
        }

        return outputList;
    }

    public String getLine(int linePosition, String thisClass) {
        myList.clear();

        boolean take = false;
        boolean inClass = false;

        for (org.jsoup.nodes.Element table : doc.select("table")) {
            for (org.jsoup.nodes.Element row : table.select("tr")) {
                Elements tds = row.select("td");
                if(tds.get(0).text().equals(thisClass)) {
                    take = true;
                    inClass = true;
                } else if(tds.get(0).text().equals("\u00a0") && inClass) {
                    take = true;
                } else {
                    take = false;
                    inClass = false;
                }

                if(take) {
                    for(int i = 1; i <= 7; i++) { //TODO: implement the info
                        if (tds.get(i).text().equals("\u00a0")) {
                            myList.add("null");
                        } else {
                            myList.add(tds.get(i).text());
                        }
                    }
                }
            }
        }

        if (myList.isEmpty())
            return null;

        for (int i = 0; i < linePosition * 7; i++) {
            myList.remove(0);
        }

        if(myList.size() < 6)
            return null;

        String output = "";
        if(myList.get(0).equals("null"))
            output = "\u0009\u0009\u0009";
        else if(myList.get(0).contains("10"))
            output = myList.get(0) + ".\u0009";
        else
            output = myList.get(0) + ".\u0009\u0009";

        if (myList.get(6).contains("ganze Klasse")) {
            output = output + "Ganze Klasse ";
        }

        if (myList.get(4) == "null") {
            if(myList.get(1).equals("null"))
                output = output + "[Fach]";
            else
                output = output + myList.get(1);
        } else {
            output = output + myList.get(4);
        }

        if (myList.get(3).contains("*Frei")) {
            output = output + " entfällt";
            return output;
        } else if (myList.get(3).contains("Raum�nderung")) {
            output = output + ": Raumänderung in Raum " + myList.get(5);
            return output;
        } else if (myList.get(3).contains("*Stillarbeit")) {
            //if (myList.get(3) == "null")  //TODO: Stillarbeit Teacher
                output = output + ": Stillarbeit in Raum";
            return output;
        }


        output = output + " bei ";

        if (myList.get(3) == "null") {
            output = output + "[Lehrer]";
        } else {
            output = output + getColoredSpanned(myList.get(3), "ff0000");
        }



        if (myList.get(5) == "null") {
            if(myList.get(2).equals("null"))
                output = output + " in [Raum]";
            else {
                output = output + " in Raum ";
                output = output + myList.get(2);
            }
        } else {
            output = output + " in Raum ";
            output = output + getColoredSpanned(myList.get(5), "ff0000");
        }

        if (myList.get(6).contains("verschoben")) {
            output = myList.get(0) + ".\u0009\u0009" + myList.get(1) + " wird " + myList.get(6);  //[Fach] wird [verschoben auf Datum]
        } else if (myList.get(6).contains("anstatt")) {
            output = output + " " + myList.get(6);
        } else if (myList.get(6).contains("Aufg. erteilt")) {
            output = output + "; Aufgaben erteilt";
        } else if (myList.get(6).contains("Aufg. f�r zu Hause erteilt")) {
            output = output + "; Aufgaben für Zuhause erteilt";
        } else if (myList.get(6).contains("Aufg. f�r Stillarbeit erteilt")) {
            output = output + "; Aufgaben für Stillarbeit erteilt";
        } else if (myList.get(6).contains("ganze Klasse")) {
        } else if (myList.get(6) != "null") {
            output = output + "; " + myList.get(6);
        }



        return output;
    }

    private String getColoredSpanned(String text, String color) {  //TODO: colored text
        //String input = "<font color=" + color + ">" + text + "</font>";
        return text;
    }
}
