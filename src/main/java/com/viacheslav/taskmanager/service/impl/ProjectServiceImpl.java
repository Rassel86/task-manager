package com.viacheslav.taskmanager.service.impl;

import com.viacheslav.taskmanager.dto.ProjectCreateRequest;
import com.viacheslav.taskmanager.dto.ProjectResponse;
import com.viacheslav.taskmanager.dto.ProjectUpdateRequest;
import com.viacheslav.taskmanager.repository.ProjectRepository;
import com.viacheslav.taskmanager.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
}
