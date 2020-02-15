package http;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//요청헤더를 키와 밸류값으로 분리하여 저장하기 위한 클래스
class HttpHeaders {
	//요청헤더에서 Content-Length를 저장할 변수
	private static final String CONTENT_LENGTH = "Content-Length";

	private static final Logger log = LoggerFactory.getLogger(HttpHeaders.class);
	
	//요청헤더에 있는 데이터를 키와 밸류형태로 Map에 저장할 변수
	private Map<String, String> headers = new HashMap<>();
	
	//요청헤더를 키와 밸류로 구분하여 headers에 저장하는 작업을 수행한다.
	void add(String header) {
		log.debug("header : {}", header);
		
		//
	    String[] splitedHeaders = header.split(":");
	    headers.put(splitedHeaders[0], splitedHeaders[1].trim());   
	}
	
    String getHeader(String name) {
        return headers.get(name);
    }
    
    int getIntHeader(String name) {
    	String header = getHeader(name);
    	return header == null ? 0 : Integer.parseInt(header);
    }
    
    int getContentLength() {
    	return getIntHeader(CONTENT_LENGTH);
    }
}
