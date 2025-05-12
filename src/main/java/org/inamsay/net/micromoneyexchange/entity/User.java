package org.inamsay.net.micromoneyexchange.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Entity class representing a user in the system.
 * Maps to the "users" table in the database.
 */
@Data
@Table("users")
public class User {

    @Id
    private Long Id;

    private String username;
    private String password;

    private String role;
}
