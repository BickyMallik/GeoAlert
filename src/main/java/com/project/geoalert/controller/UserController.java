package com.project.geoalert.controller;

import com.project.geoalert.dto.UserRequest;
import com.project.geoalert.entity.Alert;
import com.project.geoalert.entity.User;
import com.project.geoalert.service.AlertService;
import com.project.geoalert.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public User createUser(@RequestBody UserRequest request){
        return userService.createUser(request);
    }

    @GetMapping
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

}
