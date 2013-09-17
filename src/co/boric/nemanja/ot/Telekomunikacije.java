package co.boric.nemanja.ot;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import co.boric.nemanja.ot.R;
import co.boric.nemanja.ot.Telekomunikacije.Predmet.Preuzimanje;
import co.boric.nemanja.ot.Telekomunikacije.Predmet.Vest;


import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.util.Xml;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;



public class Telekomunikacije extends Activity {
	private ArrayList<Button> toolbarButtons = new ArrayList<Button>(10);
	Novost weather_data[];
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Telekomunikacije");
        setContentView(R.layout.activity_naslovna);
        
        
        //todo prikazi listu za pretplatu!
        DodajTabove();
        new DownloadData(this, this).execute("http://telekomunikacije.etf.rs/zaposleni/data.xml");
        
    }

    private void UcitajSadrzaj() {
		UcitajObavestenja();
		UcitajPreuzimanja();
		UcitajKontakt();
	}
    String caption = "";
    private void UcitajPreuzimanja() {
    	

    	List<Preuzimanje> p = predmeti.get(prIndex).preuzimanja;
    	Preuzimanje [] downloads = p.toArray(new Preuzimanje[p.size()]); ;
        
		DownloadsAdapter adapter = new DownloadsAdapter(this, R.layout.novost, downloads);
		ListView lstDownloads = (ListView)findViewById(R.id.lstDownloads);
         
		lstDownloads.setOnItemClickListener(downloadListener);
		lstDownloads.setAdapter(adapter);
		
	}
        

	@TargetApi(11)
	private void UcitajKontakt() {
		WebView web = (WebView)findViewById(R.id.webContact);
		
        web.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView wView, String url)
            {
            	//if( url.startsWith("http:") || url.startsWith("https:") ) {
                //    return false;
                //}

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity( intent ); 
                return true;

            }
        });
        
        web.setBackgroundColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= 11){
        	 web.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        web.loadDataWithBaseURL(null, predmeti.get(prIndex).kontakt, "text/html", "utf-8", null);
		web.setBackgroundColor(Color.TRANSPARENT);
	
	}
	
	int prIndex = 0;
	
	private void UcitajObavestenja() {
		List<Vest> v = predmeti.get(prIndex).vesti;
        Predmet.Vest [] vesti = v.toArray(new Vest[v.size()]); ;
        
        NovostiAdapter adapter = new NovostiAdapter(this, 
                R.layout.novost, vesti);
        
        
        ListView lstNovosti = (ListView)findViewById(R.id.lstNews);
         
        lstNovosti.setOnItemClickListener(newsListener);
        lstNovosti.setAdapter(adapter);
        
		
	}

	/**
	 * @param context used to check the device version and DownloadManager information
	 * @return true if the download manager is available
	 */
	private static boolean isDownloadManagerAvailable(Context context) {
	    try {
	        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
	            return false;
	        }
	        Intent intent = new Intent(Intent.ACTION_MAIN);
	        intent.addCategory(Intent.CATEGORY_LAUNCHER);
	        intent.setClassName("com.android.providers.downloads.ui", "com.android.providers.downloads.ui.DownloadList");
	        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
	                PackageManager.MATCH_DEFAULT_ONLY);
	        return list.size() > 0;
	    } catch (Exception e) {
	        return false;
	    }
	}
	
	@TargetApi(11)
	private void DownloadFile(String url, String filename, String title, String desc)
	{
		if(isDownloadManagerAvailable(getApplicationContext())){
			DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
			request.setDescription(desc);
			request.setTitle(title);
			// in order for this if to run, you must use the android 3.2 to compile your app
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			    request.allowScanningByMediaScanner();
			    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
			}
			request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
	
			// get download service and enqueue file
			DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
			manager.enqueue(request);
		}
	}
	

	
	OnItemClickListener newsListener = new OnItemClickListener (){
		  public void onItemClick(AdapterView<?> parent, View view, int position, long id){
			  NovostiAdapter.NovostiHolder hld = (NovostiAdapter.NovostiHolder)view.getTag();
			  if(hld.data.trim().length() != 0){
		             Intent html = new Intent(view.getContext(), HtmlViewer.class);
		             html.putExtra("data", hld.data.trim());
		             startActivity(html); 

			  }

		  }

		};

		OnItemClickListener downloadListener = new OnItemClickListener (){
			  public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				  DownloadsAdapter.DownloadHolder hld = (DownloadsAdapter.DownloadHolder)view.getTag();
				  DownloadFile(hld.url, hld.filename, hld.naslov.getText().toString(), hld.text.getText().toString());
			  }
			};
		
		
	private void DodajTabove() {
    	TabHost mTabHost = (TabHost)findViewById(R.id.tabhost);  
    	
    	mTabHost.setup(); // ovo mora, ako dinamicki dohvatamo tabhost kontrolu
        mTabHost.addTab(mTabHost.newTabSpec("tab_test1")
          .setIndicator("Obaveštenja", getResources().getDrawable( R.drawable.news))
          .setContent(R.id.tab1));
        mTabHost.addTab(mTabHost.newTabSpec("tab_test2")
          .setIndicator("Preuzimanja", getResources().getDrawable( R.drawable.downloads))
          .setContent(R.id.tab2));
        mTabHost.addTab(mTabHost.newTabSpec("tab_test3")
          .setIndicator("Kontakt", getResources().getDrawable( R.drawable.mail))
          .setContent(R.id.tab3));
       
        mTabHost.setCurrentTab(0);

		
	}

	private void DodajPredmete(LinearLayout toolbar)
    {
		int i = 0;
    	for(Predmet p: predmeti){
    		final Button btn = new Button(this);
    		btn.setBackgroundResource(R.drawable.btn_inactive);
    		btn.setText(p.code);
    		btn.setTag(i++);
    		toolbarButtons.add(btn);
    		btn.setOnClickListener(new OnClickListener(){
    			public void onClick(View v){
    				toolbar_clicked((Integer)btn.getTag());
    			}
    		});
    		toolbar.addView(btn, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    	}
    }
    
    
    
    
    
    
    protected void toolbar_clicked(int index) {
		toolbarButtons.get(index).setBackgroundResource(R.drawable.btn_active);
		prIndex = index;
		
		//for other, set inactive
		for(int i = 0; i != toolbarButtons.size(); i++)
		{
			if(i == index)
				continue;
			toolbarButtons.get(i).setBackgroundResource(R.drawable.btn_inactive);
		}
		
		UcitajSadrzaj();
		
	}

    List<Predmet> predmeti;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_naslovna, menu);
        return true;
    }

	public void loadData(String result) {
		//caption = result;
		if(result == "")
		{
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Greška");
			alertDialog.setMessage("Problem pri povezivanju sa internetom.\nProverite internet konekciju i pokušajte ponovo.");
			alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
			   public void onClick(DialogInterface dialog, int which) {
			      Telekomunikacije.this.finish();
			   }
			});
			alertDialog.show();
			return;
		}
		
		try {
			predmeti = parse(new ByteArrayInputStream(result.getBytes("UTF-8")));
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		LinearLayout toolbar = (LinearLayout)findViewById(R.id.predmeti);
        DodajPredmete(toolbar);
        toolbarButtons.get(0).performClick();
		
	}
	
	
	public static class Predmet {    
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

	private Vest readVest(XmlPullParser parser) throws XmlPullParserException, IOException {
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
