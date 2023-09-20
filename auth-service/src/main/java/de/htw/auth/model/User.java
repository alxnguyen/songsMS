package de.htw.auth.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "users")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String userId;
    private String password;
    private String firstName;
    private String lastName;
}
