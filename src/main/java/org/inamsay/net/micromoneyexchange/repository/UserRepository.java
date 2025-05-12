package org.inamsay.net.micromoneyexchange.repository;

import org.inamsay.net.micromoneyexchange.entity.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;


/** * Repository interface for User entity.
 * This interface extends R2dbcRepository to provide CRUD operations
 * and custom query methods for User entities.
 */
public interface UserRepository extends R2dbcRepository<User, Long> {

    Mono<User> findByUsername(String username);

}