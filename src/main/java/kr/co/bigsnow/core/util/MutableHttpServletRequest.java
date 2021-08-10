package kr.co.bigsnow.core.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.ContentCachingRequestWrapper;

public class MutableHttpServletRequest extends ContentCachingRequestWrapper {

    private final Map<String, String> customHeaders;

    /**
     * Constructs a request object wrapping the given request.
     * @throws IllegalArgumentException if the request is null
     * @param request the {@link HttpServletRequest} to be wrapped.
     */
    public MutableHttpServletRequest(HttpServletRequest request) {
        super(request);
        this.customHeaders = new HashMap<>();
    }

    public void putHeader(String name, String value){
        this.customHeaders.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        String headerValue = customHeaders.get(name);

        if (headerValue != null){
            return headerValue;
        }

        return ((HttpServletRequest) getRequest()).getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> set = new HashSet<>(customHeaders.keySet());

        Enumeration<String> e = ((HttpServletRequest) getRequest()).getHeaderNames();
        while (e.hasMoreElements()) {
            String n = e.nextElement();
            set.add(n);
        }

        return Collections.enumeration(set);
    }
}
