package com.viacheslav.taskmanager.repository;

import com.viacheslav.taskmanager.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {
}
