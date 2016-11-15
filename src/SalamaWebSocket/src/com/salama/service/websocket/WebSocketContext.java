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
    
    
    public final static class MsgFieldNames {
        public final static String ServiceType = "serviceType";
        public final static String ServiceMethod = "serviceMethod";
        public final static String Data = "data";
    }
    
    @ServerEndpoint(value = URI_ServerEndpoint)
    protected static class CloudDataEndpoint extends AbstractTextWebSocket {
        private WsSessionWrapper _sessionWrapper;
        
        @Override
        public void onOpen(Session session, EndpointConfig config) {
            super.onOpen(session, config);
            
            _sessionWrapper = new WsSessionWrapper(_singleton._servletContext, _session);
        }

        @Override
        protected void onMsg(String msg) {
            String result = null;
            try {
                //decode msg ------
                JSONObject jsonObj = new JSONObject(msg);
                
                String serviceType = jsonObj.getString(MsgFieldNames.ServiceType);
                String serviceMethod = jsonObj.getString(MsgFieldNames.ServiceMethod);
                
                JSONObject data = jsonObj.getJSONObject(MsgFieldNames.Data);

                Map<String, String> paramMap = new HashMap<>();
                if(data != null) {
                    Iterator<?> iter = data.keys();
                    while(iter.hasNext()) {
                        String key = (String) iter.next();
                        paramMap.put(key, data.optString(key));
                    }
                }
                
                //invoke cloudDataService -----
                WsRequestWrapper request = new WsRequestWrapper(_sessionWrapper, paramMap);
                WsResponseWrapper response = new WsResponseWrapper();  
                result = _singleton._cloudDataService.cloudDataService(serviceType, serviceMethod, request, response);
            } catch (Throwable e) {
                logger.error(null, e);
            }
            
            //send response -----
            if(result == null) {
                result = "";
            }
            sendText(result);
        }
        
    } 
    
}
