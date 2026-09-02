package com.viacheslav.taskmanager.mapper;

import com.viacheslav.taskmanager.model.UserAccount;
import com.viacheslav.taskmanager.model.dto.user.UserResponse;
import com.viacheslav.taskmanager.model.dto.user.UserUpdateRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserResponse toUserResponse(UserAccount userAccount);

    void updateUserAccountFromDto(UserUpdateRequest request, @MappingTarget UserAccount userAccount);

    List<UserResponse> toUserListResponse(List<UserAccount> userAccounts);
}
