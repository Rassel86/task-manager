package com.viacheslav.taskmanager.mapper;

import com.viacheslav.taskmanager.dto.auth.RegisterRequest;
import com.viacheslav.taskmanager.dto.user.UserCreateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthMapper {

    @Mapping(target = "password", source = "encodedPassword")
    UserCreateRequest toUserCreateRequest(RegisterRequest registerRequest, String encodedPassword);
}
