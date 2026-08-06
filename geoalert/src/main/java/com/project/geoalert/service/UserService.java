package com.project.geoalert.service;

import com.project.geoalert.dto.UserRequest;
import com.project.geoalert.entity.User;
import com.project.geoalert.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(UserRequest request){
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
        return userRepository.save(user);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
}
