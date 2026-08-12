package com.viacheslav.taskmanager.repository;

import com.viacheslav.taskmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdNot(String username, UUID id);

    boolean existsByEmailAndIdNot(String email, UUID id);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    @Query(value = """
    SELECT * FROM users WHERE username = :login
    UNION ALL
    SELECT * FROM users WHERE email = :login
    LIMIT 1
    """, nativeQuery = true)
    Optional<User> findByUsernameOrEmail(@Param("login") String login);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE LOWER(u.username) = LOWER(:username)")
    boolean existsByUsernameIgnoreCase(@Param("username") String username);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    boolean existsByEmailIgnoreCase(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE LOWER(u.email) = lower(:email) ")
    Optional<User> findByEmailIgnoreCase(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE LOWER(u.username) = lower(:username) ")
    Optional<User> findByUsernameIgnoreCase(@Param("username") String username);
}
