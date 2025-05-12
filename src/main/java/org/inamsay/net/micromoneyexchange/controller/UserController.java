package org.inamsay.net.micromoneyexchange.controller;

import org.inamsay.net.micromoneyexchange.entity.User;
import org.inamsay.net.micromoneyexchange.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

     @GetMapping
     public Flux<User> getAllUsers() {
         Flux<User> lstUser= userRepository.findAll();
         return lstUser;
     }

}
