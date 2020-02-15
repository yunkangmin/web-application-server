package controller;

import http.HttpMethod;
import http.HttpRequest;
import http.HttpResponse;
//GET POST를 구분하는 클래스
//각 컨트롤러는 이 클래스를 상속하여 구현한다.
public abstract class AbstractController implements Controller {
	//GET POST인지 구분하는 메서드
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        HttpMethod method = request.getMethod();
        
        if (method.isPost()) {
            doPost(request, response);
        } else {
            doGet(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
    }
}
