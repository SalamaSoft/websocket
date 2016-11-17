package com.salama.service.websocket.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class CloudDataMsgDecoder {
    
    public final static class CommonFieldNames {
        public final static String ServiceType = "serviceType";
        public final static String ServiceMethod = "serviceMethod";
        
        public final static String RequestId = "__requestId";
        public final static String Result = "result";
    }
    
    public static Map<String, String> decodeRequestMsg(String msg) throws JSONException {
        JSONObject jsonObj = new JSONObject(msg);
        
        Map<String, String> paramMap = new HashMap<String, String>();
        Iterator<?> iter = jsonObj.keys();
        while(iter.hasNext()) {
            String key = (String) iter.next();
            Object val = jsonObj.opt(key);
            
            if(val != null) {
                paramMap.put(key, val.toString());
            }
        }
        
        return paramMap;
    }

    public static JSONObject encodeResponseMsg(String requestId, String result) throws JSONException {
        JSONObject jsonObj = new JSONObject();
        
        jsonObj.put(CommonFieldNames.RequestId, requestId);
        
        if(result != null) {
            jsonObj.put(CommonFieldNames.Result, result);
        }
        
        return jsonObj;
    }
    
}
