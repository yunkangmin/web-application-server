package http;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private DataOutputStream dos = null;
    
    //응답헤더를 키-밸류 형태로 저장하는 변수
    private Map<String, String> headers = new HashMap<String, String>();

    public HttpResponse(OutputStream out) {
        dos = new DataOutputStream(out);
    }
    
    public void addHeader(String key, String value) {
        headers.put(key, value);
    }
    
    //응답데이터를 출력하기 전 요청경로에 대한 응답헤더를 만드는 메서드
    public void forward(String url) {
        try {
        	//
            byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
            if (url.endsWith(".css")) {
                headers.put("Content-Type", "text/css");
            } else if (url.endsWith(".js")) {
                headers.put("Content-Type", "application/javascript");
            } else {
                headers.put("Content-Type", "text/html;charset=utf-8");
            }
            //응답본무의 길이가 얼마인지 정보를 생성하여 저장
            headers.put("Content-Length", body.length + "");
            //응답라인과 응답헤더를 만든다.
            response200Header(body.length);
            //응답본문을 만들어 페이지를 출력한다.
            responseBody(body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    //응답 본문을 직접 생성한 내용을 매개변수로 받아 응답헤더를 만들고 출력하는 메서드
    public void forwardBody(String body) {
        byte[] contents = body.getBytes();
        headers.put("Content-Type", "text/html;charset=utf-8");
        headers.put("Content-Length", contents.length + "");
        response200Header(contents.length);
        responseBody(contents);
    }
    
    //응답라인과 응답헤더를 만든다
    private void response200Header(int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            //응답헤더 출력하는 메서드
            processHeaders();
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    //응답 본문을 만들어 출력하는 메서드
    private void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    //리다이렉트 메서드
    public void sendRedirect(String redirectUrl) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            processHeaders();
            dos.writeBytes("Location: " + redirectUrl + " \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    //응답헤더를 출력하는 메서드
    private void processHeaders() {
        try {
            Set<String> keys = headers.keySet();
            for (String key : keys) {
                dos.writeBytes(key + ": " + headers.get(key) + " \r\n");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
