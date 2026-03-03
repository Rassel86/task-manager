package com.viacheslav.taskmanager.service.impl;

import com.viacheslav.taskmanager.repository.TaskRepository;
import com.viacheslav.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
}
