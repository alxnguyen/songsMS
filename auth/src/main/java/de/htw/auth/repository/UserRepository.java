package de.htw.auth.repository;

import de.htw.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUserIdAndPassword(String userId, String password);
}
