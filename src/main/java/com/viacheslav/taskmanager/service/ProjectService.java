package com.viacheslav.taskmanager.service;

import com.viacheslav.taskmanager.dto.ProjectCreateRequest;
import com.viacheslav.taskmanager.dto.ProjectResponse;
import com.viacheslav.taskmanager.dto.ProjectUpdateRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface ProjectService {

    List<ProjectResponse> getUserProjects(UUID userId);

    ProjectResponse createProject(UUID userId, ProjectCreateRequest request);

    ProjectResponse updateProject(UUID userId, UUID projectId, ProjectUpdateRequest request);

    ProjectResponse patchProject(UUID userId, UUID projectId, ProjectUpdateRequest request);

    void deleteProject(UUID id);
}
