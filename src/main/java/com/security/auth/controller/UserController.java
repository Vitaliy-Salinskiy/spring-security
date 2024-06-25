package com.security.auth.controller;

import com.security.auth.exception.CustomException;
import com.security.auth.repository.UserRepository;
import com.security.auth.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("")
    public List<User> getAllUsers(){
        try{
            return userRepository.findAll();
        } catch (Exception ex){
            throw new CustomException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id){
        try{
        return userRepository.findById(id)
                .orElseThrow(() -> new CustomException("Error: User is not found.", HttpStatus.NOT_FOUND));
        } catch (CustomException ex){
            throw ex;
        } catch (Exception ex){
            throw new CustomException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
