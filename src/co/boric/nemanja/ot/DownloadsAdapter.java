package co.boric.nemanja.ot;

import co.boric.nemanja.ot.Telekomunikacije.Predmet;
import co.boric.nemanja.ot.R;
import android.app.Activity;
import android.content.Context;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class DownloadsAdapter extends ArrayAdapter<Predmet.Preuzimanje> {
	Context context;
	int layoutResourceId;
	Predmet.Preuzimanje downloads[] = null;
	
	public DownloadsAdapter(Context context, int layoutResourceId, Predmet.Preuzimanje [] downloads)
	{
		super(context, layoutResourceId, downloads);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.downloads = downloads;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View row = convertView;
		DownloadHolder holder = null;
		
		if(row == null)
		{
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			
			holder = new DownloadHolder();
			holder.naslov = (TextView)row.findViewById(R.id.naslov); 
			holder.text = (TextView)row.findViewById(R.id.text); 
			
			row.setTag(holder);
		}
		else
		{
			holder = (DownloadHolder)row.getTag();
		}
		
		Predmet.Preuzimanje down  = downloads[position];
		holder.naslov.setText(down.naslov);
		holder.text.setText(down.desc);
		holder.url = down.url;
		holder.filename = down.filename;
		
		
		return row;
	}
	
	public static class DownloadHolder
	{
		TextView naslov;
		TextView text;
		String filename;
		String url;
	}
	
}
