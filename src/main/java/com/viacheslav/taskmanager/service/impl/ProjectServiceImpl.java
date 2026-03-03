package com.viacheslav.taskmanager.service.impl;

import com.viacheslav.taskmanager.repository.ProjectRepository;
import com.viacheslav.taskmanager.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
}
