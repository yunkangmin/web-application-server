package util;

import java.io.BufferedReader;
import java.io.IOException;

//요청본문을 추출하기 위한 클래스
public class IOUtils {
    /**
     * @param BufferedReader는
     *            Request Body를 시작하는 시점이어야
     * @param contentLength는
     *            Request Header의 Content-Length 값이다.
     * @return
     * @throws IOException
     */
    public static String readData(BufferedReader br, int contentLength) throws IOException {
    	//contentLength까지 요청본문에 있는 데이터를 담을 배열을 생성한다.
    	char[] body = new char[contentLength];
    	//body 배열에 요청본문의 데이터를 담는다.
        br.read(body, 0, contentLength);
        //배열에 담긴 데이터의 자료형을 String으로 변환한다.
        return String.copyValueOf(body);
    }
}
