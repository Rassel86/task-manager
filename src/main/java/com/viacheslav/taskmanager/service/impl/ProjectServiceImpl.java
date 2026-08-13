package com.viacheslav.taskmanager.service.impl;

import com.viacheslav.taskmanager.exception.AccessDeniedException;
import com.viacheslav.taskmanager.exception.ResourceAlreadyExistsException;
import com.viacheslav.taskmanager.exception.ResourceNotFoundException;
import com.viacheslav.taskmanager.mapper.ProjectMapper;
import com.viacheslav.taskmanager.model.Project;
import com.viacheslav.taskmanager.model.UserAccount;
import com.viacheslav.taskmanager.model.dto.project.ProjectCreateRequest;
import com.viacheslav.taskmanager.model.dto.project.ProjectPatchRequest;
import com.viacheslav.taskmanager.model.dto.project.ProjectResponse;
import com.viacheslav.taskmanager.model.dto.project.ProjectUpdateRequest;
import com.viacheslav.taskmanager.repository.ProjectRepository;
import com.viacheslav.taskmanager.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    @Override
    public List<ProjectResponse> getUserProjects(UUID userId) {
        return List.of();
    }

    @Override
    public ProjectResponse createProject(ProjectCreateRequest request) {
        return null;
    }

    @Override
    public ProjectResponse updateProject(UUID id, ProjectUpdateRequest request) {
        return null;
    }

    @Override
    public ProjectResponse patchProject(UUID id, ProjectPatchRequest request) {
        return null;
    }

    @Override
    public void deleteProject(UUID id) {

    }
}
