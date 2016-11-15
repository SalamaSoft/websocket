package salama.test.websocket.service;

import com.salama.service.core.net.RequestWrapper;
import com.salama.service.core.net.ResponseWrapper;

public class EchoService {

    public static String test_1(String param1, String param2) {
        String result = 
                "param1:" + param1 
                + "\n" + "param2:" + param2
                ;
        return result;
    }
    
    public static String test_2(RequestWrapper request, ResponseWrapper response) {
        return test_1(request.getParameter("param1"), request.getParameter("param2"));
    }
    
}
