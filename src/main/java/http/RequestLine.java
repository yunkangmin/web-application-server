package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//요청라인을 읽기 위한 클래스
public class RequestLine {
    private static final Logger log = LoggerFactory.getLogger(RequestLine.class);
    
    //HTTP 메서드가 GET인지 POST인지 저장할 변수 
    private HttpMethod method;
    
    //요청라인에 경로를 저장한 변수
    private String path;
    
    //요청라인에 경로뒤에 쿼리스트링을 저장할 변수
    private String queryString;
    
    //생성자
    public RequestLine(String requestLine) {
    	//요청라인을 출력한다.
    	log.debug("request line : {}", requestLine);
    	
    	//요청라인을 " "로 구분하여 배열로 저장한다.
		String[] tokens = requestLine.split(" ");
		
		//요청라인에서 HTTP메서드를 method변수에 저장한다.
		this.method = HttpMethod.valueOf(tokens[0]);
		
		//요청라인에서 url을 경로와 쿼리스트링으로 분리하여 url변수에 저장한다.
		String[] url = tokens[1].split("\\?");
		
		//경로만 path에 저장한다.
		this.path = url[0];
		
		//url 길이가 2이면 쿼리스트링이 있다는 것이므로 url변수에서 queryString변수로 쿼리스트링을 저장한다.
		if (url.length == 2) {
			this.queryString = url[1];
		}
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQueryString() {
		return queryString;
	}
}
