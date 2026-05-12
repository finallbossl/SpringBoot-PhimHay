package com.phimhay.juanng.service;


import com.phimhay.juanng.dto.request.UserCreationRequest;
import com.phimhay.juanng.entity.User;
import com.phimhay.juanng.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User createRequest(UserCreationRequest request){
        User  user = new User();

        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());

        return userRepository.save(user);

    }
}
