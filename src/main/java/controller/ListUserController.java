package controller;

import http.HttpRequest;
import http.HttpResponse;

import java.util.Collection;
import java.util.Map;

import model.User;
import util.HttpRequestUtils;
import db.DataBase;

//회원목록
public class ListUserController extends AbstractController {
    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
    	//요청헤더에서 Cookie의 밸류에서 logined=true인지 아닌지 확인
        if (!isLogin(request.getHeader("Cookie"))) {
        	//false라면 로그인이 안된 것이므로 로그인 페이지로 리다이렉트한다.
        	//index.html로 리다이렉트한다.
            //리다이렉트를 하는 이유는 새로고침시 입력한 정보가 재요청되므로 같은 작업이나 같은 데이터가 반복되어 처리될 수 있기 때문이다.
            //302 상태 코드와 경로를 브라우저에게 응답으로 보내어 브라우저가 다시 서버로 경로를 재요청하게 한다.
            //브라우저 경로창이 index.html로 바뀌면서 새로고침시 같은 작업을 반복하지 않게 된다.
            response.sendRedirect("/user/login.html");
            return;
        }
        
        //로그인이 되어 있는 상태라면 데이터베이스에서 user정보를 다 가져와서 출력한다.
        Collection<User> users = DataBase.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("<table border='1'>");
        for (User user : users) {
            sb.append("<tr>");
            sb.append("<td>" + user.getUserId() + "</td>");
            sb.append("<td>" + user.getName() + "</td>");
            sb.append("<td>" + user.getEmail() + "</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        
        //응답본문을 직접 만들어 response객체에게 출력하도록 처리
        response.forwardBody(sb.toString());
    }
    
    //현재 로그인이 되어 있는지
    private boolean isLogin(String cookieValue) {
    	//쿠키 밸류를 추출하여 다시 키-밸류형태로 맵에 저장한다.
        Map<String, String> cookies = HttpRequestUtils.parseCookies(cookieValue);
        //맵에서 logined 키의 밸류를 추출한다.
        String value = cookies.get("logined");
        //밸류가 없다면 false리턴
        if (value == null) {
            return false;
        }
        
        //밸류가 존재한다면 true리턴 
        return Boolean.parseBoolean(value);
    }
}
