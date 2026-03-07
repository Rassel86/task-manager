package com.viacheslav.taskmanager.service;

import com.viacheslav.taskmanager.dto.project.ProjectCreateRequest;
import com.viacheslav.taskmanager.dto.project.ProjectResponse;
import com.viacheslav.taskmanager.dto.project.ProjectUpdateRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface ProjectService {

    List<ProjectResponse> getUserProjects(UUID userId);

    ProjectResponse createProject(ProjectCreateRequest request);

    ProjectResponse updateProject(UUID id, ProjectUpdateRequest request);

    ProjectResponse patchProject(UUID id, ProjectUpdateRequest request);

    void deleteProject(UUID id);
}
