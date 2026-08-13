package com.viacheslav.taskmanager.mapper;

import com.viacheslav.taskmanager.model.UserAccount;
import com.viacheslav.taskmanager.model.dto.user.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserResponse toUserResponse(UserAccount userAccount);

    List<UserResponse> toUserListResponse(List<UserAccount> userAccounts);
}
