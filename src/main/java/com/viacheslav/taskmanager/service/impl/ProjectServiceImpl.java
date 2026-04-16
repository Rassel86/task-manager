package com.viacheslav.taskmanager.service.impl;

import com.viacheslav.taskmanager.exception.AccessDeniedException;
import com.viacheslav.taskmanager.exception.ResourceAlreadyExistsException;
import com.viacheslav.taskmanager.exception.ResourceNotFoundException;
import com.viacheslav.taskmanager.mapper.ProjectMapper;
import com.viacheslav.taskmanager.model.Project;
import com.viacheslav.taskmanager.model.User;
import com.viacheslav.taskmanager.model.dto.project.ProjectCreateRequest;
import com.viacheslav.taskmanager.model.dto.project.ProjectPatchRequest;
import com.viacheslav.taskmanager.model.dto.project.ProjectResponse;
import com.viacheslav.taskmanager.model.dto.project.ProjectUpdateRequest;
import com.viacheslav.taskmanager.repository.ProjectRepository;
import com.viacheslav.taskmanager.service.CurrentUserService;
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

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final CurrentUserService currentUserService;

    @Override
    public List<ProjectResponse> getUserProjects(UUID userId) {
        List<Project> projects = projectRepository.findByOwnerId(userId);
        return projectMapper.toProjectResponseList(projects);
    }

    @Override
    @Transactional
    public ProjectResponse createProject(ProjectCreateRequest request) {
        User user = currentUserService.getCurrentUser();

        if (projectRepository.existsByNameAndOwnerId(request.name(), user.getId())) {
            throw new ResourceAlreadyExistsException(
                    String.format("Project with name \"%s\" already exists", request.name())
            );
        }

        Project project = Project.builder()
                .owner(user)
                .name(request.name())
                .description(request.description())
                .build();

        Project savedProject = projectRepository.save(project);
        return projectMapper.toProjectResponse(savedProject);
    }

    @Override
    @Transactional
    public ProjectResponse updateProject(UUID id, ProjectUpdateRequest request) {
        UUID currentUserId = currentUserService.getCurrentUserId();

        Project project = findProjectById(id);

        log.info("BEFORE - updatedAt: {}", project.getUpdatedAt());

        if (!currentUserId.equals(project.getOwner().getId())) {
            throw new AccessDeniedException("You don't have permission to modify this project");
        }

        project.setName(request.name());
        project.setDescription(request.description());

        projectRepository.flush();
        log.info("AFTER FLUSH - updatedAt: {}", project.getUpdatedAt());
        return projectMapper.toProjectResponse(project);
    }

    @Override
    @Transactional
    public ProjectResponse patchProject(UUID id, ProjectPatchRequest request) {
        UUID currentUserId = currentUserService.getCurrentUserId();

        Project project = findProjectById(id);

        log.info("BEFORE - updatedAt: {}", project.getUpdatedAt());

        if (!currentUserId.equals(project.getOwner().getId())) {
            throw new AccessDeniedException("You don't have permission to modify this project");
        }

        if (request.name() != null) {
            project.setName(request.name());
        }

        if (request.description() != null) {
            project.setDescription(request.description());
        }

        projectRepository.flush();

        log.info("AFTER FLUSH - updatedAt: {}", project.getUpdatedAt());
        return projectMapper.toProjectResponse(project);
    }

    @Override
    @Transactional
    public void deleteProject(UUID id) {
        Project project = findProjectById(id);
        projectRepository.delete(project);
    }

    private Project findProjectById(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Project with id=%s not found", id)
                ));
    }

    private void checkProjectOwnership(Project project) {
        UUID currentUserId = currentUserService.getCurrentUserId();
        if (!currentUserId.equals(project.getOwner().getId())) {
            throw new AccessDeniedException("You don't have permission to modify this project");
        }
    }
}
