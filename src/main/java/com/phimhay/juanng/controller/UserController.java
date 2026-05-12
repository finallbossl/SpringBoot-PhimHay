package com.phimhay.juanng.controller;

import com.phimhay.juanng.dto.request.UserCreationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.phimhay.juanng.service.UserService;
import com.phimhay.juanng.entity.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class UserController {

    @Autowired
    private UserService userService;




    @PostMapping("/create")
     User createUser(@RequestBody UserCreationRequest request) {
      return userService.createRequest(request);

    }

}
