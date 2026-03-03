package com.viacheslav.taskmanager.service.impl;

import com.viacheslav.taskmanager.repository.TagRepository;
import com.viacheslav.taskmanager.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
}
