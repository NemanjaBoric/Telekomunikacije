package co.boric.nemanja.ot.telekomunikacije;

import co.boric.nemanja.ot.telekomunikacije.R;
import android.app.Activity;
import android.content.Context;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class NovostiAdapter extends ArrayAdapter<Predmet.Vest> {
	Context context;
	int layoutResourceId;
	Predmet.Vest novosti[] = null;
	
	public NovostiAdapter(Context context, int layoutResourceId, Predmet.Vest [] novosti)
	{
		super(context, layoutResourceId, novosti);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.novosti = novosti;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View row = convertView;
		NovostiHolder holder = null;
		
		if(row == null)
		{
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			
			holder = new NovostiHolder();
			holder.naslov = (TextView)row.findViewById(R.id.naslov); 
			holder.text = (TextView)row.findViewById(R.id.text); 
			
			row.setTag(holder);
		}
		else
		{
			holder = (NovostiHolder)row.getTag();
		}
		
		Predmet.Vest novost  = novosti[position];
		holder.naslov.setText(novost.naslov);
		holder.text.setText(novost.desc);
		holder.data = novost.data;
		
		
		return row;
	}
	
	public static class NovostiHolder
	{
		TextView naslov;
		TextView text;
		String data;
	}
	
}
