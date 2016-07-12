package main.org.lele.Task;
/**
 * 
 * @author LELE 16/9/2015
 *
 */
public class HttpResponse {
	private int statusCode ;
	private String response ;
	
	public int getStatusCode(){
		return statusCode ;
	}
	
	public String getResponse(){
		return response ;
	}
	
	//构造函数
	public HttpResponse(int statusCode, String response){
		this.statusCode = statusCode ;
		this.response = response ;
	}
}
