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
                return "Herrn Mittag";
            case "Otti":
                return "Herrn Ottink";
            case "MoßF":
                return "Frau Moß";
            case "Conr":
                return "Frau Conrad";
            case "Vökl":
                return "Frau Völker-Klatte";
            case "Häne":
                return "Herrn Hänel";
            case "vPar":
                return "Herrn von Paris";
            case "Wala":
                return "Frau Walachowitz";
            case "Beye":
                return "Herrn Beyerle";
            case "Gres":
                return "Frau Gresch";
            case "Gegu":
                return "Herrn Gegusch";
            case "Kühn":
                return "Frau Kühne";
            case "Jahn":
                return "Frau Jahn";
            case "Drew":
                return "Frau Drews";
            case "Siek":
                return "Herrn Siek";
            case "Bett":
                return "Herrn Bettencourt";
            case "Hüls":
                return "Herrn Hülsmann";
            case "Pösc":
                return "Herrn Pöschl";
            case "Toka":
                return "Frau Tokarik";
            case "Krab":
                return "Frau Krabbe";
            case "Fisc":
                return "Frau Fischer";
            case "Pill":
                return "Frau Pillin";
            case "Möbi":
                return "Herrn Möbius";
            case "Jehl":
                return "Herrn Jehle";
            case "Deut":
                return "Frau Deutschmann";
            case "Rex":
                return "Frau Rex";
            case "Wese":
                return "Herrn Weser";
            case "Bern":
                return "Frau Bernd";
            case "Kans":
                return "Herrn Kanstinger";
            case "Köni":
                return "Herrn König";
            case "MoßH":
                return "Herrn Moß";
            case "Brem":
                return "Herrn Bremert";
            case "Miko":
                return "Frau Mikoleiwski";
            case "Rapp":
                return "Herrn Rapp";
            case "Habe":
                return "Frau Habermann-Lange";
            case "Bahr":
                return "Frau Bahr";
            case "Lehn":
                return "Frau Lehne";

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
            case "Bi":
                return "Bio";

            default:
                return /*"[" + */fakeClass/* + "]"*/;
        }
    }
}
