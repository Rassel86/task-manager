package com.viacheslav.taskmanager.mapper;

import com.viacheslav.taskmanager.dto.user.UserResponse;
import com.viacheslav.taskmanager.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserResponse toUserResponse(User user);
}
