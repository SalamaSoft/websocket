package com.salama.service.websocket.core.net;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.websocket.Session;

import com.salama.service.core.net.SessionWrapper;

public class WsSessionWrapper implements SessionWrapper {
    
    private final ServletContext _servletContext;
    private final Session _session;
    private final long _creationTime;
    
    
    private long _lastAccessedTime;
    private int _maxInactiveInterval;
    
    private final ConcurrentHashMap<String, Object> _attrMap = new ConcurrentHashMap<>();
    
    
    public WsSessionWrapper(
            ServletContext servletContext,
            Session session
            ) {
        _servletContext = servletContext;
        _session = session;
        _creationTime = System.currentTimeMillis();
    }

    public String getProtocol() {
        if(_session.isSecure()) {
            return "wss:";
        } else {
            return "ws:";
        }
    }
    
    public void saveAccessedTime() {
        _lastAccessedTime = System.currentTimeMillis();
    }
    
    public boolean isSecure() {
        return _session.isSecure();
    }
    
    @Override
    public long getCreationTime() {
        return _creationTime;
    }

    @Override
    public String getId() {
        return _session.getId();
    }

    @Override
    public long getLastAccessedTime() {
        return _lastAccessedTime;
    }

    @Override
    public ServletContext getServletContext() {
        return _servletContext;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        _maxInactiveInterval = interval;
    }

    @Override
    public int getMaxInactiveInterval() {
        return _maxInactiveInterval;
    }

    @Override
    public Object getAttribute(String name) {
        return _attrMap.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return _attrMap.keys();
    }

    @Override
    public void setAttribute(String name, Object value) {
        _attrMap.put(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        _attrMap.remove(name);
    }

    @Override
    public void invalidate() {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isNew() {
        return false;
    }

}
