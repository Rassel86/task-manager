package com.viacheslav.taskmanager.repository;

import com.viacheslav.taskmanager.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
}
