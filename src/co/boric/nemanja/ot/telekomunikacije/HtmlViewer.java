package co.boric.nemanja.ot.telekomunikacije;

import co.boric.nemanja.ot.telekomunikacije.R;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class HtmlViewer extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        //Ukloni HtmlViewer
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setTitle("Naslovna");
        
        setContentView(R.layout.activity_html_viewer);
        WebView web = (WebView)findViewById(R.id.webView1);
        web.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView wView, String url)
            {
            	if( url.startsWith("http:") || url.startsWith("https:") ) {
                    return false;
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity( intent ); 
                return true;

            }
        });
        
        Intent intent = getIntent();
        String url = intent.getExtras().getString("data");
        web.loadDataWithBaseURL(null, url, "text/html", "utf-8", null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_html_viewer, menu);
        return true;
    }
}
