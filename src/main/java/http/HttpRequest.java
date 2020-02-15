package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.IOUtils;

public class HttpRequest {
	private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
	
	//요청라인을 저장할 변수
	private RequestLine requestLine;
	//요청헤더를 저장할 변수
	private HttpHeaders headers;
	//파라미터를 저장할 변수
	private RequestParams requestParams = new RequestParams();

	public HttpRequest(InputStream is) {
		try {
			//요청데이터를 라인단위로 읽기 위해서 BufferedReader객체 생성
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			//요청라인을 읽어서 requestLine객체에 저장한다.
			requestLine = new RequestLine(createRequestLine(br));
			//요청라인에 클라이언트가 요청한 경로에 쿼리스트링이 있으면 requestParams 객체에 추가한다.
			requestParams.addQueryString(requestLine.getQueryString());
			//요청헤더를 추출하여 headers변수에 저장한다.
			headers = processHeaders(br);
			//파라미터를 requestParams 변수에 저장한다.
			requestParams.addBody(IOUtils.readData(br, headers.getContentLength()));
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	//요청라인을 추출하는 메서드
	private String createRequestLine(BufferedReader br) throws IOException {
		String line = br.readLine();
		//요청라인이 없으면 에러를 발생시킨다.
		if (line == null) {
			throw new IllegalStateException();
		}
		return line;
	}
	
	//요청헤더를 추출하는 메서드
	private HttpHeaders processHeaders(BufferedReader br) throws IOException {
		//요청헤더를 저장하기 위한 변수를 생성한다.
		HttpHeaders headers = new HttpHeaders();
		String line;
		
		//요청헤더를 한 줄 씩 추출하여 headers 변수에 저장한다.
		while (!(line = br.readLine()).equals("")) {
			headers.add(line);
		}
		return headers;
	}

	public HttpMethod getMethod() {
		return requestLine.getMethod();
	}

	public String getPath() {
		return requestLine.getPath();
	}

	public String getHeader(String name) {
		return headers.getHeader(name);
	}

	public String getParameter(String name) {
		return requestParams.getParameter(name);
	}
}
