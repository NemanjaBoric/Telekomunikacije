package co.boric.nemanja.ot;

public final class Novost {
	private String naslov;
	private String text;
	private int link_id;
	
	public Novost(String naslov, String text, int link_id)
	{
		super();
		this.naslov = naslov;
		this.text = text;
		this.link_id = link_id;
	}
	
	public Novost()
	{
		super();
	}
	
	public String getNaslov(){ 
		return naslov;
	}
	
	public String getText(){
		return text;
	}
	
	public int getLink()
	{
		return link_id;
	}
	
}
