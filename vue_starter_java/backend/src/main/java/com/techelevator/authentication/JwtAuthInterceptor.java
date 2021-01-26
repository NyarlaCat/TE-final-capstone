package com.techelevator.authentication;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techelevator.controller.EventController;
import com.techelevator.controller.response.Response;
import com.techelevator.controller.response.ResponseError;
import com.techelevator.model.pojo.User;

import io.jsonwebtoken.ExpiredJwtException;

/**
 * JwtAuthInterceptor
 */
public class JwtAuthInterceptor implements HandlerInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private List<String> excludedUrls;

    @Autowired
    private JwtTokenHandler tokenHandler;

    public JwtAuthInterceptor() {
    }

    public JwtAuthInterceptor(List<String> excludedUrls) {
        this.excludedUrls = excludedUrls;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException, ServletException {
        if (excludedUrls.contains(request.getRequestURI().replaceFirst(request.getContextPath(), ""))
                || request.getMethod().equals("OPTIONS")) {
            return true;
        }
        
        User authedUser = null;
        try {
        	authedUser = tokenHandler.getUser(request.getHeader(AUTHORIZATION_HEADER));
        } catch( ExpiredJwtException e) {
        	ObjectMapper mapper = new ObjectMapper();
        	
        	response.setContentType("application/json");
        	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        	Response<ResponseError> responseJSON = new Response<>(new ResponseError("Expired Token"));
        	response.getWriter().write(mapper.writeValueAsString(responseJSON));
        	
        	return false;
        }
        if (authedUser == null) {
        	ObjectMapper mapper = new ObjectMapper();
        	
        	response.setContentType("application/json");
        	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Response<ResponseError> responseJSON = new Response<>(new ResponseError("Missing or invalid Authorization header."));
            response.getWriter().write(mapper.writeValueAsString(responseJSON));
            
            return false;
        } else {
            request.setAttribute(RequestAuthProvider.USER_KEY, authedUser);
            return true;
        }
    }

    /**
     * @param excludedUrls the excluded urls to set
     */
    public void setExcludedUrls(List<String> excludedUrls) {
        this.excludedUrls = excludedUrls;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

    }

}