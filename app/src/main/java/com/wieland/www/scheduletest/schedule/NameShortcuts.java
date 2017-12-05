package com.wieland.www.scheduletest.schedule;

/**
 * Created by wulka on 05.12.2017.
 */

public class NameShortcuts {
    public static String getRealName(String fakeName) {
        switch (fakeName) {
            case "Thei":
                return "Frau Theiss";
            case "Mitt":
                return "Herr Mittag";
            case "Otti":
                return "Herr Ottink";
            case "MoßF":
                return "Frau Moß";
            case "Conr":
                return "Frau Conrad";
            case "Völk":
                return "Frau Völker-Klatte";
            case "Häne":
                return "Herr Hänel";
            case "vPar":
                return "Herr von Paris";
            case "Wala":
                return "Frau Walachowitz";
            case "Beye":
                return "Herr Beyerle";
            case "Gres":
                return "Frau Gresch";
            case "Gegu":
                return "Herr Gegusch";
            case "Kühn":
                return "Frau Kühne";
            case "Jahn":
                return "Frau Jahn";
            case "Drew":
                return "Frau Drews";
            case "Siek":
                return "Herr Siek";
            case "Bett":
                return "Herr Bettencourt";
            case "Hüls":
                return "Herr Hülsmann";
            case "Pösc":
                return "Herr Pöschl";
            case "Toka":
                return "Frau Tokarik";
            case "Krab":
                return "Frau Krabbe";
            case "Fisc":
                return "Frau Fischer";
            case "Pill":
                return "Frau Pillin";
            case "Möbi":
                return "Herr Möbius";
            case "Jehl":
                return "Herr Jehle";
            case "Deut":
                return "Frau Deutschmann";
            case "Rex":
                return "Frau Rex";
            case "Wese":
                return "Herr Weser";
            case "Bern":
                return "Frau Bernd";
            case "Kans":
                return "Herr Kanstinger";
            case "Köni":
                return "Herr König";
            case "MoßH":
                return "Herr Moß";
            case "Brem":
                return "Herr Bremert";
            case "Miko":
                return "Frau Mikoleiwski";
            case "Rapp":
                return "Herr Rapp";
            case "Habe":
                return "Frau Habermann-Lange";


            default:
                return "[" + fakeName + "]";
        }
    }

    public static String getRealClass(String fakeClass) {
        switch (fakeClass) {
            case "Ma":
                return "Mathe";
            case "Mu":
                return "Musik";
            case "NaWi":
                return fakeClass;
            case "Ku":
                return "Kunst";
            case "Sp/m":
                return "Sport Jungen";
            case "Sp/w":
                return "Sport Mädchen";
            case "Eth":
                return "Ethik";
            case "Ge":
                return "Geschichte";
            case "Ev.R":
                return "Religion (Ev)";
            case "De":
                return "Deutsch";
            case "Ch":
                return "Chemie";
            case "Ek":
                return "Erdkunde";
            case "Ph":
                return "Physik";

            default:
                return /*"[" + */fakeClass/* + "]"*/;
        }
    }
}
