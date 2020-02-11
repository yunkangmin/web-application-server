package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());
        //java7버전부터 try 괄호안에 객체를 생성하면 알아서 close()를 실행시킨다. 단, 해당 클래스가 implement closable 되있어야 한다.	
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
        	BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        	String line = br.readLine();
        	if (line == null) {
        		return;
        	}
        	
        	//요청라인만 가져오기(요청규약의 첫번째 라인)
        	String url = HttpRequestUtils.getUrl(line);
        	//요청데이터의 Conten-Length 속성의 값을 담을 변수
        	int contentLength = 0;
        	//요청데이터의 Cookie 속성의 값을 담을 변수
        	boolean logined = false;
        	//요청라인 이후 데이터를 읽는다.
        	while(!"".equals(line)) {
        		log.debug("header : {}", line);
        		line = br.readLine();
        		//요청데이터를 읽다가 Content-Length속성이 있으면 그 값을 contentLength변수에 저장한다.
        		if(line.contains("Content-Length")) {
        			contentLength = getContentLength(line);
        		}
        		//요청데이터를 읽다가 Cookie속성이 있으면 그 값을 logined변수에 저장한다.
        		if(line.contains("Cookie")) {
        			//cookie속성에서 추출한 키=밸류 형식의 값에서 밸류를 가져와서 logined 변수에 저장한다.
        			logined = isLogin(line);
        		}
        	}
        	log.debug("Content-Length : {}", contentLength);
        	//회원정보를 입력한 후 회원가입 버튼 클릭 시
        	if("/user/create".equals(url)) {
        		//회원가입 GET 방식
        		//index는 0부터 시작
        		//int index = url.indexOf("?");
        		//0부터 index포함한 글자까지 자르기
        		//String requestPath = url.substring(0, index);
        		//index포함한 글자부터 끝까지 자르기
        		//String queryString = url.substring(index + 1);

        		//회원가입 POST방식
        		//BufferedReader br 객체를 전달함으로서 요청데이터의 body 부분에 있는 쿼리스트링을 추출한다.
        		String requestBody = IOUtils.readData(br, contentLength);
        		log.debug("Request Body : {}", requestBody);
        		//쿼리스트링을 키와 밸류로 분리하여 Map에 저장한다.
        		Map<String, String> params = HttpRequestUtils.parseQueryString(requestBody);
        		User user = new User(params.get("userId"),params.get("password") , params.get("name"), params.get("email"));
        		log.debug("User : {}", user);
        		DataBase.addUser(user);
        		DataOutputStream dos = new DataOutputStream(out);
        		//리다이렉트
        		response302Header(dos, "/index.html");
        	//로그인 정보를 입력하고 로그인 버튼을 클릭 시
        	}else if ("/user/login".equals(url)) {
        		String requestBody = IOUtils.readData(br, contentLength);
        		log.debug("Request Body : {}", requestBody);
        		                             //쿼리스트링을 키와 밸류로 분리하여 Map에 저장한다.
        		Map<String, String> params = HttpRequestUtils.parseQueryString(requestBody);
        		log.debug("UserId : {}, password : {}", params.get("userId"), params.get("password"));
        		//로그인 시 적은 아이디를 가지고 데이터베이스 객체에서 User 정보를 검색하여 있으면 User 객체를 반환한다.
        		User user = DataBase.findUserById((params.get("userId")));
        		//로그인 정보가 없을 시
        		if(user == null) {
        			log.debug("User Not Found!");
        			//로그인 실패 페이지 출력
        			responseResource(out, "/user/login_failed.html");
        			return;
        		}
        		//비밀번호가 일치하면 로그인 성공한다. 아이디는 위에서 검증됨.
        		if(user.getPassword().equals(params.get("password"))) {
        			log.debug("login success!");
        			DataOutputStream dos = new DataOutputStream(out);
        			//쿠키정보를 함께 보내어 리다이렉트한다.
        			//브라우저에서 요청시 경로가 /user 하위일 때만  Cookie 값이 전송된다.
        			response302HeaderWithCookie(dos, "logined=true");
        		}else {
        			log.debug("Password Mismatch!");
        			responseResource(out, "/user/login_failed.html");
        		}
        	//사용자 목록 출력하기
        	}else if("/user/list".equals(url)) {
        		//쿠키값이 없다면 사용자 목록페이지를 출력하지 않고 로그인 페이지로 간다.
        		if(!logined) {
        			responseResource(out, "/user/login.html");
        			return;
        		}
        		//데이터베이스 객체에 저장된 모든 사용자 정보를 가져온다.
        		Collection<User> users = DataBase.findAll();
        		StringBuilder sb = new StringBuilder();
        		sb.append("<table border='1'>");
        		//모든 유저정보를 브라우저에 출력하기 위해서 HTML형태로 StringBuilder sb에 담는다. 
        		for(User user : users) {
        			sb.append("<tr>");
        			sb.append("<td>" + user.getUserId() + "</td>");
        			sb.append("<td>" + user.getName() + "</td>");
        			sb.append("<td>" + user.getEmail() + "</td>");
        			sb.append("</tr>");
        		}
        		sb.append("</table>");
        		//StringBuilder sb에 담긴 내용을 바이트 배열 형태로 만든다.
        		byte[] body = sb.toString().getBytes();
        		DataOutputStream dos = new DataOutputStream(out);
        		response200Header(dos, body.length);
        		responseBody(dos, body);
        	//css파일 Content-Type 값 적용하기
        	}else if (url.endsWith(".css")){
        		DataOutputStream dos = new DataOutputStream(out);
            	byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
            	response200CssHeader(dos, body.length);
        		responseBody(dos, body);
        	}else {
        		responseResource(out, url);
        	}
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    /**
     * css파일 응답데이터 만들기
     * Contetn-Type을 text/css로 응답을 보낸다.
     * */
    private void response200CssHeader(DataOutputStream dos, int lengthOfBodyContent) {
    	try {
    		dos.writeBytes("HTTP/1.1 200 OK\r\n");
    		dos.writeBytes("Content-Type: text/css\r\n");
    		dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
    		dos.writeBytes("\r\n");
    	} catch (IOException e) {
    		log.error(e.getMessage());
    	}
    }
    
    /**
     * 요청데이터에서 Cookie속성이 있는지 검사 후 밸류를 리턴한다. 
     */
    private boolean isLogin(String line) {
    	//요청데이터를 키와 밸류로 분리한 뒤 배열로 반환한다.
    	String[] headerTokens = line.split(":");
    	//Cookie 속성의 값을 가져와 그 값을 키-밸류형태로 맵에 저장한다.
    	Map<String, String> cookies = HttpRequestUtils.parseCookies(headerTokens[1].trim());
    	//logined로 된 키가 있으면 그에 해당하는 밸류값을 리턴한다.
    	String value = cookies.get("logined");
    	//밸류값이 없다면 false를 리턴한다.
    	if(value == null) {
    		return false;
    	}
    	return Boolean.parseBoolean(value);
    }
    
    /**
     * 페이지 출력 함수
     */
    private void responseResource(OutputStream out, String url) throws IOException{
    	DataOutputStream dos = new DataOutputStream(out);
    	byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
    	response200Header(dos, body.length);
		responseBody(dos, body);
    }
    
    /**
     * 요청헤더에 Content-Length 속성값을 가져오는 함수
     * POST로 요청시 body 데이터의 길이를 가져오는 기능
     */
    private int getContentLength(String line) {
    	String[] headerTokens = line.split(":");
    	return Integer.parseInt(headerTokens[1].trim());
    }

    /**
     * 로그인 시 로그인 정보를 유지하기 위해 cookie정보를 응답데이터에 같이 보낸다.
     * */
    private void response302HeaderWithCookie(DataOutputStream dos, String cookie) {
    	try {
    		dos.writeBytes("HTTP/1.1 302 Redirect\r\n");
    		dos.writeBytes("Location: /index.html\r\n");
    		dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
    		dos.writeBytes("\r\n");
    	} catch (IOException e) {
    		log.error(e.getMessage());
    	}
    }
    
    /**
     * 회원가입 후 클라이언트에게 302코드를 리턴하고 클라이언트는 로케이션정보를 이용하여 다시 서버에 재요청을 보낸다. 서버에서는 다시 해당 로케이션으로 응답을 보낸다.
     * redirect기능이 이런식으로 작동한다.
     * */
    private void response302Header(DataOutputStream dos, String url) {
        try {
        	//응답코드 302의 경우 바로 해당 url로 이동하기 때문에 url정보만 가지고 있고  body부분을 입력하지 않아도 된다.
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            //dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + url + " \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
        	//writeBytes String 단위로 출력가능
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
