package com.example.inventory_management.model;

import lombok.Data;

// import java.util.ArrayList;
// import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
// import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data // Lombok will generate getters, setters, toString, equals, hashCode
@Document(collection = "roles")
public class Role {
    @Id
    private String id;
    @Indexed(unique = true)
    private String name; // Name of the role, e.g., "ADMIN", "USER"
    // @DBRef(lazy = true)
    private Role addedby;
    // @DBRef(lazy = true)
    // private List<Role> owned = new ArrayList<>();

}
