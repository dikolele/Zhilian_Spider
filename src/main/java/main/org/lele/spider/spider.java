package main.org.lele.spider;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.org.lele.Task.HttpResponse;
import main.org.lele.Task.Task;
import main.org.lele.network.MultiplexingHttpConectionPool;
import main.org.lele.threadpool.ThreadPool;

import org.apache.http.impl.client.CloseableHttpClient;

/**
 * 
 * @author LELE 15/9/2015
 * 具体的抓取工作
 */
public class spider {
	private String baseUrl = "http://xiaoyuan.zhaopin.com" ;//首页
	private List<Item> itemList = new ArrayList<Item>() ;
	//首页
	
	public boolean processWelcome(String targetUrl){
		//要爬的页面
		//targetUrl = "http://xiaoyuan.zhaopin.com/job/CC000118114J90000011000" ;
		HttpResponse hr = download(targetUrl) ;
		if(hr == null){
			return false ;
		}
		
		String text = hr.getResponse();
		String regex1 =  "<a.+?href=\"(/job/\\w+)\".+?\r\n.+?</a>";//工作链接                             
		Pattern pat1 = Pattern.compile(regex1);
		Matcher m1 = pat1.matcher(text) ;
			while(m1.find()){
			String url = m1.group(1) ;
			Item item = new Item(baseUrl+url);
			item.setPosition(m1.group(1).trim());
			itemList.add(item) ;
		}
		return true ;
	}
	//改成并发
	public boolean processItem(){
		for(Item item : itemList){
			String targetUrl = item.getUrl();
			HttpResponse hr = download(targetUrl) ;
			if(hr == null){
				return false ;
			}
			
			String text = hr.getResponse();
			text = text.replace("\n", "");
			//工作描述
			String regex = "<p class=\"mt20\"(.+?)</p>" ;
			Pattern pat = Pattern.compile(regex) ;
			Matcher m = pat.matcher(text);
			if(m.find()){
				String positionDetail = m.group(1) ;
				positionDetail = positionDetail .replaceAll("<br[/]?>", "\r\n") ;
				//positionDetail = positionDetail.replaceAll("</p>", "\r\n") ;
				//positionDetail = positionDetail.replaceAll("(.*?)", "") ;
				//positionDetail = positionDetail.replaceAll("<[^>]+>", "") ;
				item.setPositionDetail(positionDetail);
				
			}
		}
		return true ;
	}
	public void print(){
		FileOutputStream fos = null ;
		try{
			System.out.println();
			fos = new FileOutputStream("zhilianzhaopin.txt") ;
		}catch(FileNotFoundException e){
		 
			e.printStackTrace();
		}
		for(Item item : itemList){
			try{
				fos.write(item.toString().getBytes());
			
			}catch(IOException e){
				
				e.printStackTrace();
			}
		}
		try{
			fos.flush();
		}catch(IOException e){
			 
			e.printStackTrace();
		}
		try{
			fos.close(); 
		}catch(IOException e){
			
			e.printStackTrace();
		}
	}
	/**
	 * 根据传入的targetUrl去拉取数据，仅当有实质内容返回时才不返回null
	 * @param targetUrl 目标资源的URL
	 * @return当有实质内容返回时返回一个HttpResponse object;其他情况返回null.
	 */
	public HttpResponse download(String targetUrl){
		System.out.println("\"小真君\"正在努力抓取" + targetUrl + "... ...") ;
		//从Http连接池获取一个长连接
		CloseableHttpClient client = MultiplexingHttpConectionPool.getInstance().getConnection(true);
		if(client == null){
			System.out
				.println("MultiplexingHttpConnectionPool.getInstance().getConnetion(true) returned null");
			return null ;
		}
		
		//构建一个task并扔到线程池去执行
		Future<HttpResponse> future = ThreadPool.getInstance().submit(new Task(client,targetUrl)) ;
		HttpResponse hr = null ;
		try{
			//在future对象中获取抓取结果，最多等待五秒
			hr = future.get(5000,TimeUnit.MILLISECONDS);
		}catch(Exception e){
			future.cancel(true) ;
			e.printStackTrace();
		}
		
		//如果返回空，或者http返回码不为200，那么肯定没有抓取结果
		if(hr == null || hr.getStatusCode() != 200){
			return null ;
		}
		return hr;
	}
	
	public static void main(String[] args){
		spider spider1 = new spider() ;
		//入口
		String targetUrl = "http://xiaoyuan.zhaopin.com/FindFullTime/FullTimeSearch/SearchResult/?ind=210500&&SearchModel=0&&ref=jobsearch&&OB=0&&ST=1" ;
		
		@SuppressWarnings("unused")
		boolean hr = spider1.processWelcome(targetUrl) ;
		//多线程
		spider1.processItem();
		spider1.print();
		ThreadPool.getInstance().shutdown();//关闭线程池
		MultiplexingHttpConectionPool.getInstance().uninitialize();//关闭http连接池
	}
}
