package com.viacheslav.taskmanager.repository;

import com.viacheslav.taskmanager.model.Credentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CredentialsRepository extends JpaRepository<Credentials, UUID> {
    @Query("SELECT c FROM Credentials c JOIN fetch c.userAccount where c.login = :login")
    Optional<Credentials> findByLogin(@Param("login") String login);
    boolean existsByLogin(String login);
}
