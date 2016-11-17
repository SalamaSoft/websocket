package com.salama.service.websocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.apache.log4j.Logger;

public abstract class AbstractTextWebSocket extends Endpoint {
    private final static Logger logger = Logger.getLogger(AbstractTextWebSocket.class);
    
    protected Session _session;

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        _session = session;
        _session.addMessageHandler(new TextMsgHandler());

        logger.debug("session[" + session.getId() + "] onOpen() done");
        
        if(logger.isDebugEnabled()) {
            printSessionInfo(session, config);
        }
    }
    

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        super.onClose(session, closeReason);
        
        logger.debug("session[" + session.getId() + "] onClose() done");
    }
    
    @Override
    public void onError(Session session, Throwable err) {
        super.onError(session, err);
        
        if(err.getCause() != null && err.getCause().getMessage().equals("Broken pipe")) {
            logger.info("session[" + session.getId() + "] client broken");
        } else {
            logger.error("session[" + session.getId() + "] onError()", err);
        }
    }
    
    
    protected abstract void onMsg(String msg);
    
    protected void sendText(String text) {
        _session.getAsyncRemote().sendText(text);
    }
    
    private class TextMsgHandler implements MessageHandler.Partial<String> {
        private List<String> _msgList = new ArrayList<String>();

        @Override
        public void onMessage(String msg, boolean last) {
//            if(logger.isDebugEnabled()) {
//                logger.debug("session[" + _session.getId() + "] onMessage()" + " length:" + msg.length() + " last:" + last);
//            }
            
            _msgList.add(msg);
            
            if(last) {
                StringBuilder wholeMsg = new StringBuilder();
                for(String s : _msgList) {
                    wholeMsg.append(s);
                }
                _msgList.clear();
                
                try {
                    onMsg(wholeMsg.toString());
                } catch (Throwable e) {
                    logger.error(null, e);
                }
            }
        }
    }
    
    private static void printSessionInfo(Session session, EndpointConfig config) {
        logger.debug(
                "session[" + session.getId() + "]"
                + "\n QueryString:" + session.getQueryString()
                + "\n MaxBinaryMessageBufferSize:" + session.getMaxBinaryMessageBufferSize()
                + "\n MaxTextMessageBufferSize:" + session.getMaxTextMessageBufferSize()
                + "\n MaxIdleTimeout:" + session.getMaxIdleTimeout()
                );
        
        for(Entry<String, String> entry : session.getPathParameters().entrySet()) {
            logger.debug(
                    "PathParameters["+ entry.getKey() + "]"
                    + " -> [" + entry.getValue() + "]"
                    );
        }
        
        for(Entry<String, List<String>> entry : session.getRequestParameterMap().entrySet()) {
            logger.debug(
                    "RequestParameterMap["+ entry.getKey() + "]"
                    + " -> [" + toCommaStr(entry.getValue()) + "]"
                    );
        }
        
        logger.debug("onOpen() config.UserProperties ---->");
        for(Entry<String, Object> entry : config.getUserProperties().entrySet()) {
            logger.debug(
                    "UserProperties[" + entry.getKey() + "]" 
                    + " -> [" + entry.getValue() + "]"
                    );
        }
    }
    
    private static String toCommaStr(List<String> strList) {
        StringBuilder commaStr = new StringBuilder();
        
        for(String str : strList) {
            if(commaStr.length() > 0) {
                commaStr.append(",");
            }
            commaStr.append(str);
        }
        
        return commaStr.toString();
    }
}
