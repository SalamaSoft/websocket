package com.salama.service.websocket.core.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.salama.service.core.net.RequestWrapper;
import com.salama.service.core.net.SessionWrapper;

public class WsRequestWrapper implements RequestWrapper {
    private final WsSessionWrapper _session;
    private final Map<String, String> _paramMap;
    
    private Map<String, String[]> _paramMapWrapper = null;
    private final WsServletRequest _servletRequest;
    
    private final ConcurrentHashMap<String, Object> _attrMap = new ConcurrentHashMap<>();
    
    public WsRequestWrapper(
            WsSessionWrapper session,
            Map<String, String> paramMap
            ) {
        _session = session;
        _paramMap = paramMap;
        _servletRequest = new WsServletRequest();
    }

    @Override
    public ServletRequest getRequest() {
        if(_paramMapWrapper == null) {
            synchronized (_servletRequest) {
                if(_paramMapWrapper == null) {
                    Set<Entry<String, String>> entrySet = _paramMap.entrySet();
                    
                    _paramMapWrapper = new HashMap<>();
                    for(Entry<String, String> entry : entrySet) {
                        _paramMapWrapper.put(entry.getKey(), new String[] {entry.getValue()});
                    }
                }
            }
        }
        
        return _servletRequest;
    }

    @Override
    public boolean isMultipartContent() {
        return false;
    }

    @Override
    public SessionWrapper getSession() {
        return _session;
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
    public String getCharacterEncoding() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getContentLength() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getContentType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getLocalAddr() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Locale getLocale() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getLocalName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getLocalPort() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getParameter(String name) {
        return _paramMap.get(name);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return new Enumeration<String>() {
            private Iterator<String> _iter = _paramMap.keySet().iterator();

            @Override
            public boolean hasMoreElements() {
                return _iter.hasNext();
            }

            @Override
            public String nextElement() {
                return _iter.next();
            }
        };
    }

    @Override
    public String getProtocol() {
        return _session.getProtocol();
    }

    @Override
    public String getRemoteAddr() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getRemoteHost() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getRemotePort() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getScheme() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getServerName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getServerPort() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return _session.getServletContext();
    }

    @Override
    public boolean isSecure() {
        return _session.isSecure();
    }

    @Override
    public void removeAttribute(String name) {
        _attrMap.remove(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        _attrMap.put(name, value);
    }

    @Override
    public void setCharacterEncoding(String encoding) throws UnsupportedEncodingException {
        // TODO Auto-generated method stub
    }

    private class WsServletRequest implements ServletRequest {

        @Override
        public AsyncContext getAsyncContext() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Object getAttribute(String name) {
            // TODO Auto-generated method stub
            return _attrMap.get(name);
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return _attrMap.keys();
        }

        @Override
        public String getCharacterEncoding() {
            return WsRequestWrapper.this.getCharacterEncoding();
        }

        @Override
        public int getContentLength() {
            return WsRequestWrapper.this.getContentLength();
        }

        @Override
        public long getContentLengthLong() {
            return WsRequestWrapper.this.getContentLength();
        }

        @Override
        public String getContentType() {
            return WsRequestWrapper.this.getContentType();
        }

        @Override
        public DispatcherType getDispatcherType() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getLocalAddr() {
            return WsRequestWrapper.this.getLocalAddr();
        }

        @Override
        public String getLocalName() {
            return WsRequestWrapper.this.getLocalName();
        }

        @Override
        public int getLocalPort() {
            return WsRequestWrapper.this.getLocalPort();
        }

        @Override
        public Locale getLocale() {
            return WsRequestWrapper.this.getLocale();
        }

        @Override
        public Enumeration<Locale> getLocales() {
            return null;
        }

        @Override
        public String getParameter(String name) {
            return _paramMap.get(name);
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return _paramMapWrapper;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return WsRequestWrapper.this.getParameterNames();
        }

        @Override
        public String[] getParameterValues(String name) {
            return _paramMapWrapper.get(name);
        }

        @Override
        public String getProtocol() {
            return WsRequestWrapper.this.getProtocol();
        }

        @Override
        public BufferedReader getReader() throws IOException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getRealPath(String virtualPath) {
            return _session.getServletContext().getRealPath(virtualPath);
        }

        @Override
        public String getRemoteAddr() {
            return WsRequestWrapper.this.getRemoteAddr();
        }

        @Override
        public String getRemoteHost() {
            return WsRequestWrapper.this.getRemoteHost();
        }

        @Override
        public int getRemotePort() {
            return WsRequestWrapper.this.getRemotePort();
        }

        @Override
        public RequestDispatcher getRequestDispatcher(String uri) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getScheme() {
            return WsRequestWrapper.this.getScheme();
        }

        @Override
        public String getServerName() {
            return WsRequestWrapper.this.getServerName();
        }

        @Override
        public int getServerPort() {
            return WsRequestWrapper.this.getServerPort();
        }

        @Override
        public ServletContext getServletContext() {
            return _session.getServletContext();
        }

        @Override
        public boolean isAsyncStarted() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isAsyncSupported() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isSecure() {
            // TODO Auto-generated method stub
            return WsRequestWrapper.this.isSecure();
        }

        @Override
        public void removeAttribute(String name) {
            _attrMap.remove(name);
        }

        @Override
        public void setAttribute(String name, Object value) {
            _attrMap.put(name, value);
        }

        @Override
        public void setCharacterEncoding(String encoding) throws UnsupportedEncodingException {
            WsRequestWrapper.this.setCharacterEncoding(encoding);
        }

        @Override
        public AsyncContext startAsync() throws IllegalStateException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1) throws IllegalStateException {
            // TODO Auto-generated method stub
            return null;
        }
        
    }
}
