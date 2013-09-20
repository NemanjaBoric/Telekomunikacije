package co.boric.nemanja.ot;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Burgos
 * Date: 21.9.13.
 * Time: 01.04
 * To change this template use File | Settings | File Templates.
 */
public class Predmet {
    public static class Vest{
        public final String naslov;
        public final String desc;
        public final String data;
        public Vest(String naslov, String desc, String data)
        {
            this.naslov = naslov;
            this.desc = desc;
            this.data = data;
        }
    }

    public static class Preuzimanje
    {

        public final String naslov;
        public final String desc;
        public final String url;
        public final String filename;
        public Preuzimanje(String naslov, String desc, String url, String filename)
        {
            this.naslov = naslov;
            this.desc = desc;
            this.url = url;
            this.filename = filename;
        }
    }

    public final String name;
    public final String code;
    public final String kontakt;
    public final List<Vest> vesti;
    public final List<Preuzimanje> preuzimanja;


    public Predmet(String name, String code, String kontakt, List<Vest> vesti, List<Preuzimanje> preuzimanja)
    {
        this.name = name;
        this.code = code;
        this.kontakt = kontakt;
        this.vesti = vesti;
        this.preuzimanja = preuzimanja;
    }

}