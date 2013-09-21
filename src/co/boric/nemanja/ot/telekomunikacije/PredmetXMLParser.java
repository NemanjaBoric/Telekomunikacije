package co.boric.nemanja.ot.telekomunikacije;

import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Burgos
 * Date: 21.9.13.
 * Time: 01.06
 * To change this template use File | Settings | File Templates.
 */
public class PredmetXMLParser {


    private static final String ns = null;

    public List<Predmet> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List<Predmet> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Predmet> predmeti = new ArrayList<Predmet>();

        parser.require(XmlPullParser.START_TAG, ns, "predmeti");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("predmet")) {
                predmeti.add(readPredmet(parser));
            } else {
                skip(parser);
            }
        }
        return predmeti;
    }


    private Predmet readPredmet(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "predmet");
        String ime = null;
        String code = null;
        String kontakt = null;
        List<Predmet.Vest> vesti = null;
        List<Predmet.Preuzimanje> preuz = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("name")) {
                ime = readName(parser);
            } else if (name.equals("code")) {
                code = readCode(parser);
            } else if (name.equals("kontakt")) {
                kontakt = readKontakt(parser);
            } else if (name.equals("vesti")){
                vesti = readVesti(parser);
            } else if (name.equals("preuzimanja")){
                preuz = readPreuzimanja(parser);
            } else {
                skip(parser);
            }
        }
        return new Predmet(ime, code, kontakt, vesti, preuz);
    }


    private List<Predmet.Vest> readVesti(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Predmet.Vest> vesti = new ArrayList<Predmet.Vest>();

        parser.require(XmlPullParser.START_TAG, ns, "vesti");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("vest")) {
                vesti.add(readVest(parser));
            } else {
                skip(parser);
            }
        }
        return vesti;
    }

    private Predmet.Vest readVest(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "vest");
        String naslov = null;
        String desc = null;
        String data = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("naslov")) {
                naslov = readVestiNaslov(parser);
            } else if (name.equals("desc")) {
                desc = readVestiDesc(parser);
            } else if (name.equals("data")) {
                data = readVestiData(parser);
            } else {
                skip(parser);
            }
        }
        return new Predmet.Vest(naslov, desc, data);
    }

    private String readVestiData(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "data");
        String temp = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "data");
        return temp;
    }

    private String readVestiDesc(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "desc");
        String temp = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "desc");
        return temp;
    }

    private String readVestiNaslov(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "naslov");
        String temp = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "naslov");
        return temp;
    }

    // Processes title tags in the feed.
    private String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "name");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "name");
        return title;
    }

    private String readCode(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "code");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "code");
        return title;
    }

    private String readKontakt(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "kontakt");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "kontakt");
        return title;
    }


    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private List<Predmet.Preuzimanje> readPreuzimanja(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Predmet.Preuzimanje> Preuzimanja = new ArrayList<Predmet.Preuzimanje>();

        parser.require(XmlPullParser.START_TAG, ns, "preuzimanja");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("preuzimanje")) {
                Preuzimanja.add(readPreuzimanje(parser));
            } else {
                skip(parser);
            }
        }
        return Preuzimanja;
    }

    private Predmet.Preuzimanje readPreuzimanje(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "preuzimanje");
        String naslov = null;
        String desc = null;
        String url = null;
        String filename = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("naslov")) {
                naslov = readPreuzimanjaNaslov(parser);
            } else if (name.equals("desc")) {
                desc = readPreuzimanjaDesc(parser);
            } else if (name.equals("url")) {
                url = readPreuzimanjaUrl(parser);
            } else if (name.equals("filename")) {
                filename = readPreuzimanjaFilename(parser);
            } else {
                skip(parser);
            }
        }
        return new Predmet.Preuzimanje(naslov, desc, url, filename);
    }

    private String readPreuzimanjaUrl(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "url");
        String temp = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "url");
        return temp;
    }

    private String readPreuzimanjaFilename(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "filename");
        String temp = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "filename");
        return temp;
    }

    private String readPreuzimanjaDesc(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "desc");
        String temp = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "desc");
        return temp;
    }

    private String readPreuzimanjaNaslov(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "naslov");
        String temp = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "naslov");
        return temp;
    }


    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }


}