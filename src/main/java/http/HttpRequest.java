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

	private RequestLine requestLine;

	private HttpHeaders headers;
	
	private RequestParams requestParams = new RequestParams();

	public HttpRequest(InputStream is) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			requestLine = new RequestLine(createRequestLine(br));
			requestParams.addQueryString(requestLine.getQueryString());
			//요청라인을 제외한 요청헤더를 headers 객체에 키와 밸류값으로 관리한다.
			headers = processHeaders(br);
			requestParams.addBody(IOUtils.readData(br, headers.getContentLength()));
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private String createRequestLine(BufferedReader br) throws IOException {
		String line = br.readLine();
		if (line == null) {
			throw new IllegalStateException();
		}
		return line;
	}

	private HttpHeaders processHeaders(BufferedReader br) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		String line;
		//요청헤더에서 ""을 만날 때까지 읽는다.
		//""는 요청본문과 요청헤더를 나누는 기준이다.
		while (!(line = br.readLine()).equals("")) {
			//요청헤더들의 정보를 저장하고 관리하는 headers
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
	
	//쿠키값( name1=value1; name2=value2; name3=value3;...)을 키와 밸류형식으로 저장하고 관리하는 HttpCookie 객체를 반환한다.
	public HttpCookie getCookies() {
		return headers.getCookies();
	}
	
	public HttpSession getSession() {
		return headers.getSession();
	}
}
