package co.boric.nemanja.ot;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

class DownloadData extends AsyncTask<String,Integer,String> {

	Context context;
	Telekomunikacije naslovna;
	public DownloadData(Telekomunikacije naslovna, Context context)
	{
		this.context = context;
		this.naslovna = naslovna;
	}
	
	ProgressDialog progDailog;
	protected void onPreExecute() {
		super.onPreExecute();
        progDailog = new ProgressDialog(context);
        progDailog.setMessage("Uèitavanje...");
        progDailog.setIndeterminate(false);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(true);
        progDailog.show();
    }


    @Override
    public String doInBackground(String... params) {
       return getFeed(params[0]);
    }

    private String getFeed(String url) {
    	// DOWNLOAD THE PROJECT JSON FILE
	/*	HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet httpget = new HttpGet(url);

            // Create a response handler
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = httpclient.execute(httpget, responseHandler);
            
            //
            return  responseBody;

        } catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
        finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
        return "";*/
        String s = "", html = "";
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.connect();
            InputStreamReader reader = new InputStreamReader(conn.getInputStream(), "UTF-8");
            StringBuilder builder = new StringBuilder();
            char[] buffer = new char[8192];

            for (int length = 0; (length = reader.read(buffer)) > 0;) {
                builder.append(buffer, 0, length);
            }
            	
            return builder.toString();
        } catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            if (conn != null)
                conn.disconnect();
        }
        return "";

    }

    @Override
    public void onPostExecute(String result) {
        super.onPostExecute(result);
        progDailog.dismiss();
        naslovna.loadData(result);
    }

}
