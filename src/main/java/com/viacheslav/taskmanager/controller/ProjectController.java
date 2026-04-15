package com.viacheslav.taskmanager.controller;

import com.viacheslav.taskmanager.model.dto.project.ProjectCreateRequest;
import com.viacheslav.taskmanager.model.dto.project.ProjectPatchRequest;
import com.viacheslav.taskmanager.model.dto.project.ProjectResponse;
import com.viacheslav.taskmanager.model.dto.project.ProjectUpdateRequest;
import com.viacheslav.taskmanager.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<ProjectResponse>> getProjects(@PathVariable UUID userId) {
        List<ProjectResponse> responses = projectService.getUserProjects(userId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody ProjectCreateRequest request) {

        ProjectResponse response = projectService.createProject(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity
                .created(location)
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable UUID id,
                                                         @RequestBody ProjectUpdateRequest request) {

        ProjectResponse response = projectService.updateProject(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectResponse> patchProject(@PathVariable UUID id,
                                                        @RequestBody ProjectPatchRequest request) {
        ProjectResponse response = projectService.patchProject(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
        return ResponseEntity
                .noContent()
                .build();
    }
}
