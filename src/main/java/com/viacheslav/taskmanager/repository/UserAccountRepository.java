package com.viacheslav.taskmanager.repository;

import com.viacheslav.taskmanager.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID>, JpaSpecificationExecutor<UserAccount> {

    @Query(value = """
    SELECT * FROM user_accounts WHERE username = :login
    UNION ALL
    SELECT * FROM user_accounts WHERE email = :login
    LIMIT 1
    """, nativeQuery = true)
    Optional<UserAccount> findByUsernameOrEmail(@Param("login") String login);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserAccount u WHERE LOWER(u.username) = LOWER(:username)")
    boolean existsByUsernameIgnoreCase(@Param("username") String username);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserAccount u WHERE LOWER(u.email) = LOWER(:email)")
    boolean existsByEmailIgnoreCase(@Param("email") String email);

    @Query("SELECT u FROM UserAccount u WHERE LOWER(u.email) = lower(:email) ")
    Optional<UserAccount> findByEmailIgnoreCase(@Param("email") String email);

    @Query("SELECT u FROM UserAccount u WHERE LOWER(u.username) = lower(:username) ")
    Optional<UserAccount> findByUsernameIgnoreCase(@Param("username") String username);
}
