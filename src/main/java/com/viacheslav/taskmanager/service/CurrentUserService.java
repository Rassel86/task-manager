package com.viacheslav.taskmanager.service;

import com.viacheslav.taskmanager.model.User;

import java.util.UUID;

public interface CurrentUserService {
    User getCurrentUser();
    UUID getCurrentUserId();
}
