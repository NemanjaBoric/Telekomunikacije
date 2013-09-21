package co.boric.nemanja.ot;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.*;
import android.webkit.MimeTypeMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import co.boric.nemanja.ot.R;

import co.boric.nemanja.ot.Predmet;



import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;

import android.app.DownloadManager;
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
import android.content.Intent;


public class Telekomunikacije extends Activity {
	private ArrayList<Button> toolbarButtons = new ArrayList<Button>(10);
	Novost weather_data[];
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Telekomunikacije");
        setContentView(R.layout.activity_naslovna);

        manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        // Initialize download manager
        IntentFilter completeFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(onDownloadComplete, completeFilter);


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
    	

    	List<Predmet.Preuzimanje> p = predmeti.get(prIndex).preuzimanja;
    	Predmet.Preuzimanje [] downloads = p.toArray(new Predmet.Preuzimanje[p.size()]); ;
        
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
		List<Predmet.Vest> v = predmeti.get(prIndex).vesti;
        Predmet.Vest [] vesti = v.toArray(new Predmet.Vest[v.size()]); ;
        
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

    boolean downloadRunning = false;
    long downloadId = 0;
    DownloadManager manager;

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
                String mime = getMimeFromExtension(url);
               // request.setMimeType(mime);
			    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
			}
			request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
	
			// get download service and enqueue file
            downloadRunning = true;
			downloadId = manager.enqueue(request);
		}
	}

    BroadcastReceiver onDownloadComplete=new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);

            alert.setTitle("Preuzimanje završeno");
            alert.setMessage("Želite li da otvorite datoteku?");

            alert.setPositiveButton("Da", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    openFile(context, manager.getUriForDownloadedFile(downloadId));
                }
            });

            alert.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });


            alert.show();
        }
    };


    // Gets MIME type from extension
    protected String getMimeFromExtension(String url)
    {
        String extension = url.substring(url.lastIndexOf("."));
        String mimeTypeMap = MimeTypeMap.getFileExtensionFromUrl(extension);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeTypeMap);
        return mimeType;
    }

    // Opens file after download
    protected void openFile(Context context, Uri fileName) {
        Intent open = new Intent(Intent.ACTION_VIEW);
        open.setDataAndType(fileName,
                getMimeFromExtension(fileName.getPath()));
        try {
            startActivity(open);
        }
        catch(ActivityNotFoundException ex)
        {
            TipDatotekeNijePodrzan(context);

        }
    }

    void TipDatotekeNijePodrzan(Context context)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);


        alertDialogBuilder.setTitle("Greška pri otvaranju");

        // set dialog message
        alertDialogBuilder
                .setMessage("Ne postoji instaliran program za otvaranje date datoteke")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {

                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
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
            PredmetXMLParser parser = new PredmetXMLParser();
			predmeti = parser.parse(new ByteArrayInputStream(result.getBytes("UTF-8")));
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		LinearLayout toolbar = (LinearLayout)findViewById(R.id.predmeti);
        DodajPredmete(toolbar);
        toolbarButtons.get(0).performClick();
		
	}



    private static final String ns = null;



}
