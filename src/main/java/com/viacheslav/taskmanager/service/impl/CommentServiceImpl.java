package com.viacheslav.taskmanager.service.impl;

import com.viacheslav.taskmanager.repository.CommentRepository;
import com.viacheslav.taskmanager.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
}
