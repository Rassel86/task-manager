package com.viacheslav.taskmanager.repository;

import com.viacheslav.taskmanager.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
}
