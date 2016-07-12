package main.org.lele.spider;
/**
 * 
 * @author LELE 15/9/2015
 * 表示一个完整的智联招聘项(url/position/positionDetail)
 *
 */
public class Item {
	private String url ;
	private String position ;
	private String positionDetail ;
	
	public String toString(){
		StringBuilder sb = new StringBuilder() ;
		sb.append(position).append("\n\n\n").append(url).append("\n\n\n").append(positionDetail).append("\n\n\n");
		//String sb = position + "\n" + url + "\n" + positionDetail + "\n";

		return  sb.toString();
		
	}
	
	public Item(String url){
		this.url = url ;
	}
	public String getUrl(){
		return url ;
	}
	public void setUrl(String url){
		this.url = url ;
	}
	
	public String getPosition(){
		return position ;
	}
	public void setPosition(String position){
		this.position = position ;
	}
	
	public String getPositionDetail(){
		return positionDetail ;
	}
	public void setPositionDetail(String positionDetail){
		this.positionDetail = positionDetail ;
	}
}
