package com.viacheslav.taskmanager.repository;

import com.viacheslav.taskmanager.model.Credentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CredentialsRepository extends JpaRepository<Credentials, UUID> {
    Optional<Credentials> findByLogin(String login);
}
