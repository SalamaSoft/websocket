package com.salama.service.websocket;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.salama.service.clouddata.core.ICloudDataService;
import com.salama.service.clouddata.core.ICloudDataServiceContext;
import com.salama.service.core.context.CommonContext;
import com.salama.service.core.context.ServiceContext;
import com.salama.service.websocket.core.CloudDataMsgDecoder;
import com.salama.service.websocket.core.CloudDataMsgDecoder.CommonFieldNames;
import com.salama.service.websocket.core.net.WsRequestWrapper;
import com.salama.service.websocket.core.net.WsResponseWrapper;
import com.salama.service.websocket.core.net.WsSessionWrapper;

public class WebSocketContext implements CommonContext {
    private final static Logger logger = Logger.getLogger(WebSocketContext.class);

    private static final long serialVersionUID = 5236021315722193707L;
    
    public final static String URI_ServerEndpoint = "/cloudDataService.socket";
    
    protected static WebSocketContext _singleton = null;

    
    protected ICloudDataService _cloudDataService = null;
    private ServletContext _servletContext;
    
    @Override
    public void reload(ServletContext servletContext, String configLocation) {
        logger.debug(
                "reload. configLocation:" + configLocation
                + " URI:" + URI_ServerEndpoint 
                );
        
        _servletContext = servletContext;
        _singleton = this;
        
        //Init CloudDataService -----------------------------
        try {
            ICloudDataServiceContext cloudDataServiceContext = (ICloudDataServiceContext) ServiceContext
                    .getContext(servletContext)
                    .getContext(Class.forName("com.salama.service.clouddata.CloudDataServiceContext"));
            
            _cloudDataService = cloudDataServiceContext.createCloudDataService();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {
    }
    
    
    @ServerEndpoint(value = URI_ServerEndpoint)
    public static class CloudDataEndpoint extends AbstractTextWebSocket {
        private WsSessionWrapper _sessionWrapper;
        
        @Override
        public void onOpen(Session session, EndpointConfig config) {
            super.onOpen(session, config);
            
            _sessionWrapper = new WsSessionWrapper(_singleton._servletContext, _session);
        }

        @Override
        protected void onMsg(String msg) {
            String result = null;
            String requestId = null; 
            try {
                //decode msg ------
                Map<String, String> paramMap = CloudDataMsgDecoder.decodeRequestMsg(msg);
                requestId = paramMap.get(CommonFieldNames.RequestId); 

                
                //invoke cloudDataService -----
                WsRequestWrapper request = new WsRequestWrapper(_sessionWrapper, paramMap);
                WsResponseWrapper response = new WsResponseWrapper();
                
                result = _singleton._cloudDataService.cloudDataService(
                        paramMap.get(CommonFieldNames.ServiceType), 
                        paramMap.get(CommonFieldNames.ServiceMethod), 
                        request, 
                        response
                        );
            } catch (Throwable e) {
                logger.error(null, e);
            }
            
            try {
                //send response -----
                JSONObject responseJson = CloudDataMsgDecoder.encodeResponseMsg(requestId, result);
                sendText(responseJson.toString());
            } catch (Throwable e) {
                logger.error(null, e);
            }
        }
        
    } 
    
}
