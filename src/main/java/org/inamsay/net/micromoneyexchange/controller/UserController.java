package org.inamsay.net.micromoneyexchange.controller;

import org.inamsay.net.micromoneyexchange.entity.User;
import org.inamsay.net.micromoneyexchange.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * UserController is a REST controller that handles user-related requests.
 * It provides an endpoint to retrieve all users from the database.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    /**
     * Constructor for UserController.
     *
     * @param userRepository the UserRepository to be used for database operations
     */
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all users from the database.
     *
     * @return a Flux of User objects representing all users in the database
     */
    @GetMapping
    public Flux<User> getAllUsers() {
        Flux<User> lstUser = userRepository.findAll();
        return lstUser;
    }

}
