package com.salama.service.websocket.core.net;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;

import com.salama.service.core.net.RequestWrapper;
import com.salama.service.core.net.ResponseWrapper;

public class WsResponseWrapper implements ResponseWrapper {

    @Override
    public ServletResponse getResponse() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void flushBuffer() throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getBufferSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Locale getLocale() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getContentType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isCommitted() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void resetBuffer() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setBufferSize(int size) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setContentType(String type) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setContentLength(int len) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setCharacterEncoding(String charset) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getCharacterEncoding() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setLocale(Locale loc) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void writeFile(File srcFile) throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addHeader(String name, String value) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setHeader(String name, String value) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setDateHeader(String name, long date) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addDateHeader(String name, long date) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getHeader(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<String> getHeaders(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getStatus() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setStatus(int sc) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void sendError(int sc) throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setDownloadFileName(String fileName, String fileNameEncoding) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setDownloadFileName(RequestWrapper request, ResponseWrapper response, String fileName) {
        // TODO Auto-generated method stub
        
    }

}
