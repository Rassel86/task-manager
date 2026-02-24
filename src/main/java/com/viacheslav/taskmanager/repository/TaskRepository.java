package com.viacheslav.taskmanager.repository;

import com.viacheslav.taskmanager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
}
