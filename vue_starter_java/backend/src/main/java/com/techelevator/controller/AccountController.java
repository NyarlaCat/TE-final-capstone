package com.techelevator.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.authentication.AuthProvider;
import com.techelevator.authentication.JwtTokenHandler;
import com.techelevator.authentication.UnauthorizedException;
import com.techelevator.authentication.UserCreationException;
import com.techelevator.controller.response.Response;
import com.techelevator.controller.response.ResponseError;
import com.techelevator.controller.response.ValidationError;
import com.techelevator.model.pojo.User;

/**
 * AccountController
 */
@RestController
@CrossOrigin
@RequestMapping("/api")
public class AccountController {
    @Autowired
    private AuthProvider auth;

    @Autowired
    private JwtTokenHandler tokenHandler;

    @PostMapping(path = "/user/login")
    public Response<?> login(@RequestBody User user,
    		HttpServletResponse response) throws UnauthorizedException {
        if (auth.signIn(user.getUsername(), user.getPassword())) {
            User currentUser = auth.getCurrentUser();
            String token = tokenHandler.createToken(currentUser.getUsername(), currentUser.getRole(), currentUser.getEmail());
            return new Response<>("token", token);
        } else {
        	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return new Response<>(new ResponseError("Invalid Login"));
        }
    }

    @PostMapping(path = "/user/register")
    public Response<?> register(@Valid @RequestBody User user, BindingResult result, HttpServletResponse response) throws UserCreationException {
        if (result.hasErrors()) {
        	// Form validation failed
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            
            return new Response<>(ValidationError.createList(result));
        }
        
        try {
        	// try to add user
        	auth.register(user.getUsername(), user.getPassword(), user.getRole(), user.getEmail());
        } catch( DuplicateKeyException e ) {
        	// failed to add user: duplicate key
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            
            return new Response<>(ValidationError.createList(e));
        }
        return Response.EMPTY_SUCCESS; // success
    }

    @PutMapping(path="/user/update")
    public void update() {
    	// TODO
    }
}