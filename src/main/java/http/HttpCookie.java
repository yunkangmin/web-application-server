package http;

import java.util.Map;

import util.HttpRequestUtils;

//쿠키들을 관리하는 클래스
public class HttpCookie {
    private Map<String, String> cookies;
    
    // name1=value1; name2=value2; name3=value3;와 같이 여러 개의 쿠키 값이 넘어올 경우 
    //name과 value 형식으로 맵에 저장한다.
    HttpCookie(String cookieValue) {
        cookies = HttpRequestUtils.parseCookies(cookieValue);
    }

    public String getCookie(String name) {
        return cookies.get(name);
    }
}
