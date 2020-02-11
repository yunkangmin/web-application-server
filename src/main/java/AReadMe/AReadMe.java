package AReadMe;

import util.HttpRequestUtils;

/*
 * 이 클래스는 프로젝트 설명을 위해 생성한 클래스입니다.
 * 
 *  ※웹 어플리케이션 서버를 실행하는 방법
 *  1.webserver 패키지 -> WebServer 클래스(main메서드가 있음) 실행
 * 
 * */
public class AReadMe {

	public static void main(String[] args) {
		String url = "create?userId=hvs123&password=1234&name=%EC%9C%A4%EA%B0%95%EB%AF%BC&email=hvs123%40naver.com";
    	if(url.startsWith("create")) {
    		int index = url.indexOf("?");
    		
    		
    		String requestPath = url.substring(0, index);
    		String queryString = url.substring(index + 1);
    		System.out.println("index = " + index);
    		System.out.println("requestPath = " + requestPath);
    		System.out.println("queryString = " + queryString );
    		
    	}
    	
	}
}
