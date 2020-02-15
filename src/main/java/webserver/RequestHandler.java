package webserver;

import http.HttpRequest;
import http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.Controller;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            //요청작업을 처리할 객체생성
        	HttpRequest request = new HttpRequest(in);
        	//응답작업을 처리할 객체생성
            HttpResponse response = new HttpResponse(out);
            //클라이언트가 요청한 경로에 해당하는 controller 객체를 저장한다.
            Controller controller = RequestMapping.getController(request.getPath());
            //경로에 맵핑된 컨트롤러가 없으면 /index.html 페이지를 출력한다.
            if (controller == null) {
                String path = getDefaultPath(request.getPath());
                response.forward(path);
            } else {//경로에 맵핑된 컨트롤러가 있다면 해당 컨트롤러
                controller.service(request, response);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getDefaultPath(String path) {
        if (path.equals("/")) {
            return "/index.html";
        }
        return path;
    }
}
