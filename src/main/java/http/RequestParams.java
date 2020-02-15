package http;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;
//파라미터를 키와 밸류로 분리하여 저장하기 위한 클래스
public class RequestParams {
	private static final Logger log = LoggerFactory.getLogger(RequestParams.class);
	
	private Map<String, String> params = new HashMap<>();

	public void addQueryString(String queryString) {
		putParams(queryString);
	}
    //파라미터를 키-밸류 형태로 분리하는 메서드
	private void putParams(String data) {
		log.debug("data : {}", data);
		
		if (data == null || data.isEmpty()) {
			return;
		}
		
		//키-밸류 형태로 저장한 map을 params에 저장한다.
		params.putAll(HttpRequestUtils.parseQueryString(data));
		log.debug("params : {}", params);
	}

	public void addBody(String body) {
		putParams(body);	
	}

	public String getParameter(String name) {
		return params.get(name);
	}
}
