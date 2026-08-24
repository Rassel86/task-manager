package com.viacheslav.taskmanager.mapper;

import com.viacheslav.taskmanager.model.dto.auth.RegisterRequest;
import com.viacheslav.taskmanager.model.dto.user.UserCreateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthMapper {

    @Mapping(target = "password", source = "encodedPassword")
    UserCreateDto toUserCreateRequest(RegisterRequest registerRequest, String encodedPassword);
}
