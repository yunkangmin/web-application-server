package controller;

import model.User;
import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;

//로그인 시
public class LoginController extends AbstractController {
    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
    	//파라미터에 아이디가 데이터베이스에 있다면 user객체를 반환한다.
        User user = DataBase.findUserById(request.getParameter("userId"));
        //user객체가 있다면
        if (user != null) {
        	//해당 user와 파라미터로 넘어온 비밀번호가 같다면
            if (user.login(request.getParameter("password"))) {
            	//응답헤더에 쿠키정보를 설정한다.
                response.addHeader("Set-Cookie", "logined=true");
                //index.html로 리다이렉트한다.
                //리다이렉트를 하는 이유는 새로고침시 입력한 정보가 재요청되므로 같은 작업이나 같은 데이터가 반복되어 처리될 수 있기 때문이다.
                //302 상태 코드와 경로를 브라우저에게 응답으로 보내어 브라우저가 다시 서버로 경로를 재요청하게 한다.
                //브라우저 경로창이 index.html로 바뀌면서 새로고침시 같은 작업을 반복하지 않게 된다.
                response.sendRedirect("/index.html");
            } else {
                response.sendRedirect("/user/login_failed.html");
            }
        } else {
            response.sendRedirect("/user/login_failed.html");
        }
    }
}
