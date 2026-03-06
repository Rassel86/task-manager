package com.viacheslav.taskmanager.service.impl;

import com.viacheslav.taskmanager.dto.ProjectCreateRequest;
import com.viacheslav.taskmanager.dto.ProjectResponse;
import com.viacheslav.taskmanager.dto.ProjectUpdateRequest;
import com.viacheslav.taskmanager.entity.Project;
import com.viacheslav.taskmanager.entity.User;
import com.viacheslav.taskmanager.mapper.ProjectMapper;
import com.viacheslav.taskmanager.repository.ProjectRepository;
import com.viacheslav.taskmanager.service.CurrentUserService;
import com.viacheslav.taskmanager.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final CurrentUserService currentUserService;

    @Override
    public List<ProjectResponse> getUserProjects(UUID userId) {
        List<Project> projects = projectRepository.findByOwnerId(userId);
        return projectMapper.toProjectResponseList(projects);
    }

    @Override
    public ProjectResponse createProject(UUID userId, ProjectCreateRequest request) {
        User user = currentUserService.getCurrentUser();
        Project project = Project.builder()
                .owner(user)
                .name(request.name())
                .description(request.description())
                .build();

        Project savedProject = projectRepository.save(project);
        return projectMapper.toProjectResponse(savedProject);
    }

    @Override
    public ProjectResponse updateProject(UUID userId, UUID projectId, ProjectUpdateRequest request) {
        return null;
    }

    @Override
    public ProjectResponse patchProject(UUID userId, UUID projectId, ProjectUpdateRequest request) {
        return null;
    }

    @Override
    public void deleteProject(UUID id) {

    }
}
