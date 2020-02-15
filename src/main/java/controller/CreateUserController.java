package controller;

import http.HttpRequest;
import http.HttpResponse;
import model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;

//회원가입시 
public class CreateUserController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        User user = new User(request.getParameter("userId"), request.getParameter("password"),
                request.getParameter("name"), request.getParameter("email"));
        log.debug("user : {}", user);
        DataBase.addUser(user);
        //index.html로 리다이렉트한다.
        //리다이렉트를 하는 이유는 새로고침시 입력한 정보가 재요청되므로 같은 작업이나 같은 데이터가 반복되어 처리될 수 있기 때문이다.
        //302 상태 코드와 경로를 브라우저에게 응답으로 보내어 브라우저가 다시 서버로 경로를 재요청하게 한다.
        //브라우저 경로창이 index.html로 바뀌면서 새로고침시 같은 작업을 반복하지 않게 된다.
        response.sendRedirect("/index.html");
    }
}
