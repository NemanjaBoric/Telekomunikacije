package co.boric.nemanja.ot;

public final class Download {
	String url;
	String caption;
	String text;
	String filename; 
	public Download(String url, String caption, String text, String filename)
	{
		this.url = url;
		this.caption = caption;
		this.text = text;
		this.filename = filename;
	}
	
	public String getUrl(){ return url; }
	public String getCaption(){ return caption; }
	public String getText(){ return text; }
	public String getFileName(){ return filename; }
}
