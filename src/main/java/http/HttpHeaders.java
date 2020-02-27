package http;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class HttpHeaders {
	private static final String COOKIE = "Cookie";

	private static final String CONTENT_LENGTH = "Content-Length";

	private static final Logger log = LoggerFactory.getLogger(HttpHeaders.class);

	private Map<String, String> headers = new HashMap<>();
	
	//요청헤더에서 한 줄 씩 읽어 매개변수 header로 값이 넘어오면 ":"로 키와 밸류형식으로 분리하여 
	//headers객체에 저장한다.
	void add(String header) {
		log.debug("header : {}", header);
	    String[] splitedHeaders = header.split(":");
	    headers.put(splitedHeaders[0], splitedHeaders[1].trim());
	}
	
	//Cookie라는 이름으로 된 값이 여러 개일 경우 name1=value1; name2=value2; name3=value3;... 이런 식으로 값이 반환된다.
    String getHeader(String name) {
    	//headers는 요청헤더를 키와 밸류형식으로 보관하는 객체이다.
        return headers.get(name);
    }
    
    int getIntHeader(String name) {
    	String header = getHeader(name);
    	return header == null ? 0 : Integer.parseInt(header);
    }
    
    int getContentLength() {
    	return getIntHeader(CONTENT_LENGTH);
    }
    
    //getHeader(COOKIE)는 name1=value1; name2=value2; name3=value3;...이런 식으로 값을 반환한다.
    //name과 value 형식으로 저장된 HttpCookie가 반환된다.
    HttpCookie getCookies() {
    	return new HttpCookie(getHeader(COOKIE));
    }
    
    HttpSession getSession() {
        return HttpSessions.getSession(getCookies().getCookie("JSESSIONID"));
    }
}
