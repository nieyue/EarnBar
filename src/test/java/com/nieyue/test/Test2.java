package com.nieyue.test;

import com.nieyue.util.HttpClientUtil;

public class Test2 {
 public static void main(String[] args) throws Exception {
	//String url="http://url.cn/4AAIPA1";
	//String url2="http://www.hebeimulin.com/beizhuan/weixin20/member/receiveMonkeyXd.action?userid=orLJBwCu6wnP9aelC7waEcr7x5HM";
	//String url3="http://www.lucky91.com/beizhuan/weixin20/member/rtview.action?userid=orLJBwCu6wnP9aelC7waEcr7x5HM";
	//String url4="http://www.lucky91.com/beizhuan/weixin20/member/stview.action?userid=orLJBwCu6wnP9aelC7waEcr7x5HM";
	
	 //String data = HttpClientUtil.doGet(url4);
	//String data = HttpClientUtil.doPostJson(url3, "{ce:''}");
	// System.out.println(data);
	 String a="5.0 (Linux; Android 6.0; M5s Build/MRA58K; wv) AppleWebKit/537.36; wv) (KHTML, like Gecko) Version/4.0 Chrome/44.0.2403.146 Mobile Safari/537.36 Html5Plus/1.0ip地址=5.0 (Linux; Android 6.0; M5s Build/MRA58K; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/44.0.2403.146 Mobile Safari/537.36 Html5Plus/1.0";
	 String b="5.0 (Linux; Android 6.0; M5s Build/MRA58K) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/44.0.2403.146 Mobile Safari/537.36 Html5Plus/1.0";
	 System.out.println(a.substring(0,a.indexOf("; wv)")));
	 System.out.println(b.indexOf("; wv)"));
	 if(b.indexOf("; wv)")<=-1){
		 System.out.println(b.substring(0,b.indexOf(")")));
		 
	 }
}
}
