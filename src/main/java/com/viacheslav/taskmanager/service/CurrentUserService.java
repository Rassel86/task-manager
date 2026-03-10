package com.viacheslav.taskmanager.service;

import com.viacheslav.taskmanager.entity.User;

import java.util.UUID;

public interface CurrentUserService {
    User getCurrentUser();
    UUID getCurrentUserId();
}
